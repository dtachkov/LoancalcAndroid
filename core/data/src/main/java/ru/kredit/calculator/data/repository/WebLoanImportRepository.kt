package ru.kredit.calculator.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.network.WebLoanApi
import ru.kredit.calculator.data.network.parseLoanIdFromUrl
import ru.kredit.calculator.data.network.toExtras
import ru.kredit.calculator.data.network.toLoan
import java.io.IOException

class WebLoanImportRepository(
    private val api: WebLoanApi,
    private val loanRepository: LoanRepository,
    private val extraRepository: ExtraRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun importFromUrl(url: String): Int = withContext(ioDispatcher) {
        val loanId = parseLoanIdFromUrl(url)
            ?: throw WebLoanImportException(WebLoanImportError.INVALID_URL)

        val response = try {
            api.getLoan(loanId)
        } catch (_: IOException) {
            throw WebLoanImportException(WebLoanImportError.NETWORK)
        }

        if (!response.error.isNullOrBlank()) {
            throw WebLoanImportException(WebLoanImportError.NOT_FOUND)
        }

        val savedLoan = loanRepository.saveLoan(response.toLoan(title = loanId))
        response.toExtras().forEach { extra ->
            extraRepository.saveExtra(extra.copy(id = 0, loanId = savedLoan.id))
        }
        1
    }
}

enum class WebLoanImportError {
    INVALID_URL,
    NOT_FOUND,
    NETWORK,
}

class WebLoanImportException(val error: WebLoanImportError) : Exception()
