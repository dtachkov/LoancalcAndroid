package ru.kredit.calculator.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import ru.kredit.calculator.database.entity.LoanDetailsEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LoanDetailsDao_Impl(
  __db: RoomDatabase,
) : LoanDetailsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLoanDetailsEntity: EntityInsertAdapter<LoanDetailsEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLoanDetailsEntity = object : EntityInsertAdapter<LoanDetailsEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `loan_details` (`loan_id`,`bank_name`,`account_number`,`uic`,`correspondent_account`,`payment_comment`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LoanDetailsEntity) {
        statement.bindLong(1, entity.loanId)
        statement.bindText(2, entity.bankName)
        statement.bindText(3, entity.accountNumber)
        statement.bindText(4, entity.uic)
        statement.bindText(5, entity.correspondentAccount)
        val _tmpPaymentComment: String? = entity.paymentComment
        if (_tmpPaymentComment == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpPaymentComment)
        }
      }
    }
  }

  public override suspend fun upsert(details: LoanDetailsEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfLoanDetailsEntity.insert(_connection, details)
  }

  public override suspend fun getByLoanId(loanId: Long): LoanDetailsEntity? {
    val _sql: String = "SELECT * FROM loan_details WHERE loan_id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loan_id")
        val _columnIndexOfBankName: Int = getColumnIndexOrThrow(_stmt, "bank_name")
        val _columnIndexOfAccountNumber: Int = getColumnIndexOrThrow(_stmt, "account_number")
        val _columnIndexOfUic: Int = getColumnIndexOrThrow(_stmt, "uic")
        val _columnIndexOfCorrespondentAccount: Int = getColumnIndexOrThrow(_stmt,
            "correspondent_account")
        val _columnIndexOfPaymentComment: Int = getColumnIndexOrThrow(_stmt, "payment_comment")
        val _result: LoanDetailsEntity?
        if (_stmt.step()) {
          val _tmpLoanId: Long
          _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          val _tmpBankName: String
          _tmpBankName = _stmt.getText(_columnIndexOfBankName)
          val _tmpAccountNumber: String
          _tmpAccountNumber = _stmt.getText(_columnIndexOfAccountNumber)
          val _tmpUic: String
          _tmpUic = _stmt.getText(_columnIndexOfUic)
          val _tmpCorrespondentAccount: String
          _tmpCorrespondentAccount = _stmt.getText(_columnIndexOfCorrespondentAccount)
          val _tmpPaymentComment: String?
          if (_stmt.isNull(_columnIndexOfPaymentComment)) {
            _tmpPaymentComment = null
          } else {
            _tmpPaymentComment = _stmt.getText(_columnIndexOfPaymentComment)
          }
          _result =
              LoanDetailsEntity(_tmpLoanId,_tmpBankName,_tmpAccountNumber,_tmpUic,_tmpCorrespondentAccount,_tmpPaymentComment)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByLoanIds(loanIds: List<Long>): List<LoanDetailsEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM loan_details WHERE loan_id IN (")
    val _inputSize: Int = loanIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: Long in loanIds) {
          _stmt.bindLong(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loan_id")
        val _columnIndexOfBankName: Int = getColumnIndexOrThrow(_stmt, "bank_name")
        val _columnIndexOfAccountNumber: Int = getColumnIndexOrThrow(_stmt, "account_number")
        val _columnIndexOfUic: Int = getColumnIndexOrThrow(_stmt, "uic")
        val _columnIndexOfCorrespondentAccount: Int = getColumnIndexOrThrow(_stmt,
            "correspondent_account")
        val _columnIndexOfPaymentComment: Int = getColumnIndexOrThrow(_stmt, "payment_comment")
        val _result: MutableList<LoanDetailsEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: LoanDetailsEntity
          val _tmpLoanId: Long
          _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          val _tmpBankName: String
          _tmpBankName = _stmt.getText(_columnIndexOfBankName)
          val _tmpAccountNumber: String
          _tmpAccountNumber = _stmt.getText(_columnIndexOfAccountNumber)
          val _tmpUic: String
          _tmpUic = _stmt.getText(_columnIndexOfUic)
          val _tmpCorrespondentAccount: String
          _tmpCorrespondentAccount = _stmt.getText(_columnIndexOfCorrespondentAccount)
          val _tmpPaymentComment: String?
          if (_stmt.isNull(_columnIndexOfPaymentComment)) {
            _tmpPaymentComment = null
          } else {
            _tmpPaymentComment = _stmt.getText(_columnIndexOfPaymentComment)
          }
          _item_1 =
              LoanDetailsEntity(_tmpLoanId,_tmpBankName,_tmpAccountNumber,_tmpUic,_tmpCorrespondentAccount,_tmpPaymentComment)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByLoanId(loanId: Long) {
    val _sql: String = "DELETE FROM loan_details WHERE loan_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
