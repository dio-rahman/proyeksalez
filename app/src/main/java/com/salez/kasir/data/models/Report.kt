package com.salez.kasir.data.models

data class SalesReport(
    val reportId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val totalSales: Double = 0.0,
    val totalOrders: Int = 0,
    val totalDiscounts: Double = 0.0,
    val netSales: Double = 0.0,
    val itemsSold: Map<String, Int> = emptyMap(), // itemId -> quantity
    val categorySales: Map<String, Double> = emptyMap(), // category -> sales
    val createdAt: Long = System.currentTimeMillis()
)

data class PopularItemReport(
    val reportId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val topItems: List<TopItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class TopItem(
    val menuItem: MenuItem,
    val quantity: Int = 0,
    val totalSales: Double = 0.0
)

data class FinancialReport(
    val reportId: String = "",
    val month: Int = 0,
    val year: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalDiscounts: Double = 0.0,
    val netRevenue: Double = 0.0,
    val dailyRevenue: Map<Int, Double> = emptyMap(), // day -> revenue
    val createdAt: Long = System.currentTimeMillis()
)
