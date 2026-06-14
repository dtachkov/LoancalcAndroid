package ru.kredit.calculator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.entity.LoanDetailsEntity

@Dao
interface LoanDetailsDao {
    @Query(
        "SELECT * FROM ${DatabaseContract.LoanDetailsColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanDetailsColumns.LOAN_ID} = :loanId LIMIT 1"
    )
    suspend fun getByLoanId(loanId: Long): LoanDetailsEntity?

    @Query(
        "SELECT * FROM ${DatabaseContract.LoanDetailsColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanDetailsColumns.LOAN_ID} IN (:loanIds)"
    )
    suspend fun getByLoanIds(loanIds: List<Long>): List<LoanDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(details: LoanDetailsEntity)

    @Query(
        "DELETE FROM ${DatabaseContract.LoanDetailsColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanDetailsColumns.LOAN_ID} = :loanId"
    )
    suspend fun deleteByLoanId(loanId: Long)
}
