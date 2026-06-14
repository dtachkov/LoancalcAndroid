package ru.kredit.calculator.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DatabaseMigrations {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val ALL: Array<Migration> = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
    )

    private val MIGRATION_1_2 = Migration(1, 2) { database ->
        val today = dateFormat.format(Date())
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.CREATION_DATE} TEXT DEFAULT '$today'"
        )
    }

    private val MIGRATION_2_3 = Migration(2, 3) { database ->
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.APPLY_EXTRAS_IMMEDIATELY} INTEGER DEFAULT 0"
        )
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.CALCULATE_EXTRAS_BY_TERM_LIKE_SBERBANK} INTEGER DEFAULT 1"
        )
    }

    private val MIGRATION_3_4 = Migration(3, 4) { database ->
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.IGNORE_PASSED_PERIODS_AFTER_RATE_CHANGE} INTEGER DEFAULT 0"
        )
    }

    private val MIGRATION_4_5 = Migration(4, 5) { database ->
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.EXTRA_DAY_IN_MONTH} INTEGER DEFAULT 0"
        )
    }

    private val MIGRATION_5_6 = Migration(5, 6) { database ->
        database.execSQL(
            """
            CREATE TABLE ${DatabaseContract.OfferColumns.TABLE_NAME} (
                ${DatabaseContract.OfferColumns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.OfferColumns.NAME} TEXT,
                ${DatabaseContract.OfferColumns.ORG_NAME} TEXT,
                ${DatabaseContract.OfferColumns.TERM} INTEGER,
                ${DatabaseContract.OfferColumns.RATE_TYPE} TEXT,
                ${DatabaseContract.OfferColumns.RATE} TEXT,
                ${DatabaseContract.OfferColumns.LOGO_COLOR} TEXT,
                ${DatabaseContract.OfferColumns.LOGO_IMAGE} TEXT,
                ${DatabaseContract.OfferColumns.LINK} TEXT,
                ${DatabaseContract.OfferColumns.LIMIT} FLOAT,
                ${DatabaseContract.OfferColumns.REQUIREMENTS} TEXT,
                ${DatabaseContract.OfferColumns.EXTRA_PAYMENT_RULES} TEXT,
                ${DatabaseContract.OfferColumns.DOCUMENTS} TEXT
            )
            """.trimIndent()
        )
    }

    private val MIGRATION_6_7 = Migration(6, 7) { database ->
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.IS_FORECAST_ACTIVE} INTEGER DEFAULT 0"
        )
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.FORECAST_MONTHLY_PAY} FLOAT"
        )
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.FORECAST_DAYS_BEFORE} INTEGER"
        )
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.FORECAST_START_DATE} TEXT"
        )
        database.execSQL(
            "ALTER TABLE ${DatabaseContract.LoanColumns.TABLE_NAME} " +
                "ADD COLUMN ${DatabaseContract.LoanColumns.FORECAST_EXTRA_TYPE} INTEGER"
        )
    }
}
