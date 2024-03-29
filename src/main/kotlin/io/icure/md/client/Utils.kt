package io.icure.md.client

import com.github.benmanes.caffeine.cache.Cache
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

suspend fun <K, V> Cache<K, Deferred<Optional<V>>>.defGet(key: K, loader: suspend (K) -> V?): V? =
    this.get(key) { GlobalScope.async { Optional.ofNullable(loader(key)) } }.await().orElse(null)

suspend fun <K, V> Cache<K, Deferred<Optional<V>>>.defPut(key: K, loader: suspend (K) -> V?) =
    this.put(key, GlobalScope.async { Optional.ofNullable(loader(key)) })

fun <T> T.applyIf(predicate: (T) -> Boolean, action: (T) -> T): T {
    return if (predicate.invoke(this)) action.invoke(this) else this
}

fun String.isUUID(): Boolean {
    return try {
        UUID.fromString(this)
        true
    } catch (exception: IllegalArgumentException) {
        false
    }
}

fun String.isCodeId(): Boolean {
    val codeRegex = "[a-zA-Z0-9]*\\|[a-zA-Z0-9.-]*\\|[a-zA-Z0-9.]+".toRegex()
    return codeRegex.matches(this)
}

fun ByteArray.toHex(): String {
    return joinToString("") { "%02x".format(it) }
}
