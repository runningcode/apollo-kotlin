package com.apollographql.apollo3.cache.normalized.sql

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.sql.internal.createRecordDatabase
import com.apollographql.apollo3.cache.normalized.sql.internal.toUrl
import java.util.Properties

class SqlNormalizedCacheFactory internal constructor(
    private val driver: SqlDriver,
) : NormalizedCacheFactory() {

  override fun create(): SqlNormalizedCache {
    return SqlNormalizedCache(createRecordDatabase(driver))
  }
}

actual fun SqlNormalizedCacheFactory(name: String?): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(name.toUrl(null))
}

/**
 * @param name the name of the database or null for an in-memory database
 * @param baseDir the baseDirectory where to store the database.
 * If [baseDir] does not exist, it will be created
 * If [baseDir] is a relative path, it will be interpreted relative to the current working directory
 */
fun SqlNormalizedCacheFactory(name: String?, baseDir: String?): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(name.toUrl(baseDir))
}

/**
 * @param url Database connection URL in the form of `jdbc:sqlite:path` where `path` is either blank
 * (creating an in-memory database) or a path to a file.
 * @param properties
 */
fun SqlNormalizedCacheFactory(
    url: String,
    properties: Properties = Properties(),
): NormalizedCacheFactory {
  return SqlNormalizedCacheFactory(JdbcSqliteDriver(url, properties))
}

