package ru.kredit.calculator.data.repository

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.model.LoanFull
import ru.kredit.calculator.data.util.GsonFactory
import java.io.File
import java.io.FileOutputStream

class ImportExportRepository(
    private val loanRepository: LoanRepository,
    private val extraRepository: ExtraRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun importFromFile(file: File): Int = withContext(ioDispatcher) {
        val json = file.readText()
        val type = object : TypeToken<List<LoanFull>>() {}.type
        val loans: List<LoanFull> = GsonFactory.create().fromJson(json, type)

        loans.forEach { loanFull ->
            val savedLoan = loanRepository.saveLoan(loanFull.loan.copy(id = 0))
            loanFull.account?.let { account ->
                if (!account.isEmpty()) {
                    loanRepository.saveLoanDetails(savedLoan.id, account)
                }
            }
            loanFull.extras.forEach { extra ->
                extraRepository.saveExtra(
                    extra.copy(
                        id = 0,
                        loanId = savedLoan.id,
                    )
                )
            }
        }

        loans.size
    }

    suspend fun exportToFile(file: File, loanIds: List<Long>): Int = withContext(ioDispatcher) {
        val loanFulls = loanRepository.getLoanFulls(loanIds)
        val json = GsonFactory.create().toJson(loanFulls)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(json.toByteArray())
        }
        loanFulls.size
    }

    suspend fun importLoanFulls(loanFulls: List<LoanFull>): Int = withContext(ioDispatcher) {
        loanFulls.forEach { loanFull ->
            val savedLoan = loanRepository.saveLoan(loanFull.loan.copy(id = 0))
            loanFull.extras.forEach { extra ->
                extraRepository.saveExtra(
                    extra.copy(
                        id = 0,
                        loanId = savedLoan.id,
                    )
                )
            }
        }
        loanFulls.size
    }

    suspend fun exportLoanFulls(loanIds: List<Long>): List<LoanFull> = withContext(ioDispatcher) {
        loanRepository.getLoanFulls(loanIds)
    }
}
