package ru.kredit.calculator.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass
import ru.kredit.calculator.database.dao.ExtraDao
import ru.kredit.calculator.database.dao.ExtraDao_Impl
import ru.kredit.calculator.database.dao.LoanDao
import ru.kredit.calculator.database.dao.LoanDao_Impl
import ru.kredit.calculator.database.dao.OfferDao
import ru.kredit.calculator.database.dao.OfferDao_Impl

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LoancalcDatabase_Impl : LoancalcDatabase() {
  private val _loanDao: Lazy<LoanDao> = lazy {
    LoanDao_Impl(this)
  }

  private val _extraDao: Lazy<ExtraDao> = lazy {
    ExtraDao_Impl(this)
  }

  private val _offerDao: Lazy<OfferDao> = lazy {
    OfferDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(7,
        "98974e8ce5981b83c4327760e0e136b7", "df97aac265103bce3f4ea06fb7f3cc9f") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Loans` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `creation_date` TEXT, `title` TEXT, `amount` REAL, `rate` REAL, `term` INTEGER, `type` INTEGER, `first_payment_date` TEXT, `date_of_issue` TEXT, `monthly_payment` REAL, `consider_days_off` INTEGER, `pay_on_last_day_of_month` INTEGER, `apply_extras_immediately` INTEGER DEFAULT 0, `interest_only_after_principal_paid_by_extra` INTEGER DEFAULT 1, `ignore_passed_periods_after_rate_change` INTEGER DEFAULT 0, `extra_day_in_month` INTEGER DEFAULT 0, `is_forecast_active` INTEGER DEFAULT 0, `forecast_montly_pay` REAL, `forecast_days_before` INTEGER, `forecast_start_date` TEXT, `forecast_extra_type` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `extras` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `document_number` TEXT, `type` INTEGER, `date` TEXT, `amount` REAL, `loanId` INTEGER)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `Offers` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT, `org_name` TEXT, `term` INTEGER, `rate_type` TEXT, `rate` TEXT, `logo_color` TEXT, `logo_image` TEXT, `link` TEXT, `amount_limit` REAL, `requirements` TEXT, `extra_payment_rules` TEXT, `docs` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '98974e8ce5981b83c4327760e0e136b7')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `Loans`")
        connection.execSQL("DROP TABLE IF EXISTS `extras`")
        connection.execSQL("DROP TABLE IF EXISTS `Offers`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsLoans: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLoans.put("_id", TableInfo.Column("_id", "INTEGER", false, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("creation_date", TableInfo.Column("creation_date", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("title", TableInfo.Column("title", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("amount", TableInfo.Column("amount", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("rate", TableInfo.Column("rate", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("term", TableInfo.Column("term", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("type", TableInfo.Column("type", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("first_payment_date", TableInfo.Column("first_payment_date", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("date_of_issue", TableInfo.Column("date_of_issue", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("monthly_payment", TableInfo.Column("monthly_payment", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("consider_days_off", TableInfo.Column("consider_days_off", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("pay_on_last_day_of_month", TableInfo.Column("pay_on_last_day_of_month",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("apply_extras_immediately", TableInfo.Column("apply_extras_immediately",
            "INTEGER", false, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("interest_only_after_principal_paid_by_extra",
            TableInfo.Column("interest_only_after_principal_paid_by_extra", "INTEGER", false, 0,
            "1", TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("ignore_passed_periods_after_rate_change",
            TableInfo.Column("ignore_passed_periods_after_rate_change", "INTEGER", false, 0, "0",
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("extra_day_in_month", TableInfo.Column("extra_day_in_month", "INTEGER",
            false, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("is_forecast_active", TableInfo.Column("is_forecast_active", "INTEGER",
            false, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("forecast_montly_pay", TableInfo.Column("forecast_montly_pay", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("forecast_days_before", TableInfo.Column("forecast_days_before",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("forecast_start_date", TableInfo.Column("forecast_start_date", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLoans.put("forecast_extra_type", TableInfo.Column("forecast_extra_type", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLoans: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLoans: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLoans: TableInfo = TableInfo("Loans", _columnsLoans, _foreignKeysLoans,
            _indicesLoans)
        val _existingLoans: TableInfo = read(connection, "Loans")
        if (!_infoLoans.equals(_existingLoans)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |Loans(ru.kredit.calculator.database.entity.LoanEntity).
              | Expected:
              |""".trimMargin() + _infoLoans + """
              |
              | Found:
              |""".trimMargin() + _existingLoans)
        }
        val _columnsExtras: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExtras.put("_id", TableInfo.Column("_id", "INTEGER", false, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExtras.put("document_number", TableInfo.Column("document_number", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExtras.put("type", TableInfo.Column("type", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExtras.put("date", TableInfo.Column("date", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExtras.put("amount", TableInfo.Column("amount", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExtras.put("loanId", TableInfo.Column("loanId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExtras: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExtras: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExtras: TableInfo = TableInfo("extras", _columnsExtras, _foreignKeysExtras,
            _indicesExtras)
        val _existingExtras: TableInfo = read(connection, "extras")
        if (!_infoExtras.equals(_existingExtras)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |extras(ru.kredit.calculator.database.entity.ExtraEntity).
              | Expected:
              |""".trimMargin() + _infoExtras + """
              |
              | Found:
              |""".trimMargin() + _existingExtras)
        }
        val _columnsOffers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOffers.put("_id", TableInfo.Column("_id", "INTEGER", false, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("name", TableInfo.Column("name", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("org_name", TableInfo.Column("org_name", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("term", TableInfo.Column("term", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("rate_type", TableInfo.Column("rate_type", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("rate", TableInfo.Column("rate", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("logo_color", TableInfo.Column("logo_color", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("logo_image", TableInfo.Column("logo_image", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("link", TableInfo.Column("link", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("amount_limit", TableInfo.Column("amount_limit", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("requirements", TableInfo.Column("requirements", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("extra_payment_rules", TableInfo.Column("extra_payment_rules", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOffers.put("docs", TableInfo.Column("docs", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOffers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOffers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoOffers: TableInfo = TableInfo("Offers", _columnsOffers, _foreignKeysOffers,
            _indicesOffers)
        val _existingOffers: TableInfo = read(connection, "Offers")
        if (!_infoOffers.equals(_existingOffers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |Offers(ru.kredit.calculator.database.entity.OfferEntity).
              | Expected:
              |""".trimMargin() + _infoOffers + """
              |
              | Found:
              |""".trimMargin() + _existingOffers)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "Loans", "extras", "Offers")
  }

  public override fun clearAllTables() {
    super.performClear(false, "Loans", "extras", "Offers")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(LoanDao::class, LoanDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExtraDao::class, ExtraDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OfferDao::class, OfferDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun loanDao(): LoanDao = _loanDao.value

  public override fun extraDao(): ExtraDao = _extraDao.value

  public override fun offerDao(): OfferDao = _offerDao.value
}
