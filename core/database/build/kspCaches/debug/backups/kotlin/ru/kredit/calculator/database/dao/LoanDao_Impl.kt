package ru.kredit.calculator.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Float
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
import kotlinx.coroutines.flow.Flow
import ru.kredit.calculator.database.entity.LoanEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LoanDao_Impl(
  __db: RoomDatabase,
) : LoanDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLoanEntity: EntityInsertAdapter<LoanEntity>

  private val __updateAdapterOfLoanEntity: EntityDeleteOrUpdateAdapter<LoanEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLoanEntity = object : EntityInsertAdapter<LoanEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `Loans` (`_id`,`creation_date`,`title`,`amount`,`rate`,`term`,`type`,`first_payment_date`,`date_of_issue`,`monthly_payment`,`consider_days_off`,`pay_on_last_day_of_month`,`apply_extras_immediately`,`interest_only_after_principal_paid_by_extra`,`ignore_passed_periods_after_rate_change`,`extra_day_in_month`,`is_forecast_active`,`forecast_montly_pay`,`forecast_days_before`,`forecast_start_date`,`forecast_extra_type`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LoanEntity) {
        val _tmpId: Long? = entity.id
        if (_tmpId == null) {
          statement.bindNull(1)
        } else {
          statement.bindLong(1, _tmpId)
        }
        val _tmpCreationDate: String? = entity.creationDate
        if (_tmpCreationDate == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpCreationDate)
        }
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTitle)
        }
        val _tmpAmount: Float? = entity.amount
        if (_tmpAmount == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpAmount.toDouble())
        }
        val _tmpRate: Float? = entity.rate
        if (_tmpRate == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpRate.toDouble())
        }
        val _tmpTerm: Int? = entity.term
        if (_tmpTerm == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpTerm.toLong())
        }
        val _tmpType: Int? = entity.type
        if (_tmpType == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpType.toLong())
        }
        val _tmpFirstPaymentDate: String? = entity.firstPaymentDate
        if (_tmpFirstPaymentDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFirstPaymentDate)
        }
        val _tmpDateOfIssue: String? = entity.dateOfIssue
        if (_tmpDateOfIssue == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpDateOfIssue)
        }
        val _tmpMonthlyPayment: Float? = entity.monthlyPayment
        if (_tmpMonthlyPayment == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpMonthlyPayment.toDouble())
        }
        val _tmpConsiderDaysOff: Int? = entity.considerDaysOff
        if (_tmpConsiderDaysOff == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpConsiderDaysOff.toLong())
        }
        val _tmpPayOnLastDayOfMonth: Int? = entity.payOnLastDayOfMonth
        if (_tmpPayOnLastDayOfMonth == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpPayOnLastDayOfMonth.toLong())
        }
        val _tmpApplyExtrasImmediately: Int? = entity.applyExtrasImmediately
        if (_tmpApplyExtrasImmediately == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpApplyExtrasImmediately.toLong())
        }
        val _tmpCalculateExtrasByTermLikeSberbank: Int? = entity.calculateExtrasByTermLikeSberbank
        if (_tmpCalculateExtrasByTermLikeSberbank == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpCalculateExtrasByTermLikeSberbank.toLong())
        }
        val _tmpIgnorePassedPeriodsAfterRateChange: Int? = entity.ignorePassedPeriodsAfterRateChange
        if (_tmpIgnorePassedPeriodsAfterRateChange == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpIgnorePassedPeriodsAfterRateChange.toLong())
        }
        val _tmpExtraDayInMonth: Int? = entity.extraDayInMonth
        if (_tmpExtraDayInMonth == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpExtraDayInMonth.toLong())
        }
        val _tmpIsForecastActive: Int? = entity.isForecastActive
        if (_tmpIsForecastActive == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpIsForecastActive.toLong())
        }
        val _tmpForecastMonthlyPay: Float? = entity.forecastMonthlyPay
        if (_tmpForecastMonthlyPay == null) {
          statement.bindNull(18)
        } else {
          statement.bindDouble(18, _tmpForecastMonthlyPay.toDouble())
        }
        val _tmpForecastDaysBefore: Int? = entity.forecastDaysBefore
        if (_tmpForecastDaysBefore == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpForecastDaysBefore.toLong())
        }
        val _tmpForecastStartDate: String? = entity.forecastStartDate
        if (_tmpForecastStartDate == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpForecastStartDate)
        }
        val _tmpForecastExtraType: Int? = entity.forecastExtraType
        if (_tmpForecastExtraType == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpForecastExtraType.toLong())
        }
      }
    }
    this.__updateAdapterOfLoanEntity = object : EntityDeleteOrUpdateAdapter<LoanEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `Loans` SET `_id` = ?,`creation_date` = ?,`title` = ?,`amount` = ?,`rate` = ?,`term` = ?,`type` = ?,`first_payment_date` = ?,`date_of_issue` = ?,`monthly_payment` = ?,`consider_days_off` = ?,`pay_on_last_day_of_month` = ?,`apply_extras_immediately` = ?,`interest_only_after_principal_paid_by_extra` = ?,`ignore_passed_periods_after_rate_change` = ?,`extra_day_in_month` = ?,`is_forecast_active` = ?,`forecast_montly_pay` = ?,`forecast_days_before` = ?,`forecast_start_date` = ?,`forecast_extra_type` = ? WHERE `_id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: LoanEntity) {
        val _tmpId: Long? = entity.id
        if (_tmpId == null) {
          statement.bindNull(1)
        } else {
          statement.bindLong(1, _tmpId)
        }
        val _tmpCreationDate: String? = entity.creationDate
        if (_tmpCreationDate == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpCreationDate)
        }
        val _tmpTitle: String? = entity.title
        if (_tmpTitle == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpTitle)
        }
        val _tmpAmount: Float? = entity.amount
        if (_tmpAmount == null) {
          statement.bindNull(4)
        } else {
          statement.bindDouble(4, _tmpAmount.toDouble())
        }
        val _tmpRate: Float? = entity.rate
        if (_tmpRate == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpRate.toDouble())
        }
        val _tmpTerm: Int? = entity.term
        if (_tmpTerm == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpTerm.toLong())
        }
        val _tmpType: Int? = entity.type
        if (_tmpType == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpType.toLong())
        }
        val _tmpFirstPaymentDate: String? = entity.firstPaymentDate
        if (_tmpFirstPaymentDate == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpFirstPaymentDate)
        }
        val _tmpDateOfIssue: String? = entity.dateOfIssue
        if (_tmpDateOfIssue == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpDateOfIssue)
        }
        val _tmpMonthlyPayment: Float? = entity.monthlyPayment
        if (_tmpMonthlyPayment == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpMonthlyPayment.toDouble())
        }
        val _tmpConsiderDaysOff: Int? = entity.considerDaysOff
        if (_tmpConsiderDaysOff == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpConsiderDaysOff.toLong())
        }
        val _tmpPayOnLastDayOfMonth: Int? = entity.payOnLastDayOfMonth
        if (_tmpPayOnLastDayOfMonth == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpPayOnLastDayOfMonth.toLong())
        }
        val _tmpApplyExtrasImmediately: Int? = entity.applyExtrasImmediately
        if (_tmpApplyExtrasImmediately == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpApplyExtrasImmediately.toLong())
        }
        val _tmpCalculateExtrasByTermLikeSberbank: Int? = entity.calculateExtrasByTermLikeSberbank
        if (_tmpCalculateExtrasByTermLikeSberbank == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpCalculateExtrasByTermLikeSberbank.toLong())
        }
        val _tmpIgnorePassedPeriodsAfterRateChange: Int? = entity.ignorePassedPeriodsAfterRateChange
        if (_tmpIgnorePassedPeriodsAfterRateChange == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpIgnorePassedPeriodsAfterRateChange.toLong())
        }
        val _tmpExtraDayInMonth: Int? = entity.extraDayInMonth
        if (_tmpExtraDayInMonth == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpExtraDayInMonth.toLong())
        }
        val _tmpIsForecastActive: Int? = entity.isForecastActive
        if (_tmpIsForecastActive == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpIsForecastActive.toLong())
        }
        val _tmpForecastMonthlyPay: Float? = entity.forecastMonthlyPay
        if (_tmpForecastMonthlyPay == null) {
          statement.bindNull(18)
        } else {
          statement.bindDouble(18, _tmpForecastMonthlyPay.toDouble())
        }
        val _tmpForecastDaysBefore: Int? = entity.forecastDaysBefore
        if (_tmpForecastDaysBefore == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpForecastDaysBefore.toLong())
        }
        val _tmpForecastStartDate: String? = entity.forecastStartDate
        if (_tmpForecastStartDate == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpForecastStartDate)
        }
        val _tmpForecastExtraType: Int? = entity.forecastExtraType
        if (_tmpForecastExtraType == null) {
          statement.bindNull(21)
        } else {
          statement.bindLong(21, _tmpForecastExtraType.toLong())
        }
        val _tmpId_1: Long? = entity.id
        if (_tmpId_1 == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpId_1)
        }
      }
    }
  }

  public override suspend fun insert(loan: LoanEntity): Long = performSuspending(__db, false, true)
      { _connection ->
    val _result: Long = __insertAdapterOfLoanEntity.insertAndReturnId(_connection, loan)
    _result
  }

  public override suspend fun update(loan: LoanEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __updateAdapterOfLoanEntity.handle(_connection, loan)
  }

  public override fun observeAll(): Flow<List<LoanEntity>> {
    val _sql: String = "SELECT * FROM Loans ORDER BY _id ASC"
    return createFlow(__db, false, arrayOf("Loans")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfCreationDate: Int = getColumnIndexOrThrow(_stmt, "creation_date")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfFirstPaymentDate: Int = getColumnIndexOrThrow(_stmt, "first_payment_date")
        val _columnIndexOfDateOfIssue: Int = getColumnIndexOrThrow(_stmt, "date_of_issue")
        val _columnIndexOfMonthlyPayment: Int = getColumnIndexOrThrow(_stmt, "monthly_payment")
        val _columnIndexOfConsiderDaysOff: Int = getColumnIndexOrThrow(_stmt, "consider_days_off")
        val _columnIndexOfPayOnLastDayOfMonth: Int = getColumnIndexOrThrow(_stmt,
            "pay_on_last_day_of_month")
        val _columnIndexOfApplyExtrasImmediately: Int = getColumnIndexOrThrow(_stmt,
            "apply_extras_immediately")
        val _columnIndexOfCalculateExtrasByTermLikeSberbank: Int = getColumnIndexOrThrow(_stmt,
            "interest_only_after_principal_paid_by_extra")
        val _columnIndexOfIgnorePassedPeriodsAfterRateChange: Int = getColumnIndexOrThrow(_stmt,
            "ignore_passed_periods_after_rate_change")
        val _columnIndexOfExtraDayInMonth: Int = getColumnIndexOrThrow(_stmt, "extra_day_in_month")
        val _columnIndexOfIsForecastActive: Int = getColumnIndexOrThrow(_stmt, "is_forecast_active")
        val _columnIndexOfForecastMonthlyPay: Int = getColumnIndexOrThrow(_stmt,
            "forecast_montly_pay")
        val _columnIndexOfForecastDaysBefore: Int = getColumnIndexOrThrow(_stmt,
            "forecast_days_before")
        val _columnIndexOfForecastStartDate: Int = getColumnIndexOrThrow(_stmt,
            "forecast_start_date")
        val _columnIndexOfForecastExtraType: Int = getColumnIndexOrThrow(_stmt,
            "forecast_extra_type")
        val _result: MutableList<LoanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LoanEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpCreationDate: String?
          if (_stmt.isNull(_columnIndexOfCreationDate)) {
            _tmpCreationDate = null
          } else {
            _tmpCreationDate = _stmt.getText(_columnIndexOfCreationDate)
          }
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpRate: Float?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getDouble(_columnIndexOfRate).toFloat()
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpFirstPaymentDate: String?
          if (_stmt.isNull(_columnIndexOfFirstPaymentDate)) {
            _tmpFirstPaymentDate = null
          } else {
            _tmpFirstPaymentDate = _stmt.getText(_columnIndexOfFirstPaymentDate)
          }
          val _tmpDateOfIssue: String?
          if (_stmt.isNull(_columnIndexOfDateOfIssue)) {
            _tmpDateOfIssue = null
          } else {
            _tmpDateOfIssue = _stmt.getText(_columnIndexOfDateOfIssue)
          }
          val _tmpMonthlyPayment: Float?
          if (_stmt.isNull(_columnIndexOfMonthlyPayment)) {
            _tmpMonthlyPayment = null
          } else {
            _tmpMonthlyPayment = _stmt.getDouble(_columnIndexOfMonthlyPayment).toFloat()
          }
          val _tmpConsiderDaysOff: Int?
          if (_stmt.isNull(_columnIndexOfConsiderDaysOff)) {
            _tmpConsiderDaysOff = null
          } else {
            _tmpConsiderDaysOff = _stmt.getLong(_columnIndexOfConsiderDaysOff).toInt()
          }
          val _tmpPayOnLastDayOfMonth: Int?
          if (_stmt.isNull(_columnIndexOfPayOnLastDayOfMonth)) {
            _tmpPayOnLastDayOfMonth = null
          } else {
            _tmpPayOnLastDayOfMonth = _stmt.getLong(_columnIndexOfPayOnLastDayOfMonth).toInt()
          }
          val _tmpApplyExtrasImmediately: Int?
          if (_stmt.isNull(_columnIndexOfApplyExtrasImmediately)) {
            _tmpApplyExtrasImmediately = null
          } else {
            _tmpApplyExtrasImmediately = _stmt.getLong(_columnIndexOfApplyExtrasImmediately).toInt()
          }
          val _tmpCalculateExtrasByTermLikeSberbank: Int?
          if (_stmt.isNull(_columnIndexOfCalculateExtrasByTermLikeSberbank)) {
            _tmpCalculateExtrasByTermLikeSberbank = null
          } else {
            _tmpCalculateExtrasByTermLikeSberbank =
                _stmt.getLong(_columnIndexOfCalculateExtrasByTermLikeSberbank).toInt()
          }
          val _tmpIgnorePassedPeriodsAfterRateChange: Int?
          if (_stmt.isNull(_columnIndexOfIgnorePassedPeriodsAfterRateChange)) {
            _tmpIgnorePassedPeriodsAfterRateChange = null
          } else {
            _tmpIgnorePassedPeriodsAfterRateChange =
                _stmt.getLong(_columnIndexOfIgnorePassedPeriodsAfterRateChange).toInt()
          }
          val _tmpExtraDayInMonth: Int?
          if (_stmt.isNull(_columnIndexOfExtraDayInMonth)) {
            _tmpExtraDayInMonth = null
          } else {
            _tmpExtraDayInMonth = _stmt.getLong(_columnIndexOfExtraDayInMonth).toInt()
          }
          val _tmpIsForecastActive: Int?
          if (_stmt.isNull(_columnIndexOfIsForecastActive)) {
            _tmpIsForecastActive = null
          } else {
            _tmpIsForecastActive = _stmt.getLong(_columnIndexOfIsForecastActive).toInt()
          }
          val _tmpForecastMonthlyPay: Float?
          if (_stmt.isNull(_columnIndexOfForecastMonthlyPay)) {
            _tmpForecastMonthlyPay = null
          } else {
            _tmpForecastMonthlyPay = _stmt.getDouble(_columnIndexOfForecastMonthlyPay).toFloat()
          }
          val _tmpForecastDaysBefore: Int?
          if (_stmt.isNull(_columnIndexOfForecastDaysBefore)) {
            _tmpForecastDaysBefore = null
          } else {
            _tmpForecastDaysBefore = _stmt.getLong(_columnIndexOfForecastDaysBefore).toInt()
          }
          val _tmpForecastStartDate: String?
          if (_stmt.isNull(_columnIndexOfForecastStartDate)) {
            _tmpForecastStartDate = null
          } else {
            _tmpForecastStartDate = _stmt.getText(_columnIndexOfForecastStartDate)
          }
          val _tmpForecastExtraType: Int?
          if (_stmt.isNull(_columnIndexOfForecastExtraType)) {
            _tmpForecastExtraType = null
          } else {
            _tmpForecastExtraType = _stmt.getLong(_columnIndexOfForecastExtraType).toInt()
          }
          _item =
              LoanEntity(_tmpId,_tmpCreationDate,_tmpTitle,_tmpAmount,_tmpRate,_tmpTerm,_tmpType,_tmpFirstPaymentDate,_tmpDateOfIssue,_tmpMonthlyPayment,_tmpConsiderDaysOff,_tmpPayOnLastDayOfMonth,_tmpApplyExtrasImmediately,_tmpCalculateExtrasByTermLikeSberbank,_tmpIgnorePassedPeriodsAfterRateChange,_tmpExtraDayInMonth,_tmpIsForecastActive,_tmpForecastMonthlyPay,_tmpForecastDaysBefore,_tmpForecastStartDate,_tmpForecastExtraType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<LoanEntity> {
    val _sql: String = "SELECT * FROM Loans ORDER BY _id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfCreationDate: Int = getColumnIndexOrThrow(_stmt, "creation_date")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfFirstPaymentDate: Int = getColumnIndexOrThrow(_stmt, "first_payment_date")
        val _columnIndexOfDateOfIssue: Int = getColumnIndexOrThrow(_stmt, "date_of_issue")
        val _columnIndexOfMonthlyPayment: Int = getColumnIndexOrThrow(_stmt, "monthly_payment")
        val _columnIndexOfConsiderDaysOff: Int = getColumnIndexOrThrow(_stmt, "consider_days_off")
        val _columnIndexOfPayOnLastDayOfMonth: Int = getColumnIndexOrThrow(_stmt,
            "pay_on_last_day_of_month")
        val _columnIndexOfApplyExtrasImmediately: Int = getColumnIndexOrThrow(_stmt,
            "apply_extras_immediately")
        val _columnIndexOfCalculateExtrasByTermLikeSberbank: Int = getColumnIndexOrThrow(_stmt,
            "interest_only_after_principal_paid_by_extra")
        val _columnIndexOfIgnorePassedPeriodsAfterRateChange: Int = getColumnIndexOrThrow(_stmt,
            "ignore_passed_periods_after_rate_change")
        val _columnIndexOfExtraDayInMonth: Int = getColumnIndexOrThrow(_stmt, "extra_day_in_month")
        val _columnIndexOfIsForecastActive: Int = getColumnIndexOrThrow(_stmt, "is_forecast_active")
        val _columnIndexOfForecastMonthlyPay: Int = getColumnIndexOrThrow(_stmt,
            "forecast_montly_pay")
        val _columnIndexOfForecastDaysBefore: Int = getColumnIndexOrThrow(_stmt,
            "forecast_days_before")
        val _columnIndexOfForecastStartDate: Int = getColumnIndexOrThrow(_stmt,
            "forecast_start_date")
        val _columnIndexOfForecastExtraType: Int = getColumnIndexOrThrow(_stmt,
            "forecast_extra_type")
        val _result: MutableList<LoanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LoanEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpCreationDate: String?
          if (_stmt.isNull(_columnIndexOfCreationDate)) {
            _tmpCreationDate = null
          } else {
            _tmpCreationDate = _stmt.getText(_columnIndexOfCreationDate)
          }
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpRate: Float?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getDouble(_columnIndexOfRate).toFloat()
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpFirstPaymentDate: String?
          if (_stmt.isNull(_columnIndexOfFirstPaymentDate)) {
            _tmpFirstPaymentDate = null
          } else {
            _tmpFirstPaymentDate = _stmt.getText(_columnIndexOfFirstPaymentDate)
          }
          val _tmpDateOfIssue: String?
          if (_stmt.isNull(_columnIndexOfDateOfIssue)) {
            _tmpDateOfIssue = null
          } else {
            _tmpDateOfIssue = _stmt.getText(_columnIndexOfDateOfIssue)
          }
          val _tmpMonthlyPayment: Float?
          if (_stmt.isNull(_columnIndexOfMonthlyPayment)) {
            _tmpMonthlyPayment = null
          } else {
            _tmpMonthlyPayment = _stmt.getDouble(_columnIndexOfMonthlyPayment).toFloat()
          }
          val _tmpConsiderDaysOff: Int?
          if (_stmt.isNull(_columnIndexOfConsiderDaysOff)) {
            _tmpConsiderDaysOff = null
          } else {
            _tmpConsiderDaysOff = _stmt.getLong(_columnIndexOfConsiderDaysOff).toInt()
          }
          val _tmpPayOnLastDayOfMonth: Int?
          if (_stmt.isNull(_columnIndexOfPayOnLastDayOfMonth)) {
            _tmpPayOnLastDayOfMonth = null
          } else {
            _tmpPayOnLastDayOfMonth = _stmt.getLong(_columnIndexOfPayOnLastDayOfMonth).toInt()
          }
          val _tmpApplyExtrasImmediately: Int?
          if (_stmt.isNull(_columnIndexOfApplyExtrasImmediately)) {
            _tmpApplyExtrasImmediately = null
          } else {
            _tmpApplyExtrasImmediately = _stmt.getLong(_columnIndexOfApplyExtrasImmediately).toInt()
          }
          val _tmpCalculateExtrasByTermLikeSberbank: Int?
          if (_stmt.isNull(_columnIndexOfCalculateExtrasByTermLikeSberbank)) {
            _tmpCalculateExtrasByTermLikeSberbank = null
          } else {
            _tmpCalculateExtrasByTermLikeSberbank =
                _stmt.getLong(_columnIndexOfCalculateExtrasByTermLikeSberbank).toInt()
          }
          val _tmpIgnorePassedPeriodsAfterRateChange: Int?
          if (_stmt.isNull(_columnIndexOfIgnorePassedPeriodsAfterRateChange)) {
            _tmpIgnorePassedPeriodsAfterRateChange = null
          } else {
            _tmpIgnorePassedPeriodsAfterRateChange =
                _stmt.getLong(_columnIndexOfIgnorePassedPeriodsAfterRateChange).toInt()
          }
          val _tmpExtraDayInMonth: Int?
          if (_stmt.isNull(_columnIndexOfExtraDayInMonth)) {
            _tmpExtraDayInMonth = null
          } else {
            _tmpExtraDayInMonth = _stmt.getLong(_columnIndexOfExtraDayInMonth).toInt()
          }
          val _tmpIsForecastActive: Int?
          if (_stmt.isNull(_columnIndexOfIsForecastActive)) {
            _tmpIsForecastActive = null
          } else {
            _tmpIsForecastActive = _stmt.getLong(_columnIndexOfIsForecastActive).toInt()
          }
          val _tmpForecastMonthlyPay: Float?
          if (_stmt.isNull(_columnIndexOfForecastMonthlyPay)) {
            _tmpForecastMonthlyPay = null
          } else {
            _tmpForecastMonthlyPay = _stmt.getDouble(_columnIndexOfForecastMonthlyPay).toFloat()
          }
          val _tmpForecastDaysBefore: Int?
          if (_stmt.isNull(_columnIndexOfForecastDaysBefore)) {
            _tmpForecastDaysBefore = null
          } else {
            _tmpForecastDaysBefore = _stmt.getLong(_columnIndexOfForecastDaysBefore).toInt()
          }
          val _tmpForecastStartDate: String?
          if (_stmt.isNull(_columnIndexOfForecastStartDate)) {
            _tmpForecastStartDate = null
          } else {
            _tmpForecastStartDate = _stmt.getText(_columnIndexOfForecastStartDate)
          }
          val _tmpForecastExtraType: Int?
          if (_stmt.isNull(_columnIndexOfForecastExtraType)) {
            _tmpForecastExtraType = null
          } else {
            _tmpForecastExtraType = _stmt.getLong(_columnIndexOfForecastExtraType).toInt()
          }
          _item =
              LoanEntity(_tmpId,_tmpCreationDate,_tmpTitle,_tmpAmount,_tmpRate,_tmpTerm,_tmpType,_tmpFirstPaymentDate,_tmpDateOfIssue,_tmpMonthlyPayment,_tmpConsiderDaysOff,_tmpPayOnLastDayOfMonth,_tmpApplyExtrasImmediately,_tmpCalculateExtrasByTermLikeSberbank,_tmpIgnorePassedPeriodsAfterRateChange,_tmpExtraDayInMonth,_tmpIsForecastActive,_tmpForecastMonthlyPay,_tmpForecastDaysBefore,_tmpForecastStartDate,_tmpForecastExtraType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(loanId: Long): LoanEntity? {
    val _sql: String = "SELECT * FROM Loans WHERE _id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfCreationDate: Int = getColumnIndexOrThrow(_stmt, "creation_date")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfFirstPaymentDate: Int = getColumnIndexOrThrow(_stmt, "first_payment_date")
        val _columnIndexOfDateOfIssue: Int = getColumnIndexOrThrow(_stmt, "date_of_issue")
        val _columnIndexOfMonthlyPayment: Int = getColumnIndexOrThrow(_stmt, "monthly_payment")
        val _columnIndexOfConsiderDaysOff: Int = getColumnIndexOrThrow(_stmt, "consider_days_off")
        val _columnIndexOfPayOnLastDayOfMonth: Int = getColumnIndexOrThrow(_stmt,
            "pay_on_last_day_of_month")
        val _columnIndexOfApplyExtrasImmediately: Int = getColumnIndexOrThrow(_stmt,
            "apply_extras_immediately")
        val _columnIndexOfCalculateExtrasByTermLikeSberbank: Int = getColumnIndexOrThrow(_stmt,
            "interest_only_after_principal_paid_by_extra")
        val _columnIndexOfIgnorePassedPeriodsAfterRateChange: Int = getColumnIndexOrThrow(_stmt,
            "ignore_passed_periods_after_rate_change")
        val _columnIndexOfExtraDayInMonth: Int = getColumnIndexOrThrow(_stmt, "extra_day_in_month")
        val _columnIndexOfIsForecastActive: Int = getColumnIndexOrThrow(_stmt, "is_forecast_active")
        val _columnIndexOfForecastMonthlyPay: Int = getColumnIndexOrThrow(_stmt,
            "forecast_montly_pay")
        val _columnIndexOfForecastDaysBefore: Int = getColumnIndexOrThrow(_stmt,
            "forecast_days_before")
        val _columnIndexOfForecastStartDate: Int = getColumnIndexOrThrow(_stmt,
            "forecast_start_date")
        val _columnIndexOfForecastExtraType: Int = getColumnIndexOrThrow(_stmt,
            "forecast_extra_type")
        val _result: LoanEntity?
        if (_stmt.step()) {
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpCreationDate: String?
          if (_stmt.isNull(_columnIndexOfCreationDate)) {
            _tmpCreationDate = null
          } else {
            _tmpCreationDate = _stmt.getText(_columnIndexOfCreationDate)
          }
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpRate: Float?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getDouble(_columnIndexOfRate).toFloat()
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpFirstPaymentDate: String?
          if (_stmt.isNull(_columnIndexOfFirstPaymentDate)) {
            _tmpFirstPaymentDate = null
          } else {
            _tmpFirstPaymentDate = _stmt.getText(_columnIndexOfFirstPaymentDate)
          }
          val _tmpDateOfIssue: String?
          if (_stmt.isNull(_columnIndexOfDateOfIssue)) {
            _tmpDateOfIssue = null
          } else {
            _tmpDateOfIssue = _stmt.getText(_columnIndexOfDateOfIssue)
          }
          val _tmpMonthlyPayment: Float?
          if (_stmt.isNull(_columnIndexOfMonthlyPayment)) {
            _tmpMonthlyPayment = null
          } else {
            _tmpMonthlyPayment = _stmt.getDouble(_columnIndexOfMonthlyPayment).toFloat()
          }
          val _tmpConsiderDaysOff: Int?
          if (_stmt.isNull(_columnIndexOfConsiderDaysOff)) {
            _tmpConsiderDaysOff = null
          } else {
            _tmpConsiderDaysOff = _stmt.getLong(_columnIndexOfConsiderDaysOff).toInt()
          }
          val _tmpPayOnLastDayOfMonth: Int?
          if (_stmt.isNull(_columnIndexOfPayOnLastDayOfMonth)) {
            _tmpPayOnLastDayOfMonth = null
          } else {
            _tmpPayOnLastDayOfMonth = _stmt.getLong(_columnIndexOfPayOnLastDayOfMonth).toInt()
          }
          val _tmpApplyExtrasImmediately: Int?
          if (_stmt.isNull(_columnIndexOfApplyExtrasImmediately)) {
            _tmpApplyExtrasImmediately = null
          } else {
            _tmpApplyExtrasImmediately = _stmt.getLong(_columnIndexOfApplyExtrasImmediately).toInt()
          }
          val _tmpCalculateExtrasByTermLikeSberbank: Int?
          if (_stmt.isNull(_columnIndexOfCalculateExtrasByTermLikeSberbank)) {
            _tmpCalculateExtrasByTermLikeSberbank = null
          } else {
            _tmpCalculateExtrasByTermLikeSberbank =
                _stmt.getLong(_columnIndexOfCalculateExtrasByTermLikeSberbank).toInt()
          }
          val _tmpIgnorePassedPeriodsAfterRateChange: Int?
          if (_stmt.isNull(_columnIndexOfIgnorePassedPeriodsAfterRateChange)) {
            _tmpIgnorePassedPeriodsAfterRateChange = null
          } else {
            _tmpIgnorePassedPeriodsAfterRateChange =
                _stmt.getLong(_columnIndexOfIgnorePassedPeriodsAfterRateChange).toInt()
          }
          val _tmpExtraDayInMonth: Int?
          if (_stmt.isNull(_columnIndexOfExtraDayInMonth)) {
            _tmpExtraDayInMonth = null
          } else {
            _tmpExtraDayInMonth = _stmt.getLong(_columnIndexOfExtraDayInMonth).toInt()
          }
          val _tmpIsForecastActive: Int?
          if (_stmt.isNull(_columnIndexOfIsForecastActive)) {
            _tmpIsForecastActive = null
          } else {
            _tmpIsForecastActive = _stmt.getLong(_columnIndexOfIsForecastActive).toInt()
          }
          val _tmpForecastMonthlyPay: Float?
          if (_stmt.isNull(_columnIndexOfForecastMonthlyPay)) {
            _tmpForecastMonthlyPay = null
          } else {
            _tmpForecastMonthlyPay = _stmt.getDouble(_columnIndexOfForecastMonthlyPay).toFloat()
          }
          val _tmpForecastDaysBefore: Int?
          if (_stmt.isNull(_columnIndexOfForecastDaysBefore)) {
            _tmpForecastDaysBefore = null
          } else {
            _tmpForecastDaysBefore = _stmt.getLong(_columnIndexOfForecastDaysBefore).toInt()
          }
          val _tmpForecastStartDate: String?
          if (_stmt.isNull(_columnIndexOfForecastStartDate)) {
            _tmpForecastStartDate = null
          } else {
            _tmpForecastStartDate = _stmt.getText(_columnIndexOfForecastStartDate)
          }
          val _tmpForecastExtraType: Int?
          if (_stmt.isNull(_columnIndexOfForecastExtraType)) {
            _tmpForecastExtraType = null
          } else {
            _tmpForecastExtraType = _stmt.getLong(_columnIndexOfForecastExtraType).toInt()
          }
          _result =
              LoanEntity(_tmpId,_tmpCreationDate,_tmpTitle,_tmpAmount,_tmpRate,_tmpTerm,_tmpType,_tmpFirstPaymentDate,_tmpDateOfIssue,_tmpMonthlyPayment,_tmpConsiderDaysOff,_tmpPayOnLastDayOfMonth,_tmpApplyExtrasImmediately,_tmpCalculateExtrasByTermLikeSberbank,_tmpIgnorePassedPeriodsAfterRateChange,_tmpExtraDayInMonth,_tmpIsForecastActive,_tmpForecastMonthlyPay,_tmpForecastDaysBefore,_tmpForecastStartDate,_tmpForecastExtraType)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByIds(loanIds: List<Long>): List<LoanEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM Loans WHERE _id IN (")
    val _inputSize: Int = loanIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ORDER BY _id ASC")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: Long in loanIds) {
          _stmt.bindLong(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfCreationDate: Int = getColumnIndexOrThrow(_stmt, "creation_date")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfFirstPaymentDate: Int = getColumnIndexOrThrow(_stmt, "first_payment_date")
        val _columnIndexOfDateOfIssue: Int = getColumnIndexOrThrow(_stmt, "date_of_issue")
        val _columnIndexOfMonthlyPayment: Int = getColumnIndexOrThrow(_stmt, "monthly_payment")
        val _columnIndexOfConsiderDaysOff: Int = getColumnIndexOrThrow(_stmt, "consider_days_off")
        val _columnIndexOfPayOnLastDayOfMonth: Int = getColumnIndexOrThrow(_stmt,
            "pay_on_last_day_of_month")
        val _columnIndexOfApplyExtrasImmediately: Int = getColumnIndexOrThrow(_stmt,
            "apply_extras_immediately")
        val _columnIndexOfCalculateExtrasByTermLikeSberbank: Int = getColumnIndexOrThrow(_stmt,
            "interest_only_after_principal_paid_by_extra")
        val _columnIndexOfIgnorePassedPeriodsAfterRateChange: Int = getColumnIndexOrThrow(_stmt,
            "ignore_passed_periods_after_rate_change")
        val _columnIndexOfExtraDayInMonth: Int = getColumnIndexOrThrow(_stmt, "extra_day_in_month")
        val _columnIndexOfIsForecastActive: Int = getColumnIndexOrThrow(_stmt, "is_forecast_active")
        val _columnIndexOfForecastMonthlyPay: Int = getColumnIndexOrThrow(_stmt,
            "forecast_montly_pay")
        val _columnIndexOfForecastDaysBefore: Int = getColumnIndexOrThrow(_stmt,
            "forecast_days_before")
        val _columnIndexOfForecastStartDate: Int = getColumnIndexOrThrow(_stmt,
            "forecast_start_date")
        val _columnIndexOfForecastExtraType: Int = getColumnIndexOrThrow(_stmt,
            "forecast_extra_type")
        val _result: MutableList<LoanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: LoanEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpCreationDate: String?
          if (_stmt.isNull(_columnIndexOfCreationDate)) {
            _tmpCreationDate = null
          } else {
            _tmpCreationDate = _stmt.getText(_columnIndexOfCreationDate)
          }
          val _tmpTitle: String?
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpRate: Float?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getDouble(_columnIndexOfRate).toFloat()
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpFirstPaymentDate: String?
          if (_stmt.isNull(_columnIndexOfFirstPaymentDate)) {
            _tmpFirstPaymentDate = null
          } else {
            _tmpFirstPaymentDate = _stmt.getText(_columnIndexOfFirstPaymentDate)
          }
          val _tmpDateOfIssue: String?
          if (_stmt.isNull(_columnIndexOfDateOfIssue)) {
            _tmpDateOfIssue = null
          } else {
            _tmpDateOfIssue = _stmt.getText(_columnIndexOfDateOfIssue)
          }
          val _tmpMonthlyPayment: Float?
          if (_stmt.isNull(_columnIndexOfMonthlyPayment)) {
            _tmpMonthlyPayment = null
          } else {
            _tmpMonthlyPayment = _stmt.getDouble(_columnIndexOfMonthlyPayment).toFloat()
          }
          val _tmpConsiderDaysOff: Int?
          if (_stmt.isNull(_columnIndexOfConsiderDaysOff)) {
            _tmpConsiderDaysOff = null
          } else {
            _tmpConsiderDaysOff = _stmt.getLong(_columnIndexOfConsiderDaysOff).toInt()
          }
          val _tmpPayOnLastDayOfMonth: Int?
          if (_stmt.isNull(_columnIndexOfPayOnLastDayOfMonth)) {
            _tmpPayOnLastDayOfMonth = null
          } else {
            _tmpPayOnLastDayOfMonth = _stmt.getLong(_columnIndexOfPayOnLastDayOfMonth).toInt()
          }
          val _tmpApplyExtrasImmediately: Int?
          if (_stmt.isNull(_columnIndexOfApplyExtrasImmediately)) {
            _tmpApplyExtrasImmediately = null
          } else {
            _tmpApplyExtrasImmediately = _stmt.getLong(_columnIndexOfApplyExtrasImmediately).toInt()
          }
          val _tmpCalculateExtrasByTermLikeSberbank: Int?
          if (_stmt.isNull(_columnIndexOfCalculateExtrasByTermLikeSberbank)) {
            _tmpCalculateExtrasByTermLikeSberbank = null
          } else {
            _tmpCalculateExtrasByTermLikeSberbank =
                _stmt.getLong(_columnIndexOfCalculateExtrasByTermLikeSberbank).toInt()
          }
          val _tmpIgnorePassedPeriodsAfterRateChange: Int?
          if (_stmt.isNull(_columnIndexOfIgnorePassedPeriodsAfterRateChange)) {
            _tmpIgnorePassedPeriodsAfterRateChange = null
          } else {
            _tmpIgnorePassedPeriodsAfterRateChange =
                _stmt.getLong(_columnIndexOfIgnorePassedPeriodsAfterRateChange).toInt()
          }
          val _tmpExtraDayInMonth: Int?
          if (_stmt.isNull(_columnIndexOfExtraDayInMonth)) {
            _tmpExtraDayInMonth = null
          } else {
            _tmpExtraDayInMonth = _stmt.getLong(_columnIndexOfExtraDayInMonth).toInt()
          }
          val _tmpIsForecastActive: Int?
          if (_stmt.isNull(_columnIndexOfIsForecastActive)) {
            _tmpIsForecastActive = null
          } else {
            _tmpIsForecastActive = _stmt.getLong(_columnIndexOfIsForecastActive).toInt()
          }
          val _tmpForecastMonthlyPay: Float?
          if (_stmt.isNull(_columnIndexOfForecastMonthlyPay)) {
            _tmpForecastMonthlyPay = null
          } else {
            _tmpForecastMonthlyPay = _stmt.getDouble(_columnIndexOfForecastMonthlyPay).toFloat()
          }
          val _tmpForecastDaysBefore: Int?
          if (_stmt.isNull(_columnIndexOfForecastDaysBefore)) {
            _tmpForecastDaysBefore = null
          } else {
            _tmpForecastDaysBefore = _stmt.getLong(_columnIndexOfForecastDaysBefore).toInt()
          }
          val _tmpForecastStartDate: String?
          if (_stmt.isNull(_columnIndexOfForecastStartDate)) {
            _tmpForecastStartDate = null
          } else {
            _tmpForecastStartDate = _stmt.getText(_columnIndexOfForecastStartDate)
          }
          val _tmpForecastExtraType: Int?
          if (_stmt.isNull(_columnIndexOfForecastExtraType)) {
            _tmpForecastExtraType = null
          } else {
            _tmpForecastExtraType = _stmt.getLong(_columnIndexOfForecastExtraType).toInt()
          }
          _item_1 =
              LoanEntity(_tmpId,_tmpCreationDate,_tmpTitle,_tmpAmount,_tmpRate,_tmpTerm,_tmpType,_tmpFirstPaymentDate,_tmpDateOfIssue,_tmpMonthlyPayment,_tmpConsiderDaysOff,_tmpPayOnLastDayOfMonth,_tmpApplyExtrasImmediately,_tmpCalculateExtrasByTermLikeSberbank,_tmpIgnorePassedPeriodsAfterRateChange,_tmpExtraDayInMonth,_tmpIsForecastActive,_tmpForecastMonthlyPay,_tmpForecastDaysBefore,_tmpForecastStartDate,_tmpForecastExtraType)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countLoansCreatedOnDate(creationDate: String, excludeLoanId: Long):
      Int {
    val _sql: String = "SELECT COUNT(*) + 1 FROM Loans WHERE creation_date = ? AND _id != ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, creationDate)
        _argIndex = 2
        _stmt.bindLong(_argIndex, excludeLoanId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(loanId: Long) {
    val _sql: String = "DELETE FROM Loans WHERE _id = ?"
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

  public override suspend fun deleteByIds(loanIds: List<Long>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM Loans WHERE _id IN (")
    val _inputSize: Int = loanIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: Long in loanIds) {
          _stmt.bindLong(_argIndex, _item)
          _argIndex++
        }
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
