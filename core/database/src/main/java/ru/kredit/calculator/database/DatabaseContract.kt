package ru.kredit.calculator.database

object DatabaseContract {
    const val DATABASE_NAME = "main.db"
    const val DATABASE_VERSION = 8

    object LoanColumns {
        const val TABLE_NAME = "Loans"
        const val ID = "_id"
        const val CREATION_DATE = "creation_date"
        const val TITLE = "title"
        const val AMOUNT = "amount"
        const val RATE = "rate"
        const val TERM = "term"
        const val TYPE = "type"
        const val FIRST_PAYMENT_DATE = "first_payment_date"
        const val MONTHLY_PAYMENT = "monthly_payment"
        const val DATE_OF_ISSUE = "date_of_issue"
        const val FLAG_CONSIDER_DAYS_OFF = "consider_days_off"
        const val FLAG_PAY_ON_LAST_DAY_OF_MONTH = "pay_on_last_day_of_month"
        const val APPLY_EXTRAS_IMMEDIATELY = "apply_extras_immediately"
        const val CALCULATE_EXTRAS_BY_TERM_LIKE_SBERBANK = "interest_only_after_principal_paid_by_extra"
        const val IGNORE_PASSED_PERIODS_AFTER_RATE_CHANGE = "ignore_passed_periods_after_rate_change"
        const val EXTRA_DAY_IN_MONTH = "extra_day_in_month"
        const val IS_FORECAST_ACTIVE = "is_forecast_active"
        const val FORECAST_MONTHLY_PAY = "forecast_montly_pay"
        const val FORECAST_DAYS_BEFORE = "forecast_days_before"
        const val FORECAST_START_DATE = "forecast_start_date"
        const val FORECAST_EXTRA_TYPE = "forecast_extra_type"
    }

    object ExtraColumns {
        const val TABLE_NAME = "extras"
        const val ID = "_id"
        const val AMOUNT = "amount"
        const val TYPE = "type"
        const val DATE = "date"
        const val DOCUMENT_NUMBER = "document_number"
        const val LOAN_ID = "loanId"
    }

    object OfferColumns {
        const val TABLE_NAME = "Offers"
        const val ID = "_id"
        const val NAME = "name"
        const val RATE_TYPE = "rate_type"
        const val RATE = "rate"
        const val ORG_NAME = "org_name"
        const val TERM = "term"
        const val LOGO_IMAGE = "logo_image"
        const val LOGO_COLOR = "logo_color"
        const val LINK = "link"
        const val LIMIT = "amount_limit"
        const val REQUIREMENTS = "requirements"
        const val DOCUMENTS = "docs"
        const val EXTRA_PAYMENT_RULES = "extra_payment_rules"
    }

    object LoanDetailsColumns {
        const val TABLE_NAME = "loan_details"
        const val LOAN_ID = "loan_id"
        const val BANK_NAME = "bank_name"
        const val ACCOUNT_NUMBER = "account_number"
        const val UIC = "uic"
        const val CORRESPONDENT_ACCOUNT = "correspondent_account"
        const val PAYMENT_COMMENT = "payment_comment"
    }

    object Version {
        const val INITIAL = 1
        const val ADD_CREATION_DATE_COLUMN_TO_LOAN = 2
        const val ADD_EXTRA_CALCULATION_PARAMETERS_TO_LOAN = 3
        const val ADD_IGNORE_PASSED_PERIODS_AFTER_RATE_CHANGE_COLUMN_TO_LOAN = 4
        const val ADD_EXTRA_DAY_IN_MONTH_COLUMN_TO_LOAN = 5
        const val ADD_OFFERS_TABLE = 6
        const val ADD_FORECAST = 7
        const val ADD_LOAN_DETAILS = 8
    }
}
