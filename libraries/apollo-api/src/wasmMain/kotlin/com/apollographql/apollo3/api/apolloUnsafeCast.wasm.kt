package com.apollographql.apollo3.api

import com.apollographql.apollo3.annotations.ApolloExperimental

/**
 * Uses [unsafeCast] on JS or a regular `as` unsafe cast on other platforms.
 *
 * On JS targets [apolloUnsafeCast] bypasses the Kotlin type system. If the target class or interface is missing some fields, the cast will succeed but an exception will be thrown later when accessing the missing field.
 *
 * On non-JS targets, [apolloUnsafeCast] uses the Kotlin type system and will throw an exception if the receiver is not of the expected type.
 */
@ApolloExperimental
actual inline fun <reified T> Any.apolloUnsafeCast(): T = defaultApolloUnsafeCast()