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
import ru.kredit.calculator.database.entity.ExtraEntity

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ExtraDao_Impl(
  __db: RoomDatabase,
) : ExtraDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfExtraEntity: EntityInsertAdapter<ExtraEntity>

  private val __updateAdapterOfExtraEntity: EntityDeleteOrUpdateAdapter<ExtraEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfExtraEntity = object : EntityInsertAdapter<ExtraEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `extras` (`_id`,`document_number`,`type`,`date`,`amount`,`loanId`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExtraEntity) {
        val _tmpId: Long? = entity.id
        if (_tmpId == null) {
          statement.bindNull(1)
        } else {
          statement.bindLong(1, _tmpId)
        }
        val _tmpDocumentNumber: String? = entity.documentNumber
        if (_tmpDocumentNumber == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpDocumentNumber)
        }
        val _tmpType: Int? = entity.type
        if (_tmpType == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpType.toLong())
        }
        val _tmpDate: String? = entity.date
        if (_tmpDate == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDate)
        }
        val _tmpAmount: Float? = entity.amount
        if (_tmpAmount == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpAmount.toDouble())
        }
        val _tmpLoanId: Long? = entity.loanId
        if (_tmpLoanId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpLoanId)
        }
      }
    }
    this.__updateAdapterOfExtraEntity = object : EntityDeleteOrUpdateAdapter<ExtraEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `extras` SET `_id` = ?,`document_number` = ?,`type` = ?,`date` = ?,`amount` = ?,`loanId` = ? WHERE `_id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ExtraEntity) {
        val _tmpId: Long? = entity.id
        if (_tmpId == null) {
          statement.bindNull(1)
        } else {
          statement.bindLong(1, _tmpId)
        }
        val _tmpDocumentNumber: String? = entity.documentNumber
        if (_tmpDocumentNumber == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpDocumentNumber)
        }
        val _tmpType: Int? = entity.type
        if (_tmpType == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpType.toLong())
        }
        val _tmpDate: String? = entity.date
        if (_tmpDate == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDate)
        }
        val _tmpAmount: Float? = entity.amount
        if (_tmpAmount == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpAmount.toDouble())
        }
        val _tmpLoanId: Long? = entity.loanId
        if (_tmpLoanId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpLoanId)
        }
        val _tmpId_1: Long? = entity.id
        if (_tmpId_1 == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpId_1)
        }
      }
    }
  }

  public override suspend fun insert(extra: ExtraEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfExtraEntity.insertAndReturnId(_connection, extra)
    _result
  }

  public override suspend fun update(extra: ExtraEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfExtraEntity.handle(_connection, extra)
  }

  public override fun observeCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM extras"
    return createFlow(__db, false, arrayOf("extras")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun observeByLoanId(loanId: Long): Flow<List<ExtraEntity>> {
    val _sql: String = "SELECT * FROM extras WHERE loanId = ? ORDER BY _id ASC"
    return createFlow(__db, false, arrayOf("extras")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfDocumentNumber: Int = getColumnIndexOrThrow(_stmt, "document_number")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loanId")
        val _result: MutableList<ExtraEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExtraEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpDocumentNumber: String?
          if (_stmt.isNull(_columnIndexOfDocumentNumber)) {
            _tmpDocumentNumber = null
          } else {
            _tmpDocumentNumber = _stmt.getText(_columnIndexOfDocumentNumber)
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpDate: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpLoanId: Long?
          if (_stmt.isNull(_columnIndexOfLoanId)) {
            _tmpLoanId = null
          } else {
            _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          }
          _item = ExtraEntity(_tmpId,_tmpDocumentNumber,_tmpType,_tmpDate,_tmpAmount,_tmpLoanId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByLoanId(loanId: Long): List<ExtraEntity> {
    val _sql: String = "SELECT * FROM extras WHERE loanId = ? ORDER BY _id ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfDocumentNumber: Int = getColumnIndexOrThrow(_stmt, "document_number")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loanId")
        val _result: MutableList<ExtraEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExtraEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpDocumentNumber: String?
          if (_stmt.isNull(_columnIndexOfDocumentNumber)) {
            _tmpDocumentNumber = null
          } else {
            _tmpDocumentNumber = _stmt.getText(_columnIndexOfDocumentNumber)
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpDate: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpLoanId: Long?
          if (_stmt.isNull(_columnIndexOfLoanId)) {
            _tmpLoanId = null
          } else {
            _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          }
          _item = ExtraEntity(_tmpId,_tmpDocumentNumber,_tmpType,_tmpDate,_tmpAmount,_tmpLoanId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByLoanIds(loanIds: List<Long>): List<ExtraEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM extras WHERE loanId IN (")
    val _inputSize: Int = loanIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ORDER BY loanId ASC, _id ASC")
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
        val _columnIndexOfDocumentNumber: Int = getColumnIndexOrThrow(_stmt, "document_number")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loanId")
        val _result: MutableList<ExtraEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: ExtraEntity
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpDocumentNumber: String?
          if (_stmt.isNull(_columnIndexOfDocumentNumber)) {
            _tmpDocumentNumber = null
          } else {
            _tmpDocumentNumber = _stmt.getText(_columnIndexOfDocumentNumber)
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpDate: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpLoanId: Long?
          if (_stmt.isNull(_columnIndexOfLoanId)) {
            _tmpLoanId = null
          } else {
            _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          }
          _item_1 = ExtraEntity(_tmpId,_tmpDocumentNumber,_tmpType,_tmpDate,_tmpAmount,_tmpLoanId)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByLoanAndExtraId(loanId: Long, extraId: Long): ExtraEntity? {
    val _sql: String = "SELECT * FROM extras WHERE loanId = ? AND _id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, extraId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "_id")
        val _columnIndexOfDocumentNumber: Int = getColumnIndexOrThrow(_stmt, "document_number")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfLoanId: Int = getColumnIndexOrThrow(_stmt, "loanId")
        val _result: ExtraEntity?
        if (_stmt.step()) {
          val _tmpId: Long?
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId)
          }
          val _tmpDocumentNumber: String?
          if (_stmt.isNull(_columnIndexOfDocumentNumber)) {
            _tmpDocumentNumber = null
          } else {
            _tmpDocumentNumber = _stmt.getText(_columnIndexOfDocumentNumber)
          }
          val _tmpType: Int?
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType).toInt()
          }
          val _tmpDate: String?
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate)
          }
          val _tmpAmount: Float?
          if (_stmt.isNull(_columnIndexOfAmount)) {
            _tmpAmount = null
          } else {
            _tmpAmount = _stmt.getDouble(_columnIndexOfAmount).toFloat()
          }
          val _tmpLoanId: Long?
          if (_stmt.isNull(_columnIndexOfLoanId)) {
            _tmpLoanId = null
          } else {
            _tmpLoanId = _stmt.getLong(_columnIndexOfLoanId)
          }
          _result = ExtraEntity(_tmpId,_tmpDocumentNumber,_tmpType,_tmpDate,_tmpAmount,_tmpLoanId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countExtrasByTypes(
    loanId: Long,
    excludeExtraId: Long,
    types: List<Int>,
  ): Int {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT COUNT(*) + 1 FROM extras WHERE loanId = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND _id != ")
    _stringBuilder.append("?")
    _stringBuilder.append(" AND type IN (")
    val _inputSize: Int = types.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, excludeExtraId)
        _argIndex = 3
        for (_item: Int in types) {
          _stmt.bindLong(_argIndex, _item.toLong())
          _argIndex++
        }
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

  public override suspend fun deleteByLoanAndExtraId(loanId: Long, extraId: Long) {
    val _sql: String = "DELETE FROM extras WHERE loanId = ? AND _id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, loanId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, extraId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByLoanId(loanId: Long) {
    val _sql: String = "DELETE FROM extras WHERE loanId = ?"
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

  public override suspend fun deleteByLoanIds(loanIds: List<Long>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM extras WHERE loanId IN (")
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
