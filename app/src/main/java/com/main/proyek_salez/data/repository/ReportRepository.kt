package com.main.proyek_salez.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.main.proyek_salez.data.models.FinancialReport
import com.main.proyek_salez.data.models.MenuItem
import com.main.proyek_salez.data.models.OrderStatus
import com.main.proyek_salez.data.models.PopularItemReport
import com.main.proyek_salez.data.models.SalesReport
import com.main.proyek_salez.data.models.TopItem
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val orderRepository: OrderRepository
) {
    // Generate sales report
    suspend fun generateSalesReport(startDate: Long, endDate: Long): Result<SalesReport> {
        return try {
            // Get orders within date range
            val orders = orderRepository.getOrdersByDateRange(startDate, endDate)
                .getOrThrow()
                .filter { it.status == OrderStatus.COMPLETED }

            if (orders.isEmpty()) {
                return Result.failure(Exception("No completed orders found in date range"))
            }

            // Calculate total sales
            val totalSales = orders.sumOf { it.totalPrice }
            val totalDiscounts = orders.sumOf { it.discount }
            val netSales = orders.sumOf { it.finalPrice }

            // Count items sold
            val itemsSold = mutableMapOf<String, Int>()
            val categorySales = mutableMapOf<String, Double>()

            orders.forEach { order ->
                order.items.forEach { item ->
                    // Count items
                    val itemId = item.menuItem.itemId
                    itemsSold[itemId] = (itemsSold[itemId] ?: 0) + item.quantity

                    // Sum category sales
                    val category = item.menuItem.category
                    categorySales[category] = (categorySales[category] ?: 0.0) + item.subtotal
                }
            }

            // Create report
            val reportId = firestore.collection("reports").document().id
            val report = SalesReport(
                reportId = reportId,
                startDate = startDate,
                endDate = endDate,
                totalSales = totalSales,
                totalOrders = orders.size,
                totalDiscounts = totalDiscounts,
                netSales = netSales,
                itemsSold = itemsSold,
                categorySales = categorySales
            )

            // Save report
            firestore.collection("reports").document(reportId).set(report).await()

            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get popular items
    suspend fun getPopularItems(startDate: Long, endDate: Long, limit: Int = 10): Result<PopularItemReport> {
        return try {
            // Get orders within date range
            val orders = orderRepository.getOrdersByDateRange(startDate, endDate)
                .getOrThrow()
                .filter { it.status == OrderStatus.COMPLETED }

            if (orders.isEmpty()) {
                return Result.failure(Exception("No completed orders found in date range"))
            }

            // Count item occurrences and total sales
            val itemCounts = mutableMapOf<MenuItem, Int>()
            val itemSales = mutableMapOf<MenuItem, Double>()

            orders.forEach { order ->
                order.items.forEach { item ->
                    itemCounts[item.menuItem] = (itemCounts[item.menuItem] ?: 0) + item.quantity
                    itemSales[item.menuItem] = (itemSales[item.menuItem] ?: 0.0) + item.subtotal
                }
            }

            // Create top items list
            val topItems = itemCounts.keys.map { menuItem ->
                TopItem(
                    menuItem = menuItem,
                    quantity = itemCounts[menuItem] ?: 0,
                    totalSales = itemSales[menuItem] ?: 0.0
                )
            }
                .sortedByDescending { it.quantity }
                .take(limit)

            // Create report
            val reportId = firestore.collection("reports").document().id
            val report = PopularItemReport(
                reportId = reportId,
                startDate = startDate,
                endDate = endDate,
                topItems = topItems
            )

            // Save report
            firestore.collection("reports").document(reportId).set(report).await()

            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Generate financial report
    suspend fun generateFinancialReport(month: Int, year: Int): Result<FinancialReport> {
        return try {
            // Calculate date range for the month
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.timeInMillis

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endDate = calendar.timeInMillis

            // Get orders within date range
            val orders = orderRepository.getOrdersByDateRange(startDate, endDate)
                .getOrThrow()
                .filter { it.status == OrderStatus.COMPLETED }

            if (orders.isEmpty()) {
                return Result.failure(Exception("No completed orders found in selected month"))
            }

            // Calculate totals
            val totalRevenue = orders.sumOf { it.totalPrice }
            val totalDiscounts = orders.sumOf { it.discount }
            val netRevenue = orders.sumOf { it.finalPrice }

            // Group daily revenue
            val dailyRevenue = mutableMapOf<Int, Double>()

            orders.forEach { order ->
                val orderCalendar = Calendar.getInstance()
                orderCalendar.timeInMillis = order.createdAt
                val day = orderCalendar.get(Calendar.DAY_OF_MONTH)

                dailyRevenue[day] = (dailyRevenue[day] ?: 0.0) + order.finalPrice
            }

            // Create report
            val reportId = firestore.collection("reports").document().id
            val report = FinancialReport(
                reportId = reportId,
                month = month,
                year = year,
                totalRevenue = totalRevenue,
                totalDiscounts = totalDiscounts,
                netRevenue = netRevenue,
                dailyRevenue = dailyRevenue
            )

            // Save report
            firestore.collection("reports").document(reportId).set(report).await()

            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Export report to CSV format
    fun exportReportToCsv(report: Any): String {
        return when (report) {
            is SalesReport -> exportSalesReportToCsv(report)
            is PopularItemReport -> exportPopularItemsToCsv(report)
            is FinancialReport -> exportFinancialReportToCsv(report)
            else -> throw IllegalArgumentException("Unsupported report type")
        }
    }

    private fun exportSalesReportToCsv(report: SalesReport): String {
        val startDate = formatDate(report.startDate)
        val endDate = formatDate(report.endDate)

        val sb = StringBuilder()
        sb.appendLine("Sales Report,${startDate} to ${endDate}")
        sb.appendLine("Generated On,${formatDate(report.createdAt)}")
        sb.appendLine()
        sb.appendLine("Total Sales,Rp ${report.totalSales}")
        sb.appendLine("Total Orders,${report.totalOrders}")
        sb.appendLine("Total Discounts,Rp ${report.totalDiscounts}")
        sb.appendLine("Net Sales,Rp ${report.netSales}")
        sb.appendLine()

        sb.appendLine("Category,Sales Amount")
        report.categorySales.forEach { (category, amount) ->
            sb.appendLine("${category},Rp ${amount}")
        }

        return sb.toString()
    }

    private fun exportPopularItemsToCsv(report: PopularItemReport): String {
        val startDate = formatDate(report.startDate)
        val endDate = formatDate(report.endDate)

        val sb = StringBuilder()
        sb.appendLine("Popular Items Report,${startDate} to ${endDate}")
        sb.appendLine("Generated On,${formatDate(report.createdAt)}")
        sb.appendLine()

        sb.appendLine("Rank,Item Name,Category,Quantity,Total Sales")
        report.topItems.forEachIndexed { index, item ->
            sb.appendLine("${index + 1},${item.menuItem.name},${item.menuItem.category},${item.quantity},Rp ${item.totalSales}")
        }

        return sb.toString()
    }

    private fun exportFinancialReportToCsv(report: FinancialReport): String {
        val monthName = getMonthName(report.month)

        val sb = StringBuilder()
        sb.appendLine("Financial Report,${monthName} ${report.year}")
        sb.appendLine("Generated On,${formatDate(report.createdAt)}")
        sb.appendLine()

        sb.appendLine("Total Revenue,Rp ${report.totalRevenue}")
        sb.appendLine("Total Discounts,Rp ${report.totalDiscounts}")
        sb.appendLine("Net Revenue,Rp ${report.netRevenue}")
        sb.appendLine()

        sb.appendLine("Day,Revenue")
        report.dailyRevenue.forEach { (day, amount) ->
            sb.appendLine("${day},Rp ${amount}")
        }

        return sb.toString()
    }

    // Helper functions
    private fun formatDate(timestamp: Long): String {
        val sdf =   SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(calendar.time)
    }
}