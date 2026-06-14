package ru.kredit.calculator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.entity.LoanEntity

@Dao
interface LoanDao {
    @Query("SELECT * FROM ${DatabaseContract.LoanColumns.TABLE_NAME} ORDER BY ${DatabaseContract.LoanColumns.ID} ASC")
    fun observeAll(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM ${DatabaseContract.LoanColumns.TABLE_NAME} ORDER BY ${DatabaseContract.LoanColumns.ID} ASC")
    suspend fun getAll(): List<LoanEntity>

    @Query(
        "SELECT * FROM ${DatabaseContract.LoanColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanColumns.ID} = :loanId LIMIT 1"
    )
    suspend fun getById(loanId: Long): LoanEntity?

    @Query(
        "SELECT * FROM ${DatabaseContract.LoanColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanColumns.ID} IN (:loanIds) " +
            "ORDER BY ${DatabaseContract.LoanColumns.ID} ASC"
    )
    suspend fun getByIds(loanIds: List<Long>): List<LoanEntity>

    @Query(
        "SELECT COUNT(*) + 1 FROM ${DatabaseContract.LoanColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanColumns.CREATION_DATE} = :creationDate " +
            "AND ${DatabaseContract.LoanColumns.ID} != :excludeLoanId"
    )
    suspend fun countLoansCreatedOnDate(creationDate: String, excludeLoanId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(loan: LoanEntity): Long

    @Update
    suspend fun update(loan: LoanEntity)

    @Query("DELETE FROM ${DatabaseContract.LoanColumns.TABLE_NAME} WHERE ${DatabaseContract.LoanColumns.ID} = :loanId")
    suspend fun deleteById(loanId: Long)

    @Query(
        "DELETE FROM ${DatabaseContract.LoanColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.LoanColumns.ID} IN (:loanIds)"
    )
    suspend fun deleteByIds(loanIds: List<Long>)
}
