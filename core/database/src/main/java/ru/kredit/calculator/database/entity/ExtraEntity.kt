package ru.kredit.calculator.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kredit.calculator.database.DatabaseContract

@Entity(tableName = DatabaseContract.ExtraColumns.TABLE_NAME)
data class ExtraEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.ExtraColumns.ID)
    val id: Long? = null,
    @ColumnInfo(name = DatabaseContract.ExtraColumns.DOCUMENT_NUMBER)
    val documentNumber: String? = null,
    @ColumnInfo(name = DatabaseContract.ExtraColumns.TYPE)
    val type: Int? = null,
    @ColumnInfo(name = DatabaseContract.ExtraColumns.DATE)
    val date: String? = null,
    @ColumnInfo(name = DatabaseContract.ExtraColumns.AMOUNT)
    val amount: Float? = null,
    @ColumnInfo(name = DatabaseContract.ExtraColumns.LOAN_ID)
    val loanId: Long? = null,
)
