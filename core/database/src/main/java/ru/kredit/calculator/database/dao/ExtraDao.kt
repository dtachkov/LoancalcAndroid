package ru.kredit.calculator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.entity.ExtraEntity

@Dao
interface ExtraDao {
    @Query(
        "SELECT COUNT(*) FROM ${DatabaseContract.ExtraColumns.TABLE_NAME}"
    )
    fun observeCount(): Flow<Int>

    @Query(
        "SELECT * FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId " +
            "ORDER BY ${DatabaseContract.ExtraColumns.ID} ASC"
    )
    fun observeByLoanId(loanId: Long): Flow<List<ExtraEntity>>

    @Query(
        "SELECT * FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId " +
            "ORDER BY ${DatabaseContract.ExtraColumns.ID} ASC"
    )
    suspend fun getByLoanId(loanId: Long): List<ExtraEntity>

    @Query(
        "SELECT * FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} IN (:loanIds) " +
            "ORDER BY ${DatabaseContract.ExtraColumns.LOAN_ID} ASC, ${DatabaseContract.ExtraColumns.ID} ASC"
    )
    suspend fun getByLoanIds(loanIds: List<Long>): List<ExtraEntity>

    @Query(
        "SELECT * FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId " +
            "AND ${DatabaseContract.ExtraColumns.ID} = :extraId LIMIT 1"
    )
    suspend fun getByLoanAndExtraId(loanId: Long, extraId: Long): ExtraEntity?

    @Query(
        "SELECT COUNT(*) + 1 FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId " +
            "AND ${DatabaseContract.ExtraColumns.ID} != :excludeExtraId " +
            "AND ${DatabaseContract.ExtraColumns.TYPE} IN (:types)"
    )
    suspend fun countExtrasByTypes(
        loanId: Long,
        excludeExtraId: Long,
        types: List<Int>,
    ): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(extra: ExtraEntity): Long

    @Update
    suspend fun update(extra: ExtraEntity)

    @Query(
        "DELETE FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId " +
            "AND ${DatabaseContract.ExtraColumns.ID} = :extraId"
    )
    suspend fun deleteByLoanAndExtraId(loanId: Long, extraId: Long)

    @Query(
        "DELETE FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} = :loanId"
    )
    suspend fun deleteByLoanId(loanId: Long)

    @Query(
        "DELETE FROM ${DatabaseContract.ExtraColumns.TABLE_NAME} " +
            "WHERE ${DatabaseContract.ExtraColumns.LOAN_ID} IN (:loanIds)"
    )
    suspend fun deleteByLoanIds(loanIds: List<Long>)
}
