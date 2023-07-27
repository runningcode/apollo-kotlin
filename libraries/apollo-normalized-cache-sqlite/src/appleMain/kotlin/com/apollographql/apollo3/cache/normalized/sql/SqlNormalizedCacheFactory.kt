package com.apollographql.apollo3.cache.normalized.sql

import app.cash.sqldelight.db.SqlDriver
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.sql.internal.createDriver
import com.apollographql.apollo3.cache.normalized.sql.internal.createRecordDatabase
import com.apollographql.apollo3.cache.normalized.sql.internal.getSchema

class SqlNormalizedCacheFactory internal constructor(
    private val driver: SqlDriver,
) : NormalizedCacheFactory() {

  override fun create(): SqlNormalizedCache {
    return SqlNormalizedCache(
        recordDatabase = createRecordDatabase(driver)
    )
  }
}

actual fun SqlNormalizedCacheFactory(name: String?): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(name, null)
}

fun SqlNormalizedCacheFactory(): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory("apollo.db")
}

/**
 * @param name the name of the database or null for an in-memory database
 * @param baseDir the baseDirectory where to store the database.
 * [baseDir] must exist and be a directory
 * If [baseDir] is a relative path, it will be interpreted relative to the current working directory
 */
fun SqlNormalizedCacheFactory(
    name: String?,
    baseDir: String?
): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(createDriver(name, baseDir, getSchema()))
}
