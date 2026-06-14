package ru.kredit.calculator.database

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException

object DatabasePathResolver {
  private val databaseSidecarSuffixes = listOf("-journal", "-wal", "-shm")

  fun getDatabasePath(context: Context): File {
    val databaseName = DatabaseContract.DATABASE_NAME
    val externalDatabaseDir = getExternalDatabaseDir(context)
    if (externalDatabaseDir != null) {
      return File(externalDatabaseDir, databaseName)
    }
    return context.getDatabasePath(databaseName)
  }

  fun resolveAndMigrate(context: Context): File {
    val appContext = context.applicationContext
    val targetDatabase = getDatabasePath(appContext)
    val internalDatabase = appContext.getDatabasePath(DatabaseContract.DATABASE_NAME)

    if (targetDatabase.absolutePath != internalDatabase.absolutePath) {
      migrateIfNeeded(
        source = internalDatabase,
        target = targetDatabase,
      )
    }

    return targetDatabase
  }

  fun getExternalDatabaseDir(context: Context): File? {
    if (!isExternalStorageWritable()) {
      return null
    }

    val appStorageDir = File(
      Environment.getExternalStorageDirectory(),
      "Android${File.separator}data${File.separator}${context.packageName}",
    )
    if (!appStorageDir.exists() && !appStorageDir.mkdirs()) {
      return null
    }

    val databaseDir = File(appStorageDir, "databases")
    if (!databaseDir.exists() && !databaseDir.mkdirs()) {
      return null
    }

    return databaseDir
  }

  private fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
  }

  private fun migrateIfNeeded(source: File, target: File) {
    if (target.exists() || !source.exists()) {
      return
    }

    target.parentFile?.mkdirs()
    source.copyTo(target, overwrite = false)
    copyDatabaseSidecars(source, target)
  }

  private fun copyDatabaseSidecars(source: File, target: File) {
    databaseSidecarSuffixes.forEach { suffix ->
      val sourceSidecar = File(source.parent, source.name + suffix)
      if (!sourceSidecar.exists()) {
        return@forEach
      }

      val targetSidecar = File(target.parent, target.name + suffix)
      try {
        sourceSidecar.copyTo(targetSidecar, overwrite = false)
      } catch (_: IOException) {
        // Non-critical: Room/SQLite can rebuild WAL/SHM if needed.
      }
    }
  }
}
