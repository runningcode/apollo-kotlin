package com.apollographql.apollo3.cache.normalized.sql.internal
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

/**
 * Some implementations like Native and Android take the schema when creating the driver and the driver
 * will take care of migrations
 *
 * Others like JVM don't do this automatically. This is when [maybeCreateOrMigrateSchema] is needed
 */
internal expect fun maybeCreateOrMigrateSchema(driver: SqlDriver, schema: SqlSchema<QueryResult.Value<Unit>>)