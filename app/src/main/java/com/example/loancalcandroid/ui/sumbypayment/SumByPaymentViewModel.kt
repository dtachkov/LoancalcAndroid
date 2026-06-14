package com.example.loancalcandroid.ui.sumbypayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loancalcandroid.analytics.AnalyticsHelper
import com.example.loancalcandroid.util.Formatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow

data class SumByPaymentRow(
    val rate: Float,
    val term: Int,
    val maxSum: String,
    val overpayment: String,
)

data class SumByPaymentUiState(
    val monthlyPayment: String = "25000",
    val startRate: String = "12.5",
    val endRate: String = "12.5",
    val startTerm: String = "24",
    val endTerm: String = "24",
    val isCalculating: Boolean = false,
    val validationError: String? = null,
    val singleResultAmount: String? = null,
    val singleResultTerm: Int? = null,
    val singleResultRate: Float? = null,
    val tableRows: List<SumByPaymentRow> = emptyList(),
    val showTable: Boolean = false,
)

class SumByPaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SumByPaymentUiState())
    val uiState: StateFlow<SumByPaymentUiState> = _uiState.asStateFlow()

    fun updateMonthlyPayment(value: String) =
        _uiState.update { it.copy(monthlyPayment = value, validationError = null) }

    fun updateStartRate(value: String) =
        _uiState.update { it.copy(startRate = value, validationError = null) }

    fun updateEndRate(value: String) =
        _uiState.update { it.copy(endRate = value, validationError = null) }

    fun updateStartTerm(value: String) =
        _uiState.update { it.copy(startTerm = value, validationError = null) }

    fun updateEndTerm(value: String) =
        _uiState.update { it.copy(endTerm = value, validationError = null) }

    fun calculate(
        rateRangeError: String,
        rateZeroError: String,
        termRangeError: String,
        termZeroError: String,
    ) {
        val monthlyPayment = Formatters.parseMoney(_uiState.value.monthlyPayment).toDouble()
        val startRate = Formatters.parsePercent(_uiState.value.startRate)
        val endRate = Formatters.parsePercent(_uiState.value.endRate)
        val startTerm = Formatters.parseInt(_uiState.value.startTerm)
        val endTerm = Formatters.parseInt(_uiState.value.endTerm)

        val validationError = when {
            startRate > endRate -> rateRangeError
            startRate == 0f || endRate == 0f -> rateZeroError
            startTerm == 0 || endTerm == 0 -> termZeroError
            startTerm > endTerm -> termRangeError
            else -> null
        }
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    validationError = validationError,
                    singleResultAmount = null,
                    singleResultTerm = null,
                    singleResultRate = null,
                    tableRows = emptyList(),
                    showTable = false,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isCalculating = true,
                    validationError = null,
                    singleResultAmount = null,
                    singleResultTerm = null,
                    singleResultRate = null,
                    tableRows = emptyList(),
                    showTable = false,
                )
            }

            val rows = withContext(Dispatchers.Default) {
                buildRows(
                    monthlyPayment = monthlyPayment,
                    startRate = startRate,
                    endRate = endRate,
                    startTerm = startTerm,
                    endTerm = endTerm,
                )
            }

            if (startRate == endRate && startTerm == endTerm) {
                val row = rows.first()
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        singleResultAmount = row.maxSum,
                        singleResultTerm = startTerm,
                        singleResultRate = startRate,
                        showTable = false,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isCalculating = false,
                        tableRows = rows,
                        showTable = true,
                    )
                }
            }
            AnalyticsHelper.logEvent("CALC_SUM_LOAN")
        }
    }

    private fun buildRows(
        monthlyPayment: Double,
        startRate: Float,
        endRate: Float,
        startTerm: Int,
        endTerm: Int,
    ): List<SumByPaymentRow> {
        val rows = ArrayList<SumByPaymentRow>()
        if (startRate == endRate) {
            for (term in startTerm..endTerm) {
                rows += buildRow(monthlyPayment, startRate, term)
            }
            return rows
        }
        if (startTerm == endTerm) {
            for (rate in generateRates(startRate, endRate)) {
                rows += buildRow(monthlyPayment, rate, startTerm)
            }
            return rows
        }
        for (term in startTerm..endTerm) {
            for (rate in generateRates(startRate, endRate)) {
                rows += buildRow(monthlyPayment, rate, term)
            }
        }
        return rows
    }

    private fun buildRow(monthlyPayment: Double, rate: Float, term: Int): SumByPaymentRow {
        val maxSum = calculateMaxLoanAmount(monthlyPayment, rate, term)
        val overpayment = monthlyPayment * term - maxSum
        return SumByPaymentRow(
            rate = rate,
            term = term,
            maxSum = Formatters.moneyWithoutDecimal(maxSum),
            overpayment = Formatters.moneyWithoutDecimal(overpayment),
        )
    }

    private fun calculateMaxLoanAmount(monthlyPayment: Double, rate: Float, term: Int): Double {
        val m = 1 + rate * 0.01 / 12
        val mPowTerm = m.pow(term)
        return monthlyPayment * (mPowTerm - 1) / (m.pow(term + 1.0) - mPowTerm)
    }

    private fun generateRates(startRate: Float, endRate: Float): List<Float> {
        val rates = ArrayList<Float>()
        if (startRate - startRate.toInt() > 0f) {
            rates += startRate
        }
        var value = startRate.toInt() + 1
        while (value <= endRate.toInt()) {
            rates += value.toFloat()
            value++
        }
        if (endRate - endRate.toInt() > 0f && rates.lastOrNull() != endRate) {
            rates += endRate
        }
        if (rates.isEmpty()) {
            rates += startRate
        }
        return rates
    }
}
