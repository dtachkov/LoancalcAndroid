package ru.kredit.calculator.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.mapper.toDomain
import ru.kredit.calculator.data.mapper.toEntity
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanFull
import ru.kredit.calculator.data.util.DateFormats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoanRepository(
    private val loanDao: ru.kredit.calculator.database.dao.LoanDao,
    private val extraDao: ru.kredit.calculator.database.dao.ExtraDao,
    private val ioDispatcher: CoroutineDispatcher,
) {

    fun observeLoans(): Flow<List<Loan>> {
        return loanDao.observeAll().map { loans -> loans.map { it.toDomain() } }
    }

    suspend fun getLoans(): List<Loan> = withContext(ioDispatcher) {
        loanDao.getAll().map { it.toDomain() }
    }

    suspend fun getLoan(loanId: Long): Loan? = withContext(ioDispatcher) {
        loanDao.getById(loanId)?.toDomain()
    }

    suspend fun getLoans(loanIds: List<Long>): List<Loan> = withContext(ioDispatcher) {
        if (loanIds.isEmpty()) return@withContext emptyList()
        loanDao.getByIds(loanIds).map { it.toDomain() }
    }

    suspend fun saveLoan(loan: Loan): Loan = withContext(ioDispatcher) {
        val loanWithTitle = if (loan.title.isNullOrBlank()) {
            loan.copy(title = generateTitle(loan))
        } else {
            loan
        }

        if (loanWithTitle.id != 0L && loanDao.getById(loanWithTitle.id) != null) {
            loanDao.update(loanWithTitle.toEntity())
            loanWithTitle
        } else {
            val newId = loanDao.insert(loanWithTitle.copy(id = 0).toEntity())
            loanWithTitle.copy(id = newId)
        }
    }

    suspend fun deleteLoan(loanId: Long) = withContext(ioDispatcher) {
        extraDao.deleteByLoanId(loanId)
        loanDao.deleteById(loanId)
    }

    suspend fun deleteLoans(loanIds: List<Long>) = withContext(ioDispatcher) {
        if (loanIds.isEmpty()) return@withContext
        extraDao.deleteByLoanIds(loanIds)
        loanDao.deleteByIds(loanIds)
    }

    suspend fun getLoanFull(loanId: Long): LoanFull? = withContext(ioDispatcher) {
        val loan = loanDao.getById(loanId)?.toDomain() ?: return@withContext null
        val extras = extraDao.getByLoanId(loanId).map { it.toDomain() }
        LoanFull(loan = loan, extras = extras)
    }

    suspend fun getLoanFulls(loanIds: List<Long>): List<LoanFull> = withContext(ioDispatcher) {
        if (loanIds.isEmpty()) return@withContext emptyList()

        val loans = loanDao.getByIds(loanIds).map { it.toDomain() }
        val extrasByLoanId = extraDao.getByLoanIds(loanIds)
            .map { it.toDomain() }
            .groupBy { it.loanId }

        loans.map { loan ->
            LoanFull(
                loan = loan,
                extras = extrasByLoanId[loan.id].orEmpty(),
            )
        }
    }

    private suspend fun generateTitle(loan: Loan): String {
        val creationDate = DateFormats.formatDate(loan.creationDate) ?: DateFormats.formatDate(Date()).orEmpty()
        val count = loanDao.countLoansCreatedOnDate(creationDate, loan.id)
        val titleDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(loan.creationDate)
        return "$titleDate/$count"
    }
}

class ExtraRepository(
    private val extraDao: ru.kredit.calculator.database.dao.ExtraDao,
    private val ioDispatcher: CoroutineDispatcher,
) {

    fun observeExtras(loanId: Long): Flow<List<Extra>> {
        return extraDao.observeByLoanId(loanId).map { extras -> extras.map { it.toDomain() } }
    }

    suspend fun getExtras(loanId: Long): List<Extra> = withContext(ioDispatcher) {
        extraDao.getByLoanId(loanId).map { it.toDomain() }
    }

    suspend fun saveExtra(extra: Extra): Extra = withContext(ioDispatcher) {
        require(extra.loanId != 0L) { "loanId must be set before saving extra" }

        val extraWithDocument = if (extra.documentNumber.isNullOrBlank()) {
            extra.copy(documentNumber = generateDocumentNumber(extra))
        } else {
            extra
        }

        val existing = if (extraWithDocument.id != 0L) {
            extraDao.getByLoanAndExtraId(extraWithDocument.loanId, extraWithDocument.id)
        } else {
            null
        }

        if (existing != null) {
            extraDao.update(extraWithDocument.toEntity())
            extraWithDocument
        } else {
            val newId = extraDao.insert(extraWithDocument.copy(id = 0).toEntity())
            extraWithDocument.copy(id = newId)
        }
    }

    suspend fun deleteExtra(loanId: Long, extraId: Long) = withContext(ioDispatcher) {
        extraDao.deleteByLoanAndExtraId(loanId, extraId)
    }

    private suspend fun generateDocumentNumber(extra: Extra): String {
        val siblingTypes = ExtraType.siblingTypes(extra.type)
        val count = extraDao.countExtrasByTypes(
            loanId = extra.loanId,
            excludeExtraId = extra.id,
            types = siblingTypes,
        )
        return count.toString()
    }
}
