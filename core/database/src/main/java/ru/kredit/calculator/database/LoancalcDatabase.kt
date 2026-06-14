package ru.kredit.calculator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.kredit.calculator.database.dao.ExtraDao
import ru.kredit.calculator.database.dao.LoanDao
import ru.kredit.calculator.database.dao.LoanDetailsDao
import ru.kredit.calculator.database.dao.OfferDao
import ru.kredit.calculator.database.entity.ExtraEntity
import ru.kredit.calculator.database.entity.LoanDetailsEntity
import ru.kredit.calculator.database.entity.LoanEntity
import ru.kredit.calculator.database.entity.OfferEntity

@Database(
    entities = [
        LoanEntity::class,
        ExtraEntity::class,
        OfferEntity::class,
        LoanDetailsEntity::class,
    ],
    version = DatabaseContract.DATABASE_VERSION,
    exportSchema = true,
)
abstract class LoancalcDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao
    abstract fun extraDao(): ExtraDao
    abstract fun offerDao(): OfferDao
    abstract fun loanDetailsDao(): LoanDetailsDao

    companion object {
        @Volatile
        private var instance: LoancalcDatabase? = null

        fun getInstance(context: Context): LoancalcDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        fun getInMemoryInstance(context: Context): LoancalcDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                LoancalcDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
        }

        private fun buildDatabase(context: Context): LoancalcDatabase {
            val databaseFile = DatabasePathResolver.resolveAndMigrate(context.applicationContext)
            return Room.databaseBuilder(
                context.applicationContext,
                LoancalcDatabase::class.java,
                databaseFile.absolutePath,
            )
                .addMigrations(*DatabaseMigrations.ALL)
                .build()
        }
    }
}
