package ru.kredit.calculator.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
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
import kotlinx.coroutines.flow.Flow
import ru.kredit.calculator.database.entity.OfferEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class OfferDao_Impl(
  __db: RoomDatabase,
) : OfferDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfOfferEntity: EntityInsertAdapter<OfferEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfOfferEntity = object : EntityInsertAdapter<OfferEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `Offers` (`_id`,`name`,`org_name`,`term`,`rate_type`,`rate`,`logo_color`,`logo_image`,`link`,`amount_limit`,`requirements`,`extra_payment_rules`,`docs`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: OfferEntity) {
        val _tmpId: Long? = entity.id
        if (_tmpId == null) {
          statement.bindNull(1)
        } else {
          statement.bindLong(1, _tmpId)
        }
        val _tmpName: String? = entity.name
        if (_tmpName == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpName)
        }
        val _tmpOrgName: String? = entity.orgName
        if (_tmpOrgName == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpOrgName)
        }
        val _tmpTerm: Int? = entity.term
        if (_tmpTerm == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpTerm.toLong())
        }
        val _tmpRateType: String? = entity.rateType
        if (_tmpRateType == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpRateType)
        }
        val _tmpRate: String? = entity.rate
        if (_tmpRate == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRate)
        }
        val _tmpLogoColor: String? = entity.logoColor
        if (_tmpLogoColor == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpLogoColor)
        }
        val _tmpLogoImage: String? = entity.logoImage
        if (_tmpLogoImage == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpLogoImage)
        }
        val _tmpLink: String? = entity.link
        if (_tmpLink == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpLink)
        }
        val _tmpAmountLimit: Float? = entity.amountLimit
        if (_tmpAmountLimit == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpAmountLimit.toDouble())
        }
        val _tmpRequirements: String? = entity.requirements
        if (_tmpRequirements == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpRequirements)
        }
        val _tmpExtraPaymentRules: String? = entity.extraPaymentRules
        if (_tmpExtraPaymentRules == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpExtraPaymentRules)
        }
        val _tmpDocs: String? = entity.docs
        if (_tmpDocs == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpDocs)
        }
      }
    }
  }

  public override suspend fun insertAll(offers: List<OfferEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfOfferEntity.insert(_connection, offers)
  }

  public override suspend fun replaceAll(offers: List<OfferEntity>): Unit =
      performInTransactionSuspending(__db) {
    super@OfferDao_Impl.replaceAll(offers)
  }

  public override fun observeAll(): Flow<List<OfferEntity>> {
    val _sql: String = "SELECT * FROM Offers ORDER BY _id ASC"
    return createFlow(__db, false, arrayOf("Offers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfOrgName: Int = getColumnIndexOrThrow(_stmt, "org_name")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfRateType: Int = getColumnIndexOrThrow(_stmt, "rate_type")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfLogoColor: Int = getColumnIndexOrThrow(_stmt, "logo_color")
        val _columnIndexOfLogoImage: Int = getColumnIndexOrThrow(_stmt, "logo_image")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfAmountLimit: Int = getColumnIndexOrThrow(_stmt, "amount_limit")
        val _columnIndexOfRequirements: Int = getColumnIndexOrThrow(_stmt, "requirements")
        val _columnIndexOfExtraPaymentRules: Int = getColumnIndexOrThrow(_stmt,
            "extra_payment_rules")
        val _columnIndexOfDocs: Int = getColumnIndexOrThrow(_stmt, "docs")
        val _result: MutableList<OfferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OfferEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpName: String?
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName)
          }
          val _tmpOrgName: String?
          if (_stmt.isNull(_columnIndexOfOrgName)) {
            _tmpOrgName = null
          } else {
            _tmpOrgName = _stmt.getText(_columnIndexOfOrgName)
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpRateType: String?
          if (_stmt.isNull(_columnIndexOfRateType)) {
            _tmpRateType = null
          } else {
            _tmpRateType = _stmt.getText(_columnIndexOfRateType)
          }
          val _tmpRate: String?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getText(_columnIndexOfRate)
          }
          val _tmpLogoColor: String?
          if (_stmt.isNull(_columnIndexOfLogoColor)) {
            _tmpLogoColor = null
          } else {
            _tmpLogoColor = _stmt.getText(_columnIndexOfLogoColor)
          }
          val _tmpLogoImage: String?
          if (_stmt.isNull(_columnIndexOfLogoImage)) {
            _tmpLogoImage = null
          } else {
            _tmpLogoImage = _stmt.getText(_columnIndexOfLogoImage)
          }
          val _tmpLink: String?
          if (_stmt.isNull(_columnIndexOfLink)) {
            _tmpLink = null
          } else {
            _tmpLink = _stmt.getText(_columnIndexOfLink)
          }
          val _tmpAmountLimit: Float?
          if (_stmt.isNull(_columnIndexOfAmountLimit)) {
            _tmpAmountLimit = null
          } else {
            _tmpAmountLimit = _stmt.getDouble(_columnIndexOfAmountLimit).toFloat()
          }
          val _tmpRequirements: String?
          if (_stmt.isNull(_columnIndexOfRequirements)) {
            _tmpRequirements = null
          } else {
            _tmpRequirements = _stmt.getText(_columnIndexOfRequirements)
          }
          val _tmpExtraPaymentRules: String?
          if (_stmt.isNull(_columnIndexOfExtraPaymentRules)) {
            _tmpExtraPaymentRules = null
          } else {
            _tmpExtraPaymentRules = _stmt.getText(_columnIndexOfExtraPaymentRules)
          }
          val _tmpDocs: String?
          if (_stmt.isNull(_columnIndexOfDocs)) {
            _tmpDocs = null
          } else {
            _tmpDocs = _stmt.getText(_columnIndexOfDocs)
          }
          _item =
              OfferEntity(_tmpId,_tmpName,_tmpOrgName,_tmpTerm,_tmpRateType,_tmpRate,_tmpLogoColor,_tmpLogoImage,_tmpLink,_tmpAmountLimit,_tmpRequirements,_tmpExtraPaymentRules,_tmpDocs)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<OfferEntity> {
    val _sql: String = "SELECT * FROM Offers ORDER BY _id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfOrgName: Int = getColumnIndexOrThrow(_stmt, "org_name")
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfRateType: Int = getColumnIndexOrThrow(_stmt, "rate_type")
        val _columnIndexOfRate: Int = getColumnIndexOrThrow(_stmt, "rate")
        val _columnIndexOfLogoColor: Int = getColumnIndexOrThrow(_stmt, "logo_color")
        val _columnIndexOfLogoImage: Int = getColumnIndexOrThrow(_stmt, "logo_image")
        val _columnIndexOfLink: Int = getColumnIndexOrThrow(_stmt, "link")
        val _columnIndexOfAmountLimit: Int = getColumnIndexOrThrow(_stmt, "amount_limit")
        val _columnIndexOfRequirements: Int = getColumnIndexOrThrow(_stmt, "requirements")
        val _columnIndexOfExtraPaymentRules: Int = getColumnIndexOrThrow(_stmt,
            "extra_payment_rules")
        val _columnIndexOfDocs: Int = getColumnIndexOrThrow(_stmt, "docs")
        val _result: MutableList<OfferEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: OfferEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpName: String?
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName)
          }
          val _tmpOrgName: String?
          if (_stmt.isNull(_columnIndexOfOrgName)) {
            _tmpOrgName = null
          } else {
            _tmpOrgName = _stmt.getText(_columnIndexOfOrgName)
          }
          val _tmpTerm: Int?
          if (_stmt.isNull(_columnIndexOfTerm)) {
            _tmpTerm = null
          } else {
            _tmpTerm = _stmt.getLong(_columnIndexOfTerm).toInt()
          }
          val _tmpRateType: String?
          if (_stmt.isNull(_columnIndexOfRateType)) {
            _tmpRateType = null
          } else {
            _tmpRateType = _stmt.getText(_columnIndexOfRateType)
          }
          val _tmpRate: String?
          if (_stmt.isNull(_columnIndexOfRate)) {
            _tmpRate = null
          } else {
            _tmpRate = _stmt.getText(_columnIndexOfRate)
          }
          val _tmpLogoColor: String?
          if (_stmt.isNull(_columnIndexOfLogoColor)) {
            _tmpLogoColor = null
          } else {
            _tmpLogoColor = _stmt.getText(_columnIndexOfLogoColor)
          }
          val _tmpLogoImage: String?
          if (_stmt.isNull(_columnIndexOfLogoImage)) {
            _tmpLogoImage = null
          } else {
            _tmpLogoImage = _stmt.getText(_columnIndexOfLogoImage)
          }
          val _tmpLink: String?
          if (_stmt.isNull(_columnIndexOfLink)) {
            _tmpLink = null
          } else {
            _tmpLink = _stmt.getText(_columnIndexOfLink)
          }
          val _tmpAmountLimit: Float?
          if (_stmt.isNull(_columnIndexOfAmountLimit)) {
            _tmpAmountLimit = null
          } else {
            _tmpAmountLimit = _stmt.getDouble(_columnIndexOfAmountLimit).toFloat()
          }
          val _tmpRequirements: String?
          if (_stmt.isNull(_columnIndexOfRequirements)) {
            _tmpRequirements = null
          } else {
            _tmpRequirements = _stmt.getText(_columnIndexOfRequirements)
          }
          val _tmpExtraPaymentRules: String?
          if (_stmt.isNull(_columnIndexOfExtraPaymentRules)) {
            _tmpExtraPaymentRules = null
          } else {
            _tmpExtraPaymentRules = _stmt.getText(_columnIndexOfExtraPaymentRules)
          }
          val _tmpDocs: String?
          if (_stmt.isNull(_columnIndexOfDocs)) {
            _tmpDocs = null
          } else {
            _tmpDocs = _stmt.getText(_columnIndexOfDocs)
          }
          _item =
              OfferEntity(_tmpId,_tmpName,_tmpOrgName,_tmpTerm,_tmpRateType,_tmpRate,_tmpLogoColor,_tmpLogoImage,_tmpLink,_tmpAmountLimit,_tmpRequirements,_tmpExtraPaymentRules,_tmpDocs)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM Offers"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
