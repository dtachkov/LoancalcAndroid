package ru.kredit.calculator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.entity.OfferEntity

@Dao
interface OfferDao {
    @Query("SELECT * FROM ${DatabaseContract.OfferColumns.TABLE_NAME} ORDER BY ${DatabaseContract.OfferColumns.ID} ASC")
    fun observeAll(): Flow<List<OfferEntity>>

    @Query("SELECT * FROM ${DatabaseContract.OfferColumns.TABLE_NAME} ORDER BY ${DatabaseContract.OfferColumns.ID} ASC")
    suspend fun getAll(): List<OfferEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(offers: List<OfferEntity>)

    @Query("DELETE FROM ${DatabaseContract.OfferColumns.TABLE_NAME}")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(offers: List<OfferEntity>) {
        deleteAll()
        if (offers.isNotEmpty()) {
            insertAll(offers)
        }
    }
}
