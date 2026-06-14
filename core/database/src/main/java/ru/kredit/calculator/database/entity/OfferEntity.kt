package ru.kredit.calculator.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kredit.calculator.database.DatabaseContract

@Entity(tableName = DatabaseContract.OfferColumns.TABLE_NAME)
data class OfferEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.OfferColumns.ID)
    val id: Long = 0,
    @ColumnInfo(name = DatabaseContract.OfferColumns.NAME)
    val name: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.ORG_NAME)
    val orgName: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.TERM)
    val term: Int? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.RATE_TYPE)
    val rateType: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.RATE)
    val rate: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.LOGO_COLOR)
    val logoColor: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.LOGO_IMAGE)
    val logoImage: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.LINK)
    val link: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.LIMIT)
    val amountLimit: Double? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.REQUIREMENTS)
    val requirements: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.EXTRA_PAYMENT_RULES)
    val extraPaymentRules: String? = null,
    @ColumnInfo(name = DatabaseContract.OfferColumns.DOCUMENTS)
    val docs: String? = null,
)
