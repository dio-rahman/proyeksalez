package com.salez.kasir.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salez.kasir.data.models.FinancialReport
import com.salez.kasir.data.models.OrderStatus
import com.salez.kasir.data.models.PopularItemReport
import com.salez.kasir.data.models.SalesReport
import com.salez.kasir.data.repository.OrderRepository
import com.salez.kasir.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ManagerViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState())
    val uiState: StateFlow<ManagerUiState> = _uiState.asStateFlow()

    // Generate sales report
    fun generateSalesReport(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            reportRepository.generateSalesReport(startDate, endDate)
                .onSuccess { report ->
                    _uiState.update {
                        it.copy(
                            salesReport = report,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    // Get popular items
    fun getPopularItems(startDate: Long, endDate: Long, limit: Int = 10) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            reportRepository.getPopularItems(startDate, endDate, limit)
                .onSuccess { report ->
                    _uiState.update {
                        it.copy(
                            popularItemsReport = report,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    // Generate financial report
    fun generateFinancialReport(month: Int, year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            reportRepository.generateFinancialReport(month, year)
                .onSuccess { report ->
                    _uiState.update {
                        it.copy(
                            financialReport = report,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                }
        }
    }

    // Get today's sales data
    fun getTodaySalesSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            orderRepository.getTodayOrders()
                .collect { orders ->
                    val completedOrders = orders.filter { it.status == OrderStatus.COMPLETED }
                    val totalSales = completedOrders.sumOf { it.finalPrice }
                    val orderCount = completedOrders.size

                    _uiState.update {
                        it.copy(
                            todaySales = totalSales,
                            todayOrderCount = orderCount,
                            isLoading = false
                        )
                    }
                }
        }
    }

    // Export report to CSV
    fun exportReportToCsv(report: Any): String {
        return reportRepository.exportReportToCsv(report)
    }

    // Get date for given time period
    fun getDateRangeForPeriod(period: ReportPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        when (period) {
            ReportPeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportPeriod.YESTERDAY -> {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                return Pair(startDate, calendar.timeInMillis)
            }
            ReportPeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportPeriod.LAST_WEEK -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                calendar.add(Calendar.DAY_OF_WEEK, 6)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                return Pair(startDate, calendar.timeInMillis)
            }
            ReportPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportPeriod.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                return Pair(startDate, calendar.timeInMillis)
            }
            ReportPeriod.THIS_YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            ReportPeriod.LAST_YEAR -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis

                calendar.set(Calendar.MONTH, Calendar.DECEMBER)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                return Pair(startDate, calendar.timeInMillis)
            }
            ReportPeriod.CUSTOM -> {
                // For custom, return current time as both start and end to indicate it's not set
                return Pair(endDate, endDate)
            }
        }

        return Pair(calendar.timeInMillis, endDate)
    }
}

enum class ReportPeriod {
    TODAY, YESTERDAY, THIS_WEEK, LAST_WEEK, THIS_MONTH, LAST_MONTH, THIS_YEAR, LAST_YEAR, CUSTOM
}

data class ManagerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val salesReport: SalesReport? = null,
    val popularItemsReport: PopularItemReport? = null,
    val financialReport: FinancialReport? = null,
    val todaySales: Double = 0.0,
    val todayOrderCount: Int = 0
)
