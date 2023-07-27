package com.apollographql.apollo3.cache.normalized.sql

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.sql.internal.createDriver
import com.apollographql.apollo3.cache.normalized.sql.internal.createRecordDatabase
import com.apollographql.apollo3.cache.normalized.sql.internal.getSchema

class SqlNormalizedCacheFactory internal constructor(
    private val driver: SqlDriver,
) : NormalizedCacheFactory() {
  override fun create(): SqlNormalizedCache {
    return SqlNormalizedCache(createRecordDatabase(driver))
  }
}

actual fun SqlNormalizedCacheFactory(name: String?): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(createDriver(name, null, getSchema()))
}

/**
 * @param [name] Name of the database file, or null for an in-memory database (as per Android framework implementation).
 * @param [factory] Factory class to create instances of [SupportSQLiteOpenHelper]
 * @param [useNoBackupDirectory] Sets whether to use a no backup directory or not.
 */
fun SqlNormalizedCacheFactory(
    context: Context,
    name: String? = "apollo.db",
    factory: SupportSQLiteOpenHelper.Factory = FrameworkSQLiteOpenHelperFactory(),
    useNoBackupDirectory: Boolean = false,
    ): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(
      AndroidSqliteDriver(
          getSchema(),
          context.applicationContext,
          name,
          factory,
          useNoBackupDirectory = useNoBackupDirectory
      )
  )
}
