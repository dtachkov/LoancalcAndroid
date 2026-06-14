package ru.kredit.calculator.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.kredit.calculator.database.DatabaseContract

@Entity(
    tableName = DatabaseContract.LoanDetailsColumns.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = [DatabaseContract.LoanColumns.ID],
            childColumns = [DatabaseContract.LoanDetailsColumns.LOAN_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [DatabaseContract.LoanDetailsColumns.LOAN_ID], unique = true)],
)
data class LoanDetailsEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.LOAN_ID)
    val loanId: Long,
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.BANK_NAME)
    val bankName: String = "",
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.ACCOUNT_NUMBER)
    val accountNumber: String = "",
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.UIC)
    val uic: String = "",
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.CORRESPONDENT_ACCOUNT)
    val correspondentAccount: String = "",
    @ColumnInfo(name = DatabaseContract.LoanDetailsColumns.PAYMENT_COMMENT)
    val paymentComment: String? = null,
)
