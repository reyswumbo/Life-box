package com.reyswumbo.lifeinbox.domain

import java.net.URI

/**
 * Pure Kotlin URL validation and normalization, kept independent from Android
 * so it can be unit tested on the JVM.
 */
object UrlValidator {

    private const val MAX_LENGTH = 2048

    fun validate(raw: String): UrlValidationResult {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) {
            return UrlValidationResult.Invalid("Enter a link to save.")
        }
        if (trimmed.any { it.isWhitespace() }) {
            return UrlValidationResult.Invalid("Links cannot contain spaces.")
        }
        if (trimmed.length > MAX_LENGTH) {
            return UrlValidationResult.Invalid("That link is too long.")
        }

        val lower = trimmed.lowercase()
        val hasSupportedScheme = lower.startsWith("http://") || lower.startsWith("https://")
        val looksLikeOtherScheme = trimmed.contains("://") && !hasSupportedScheme
        if (looksLikeOtherScheme) {
            return UrlValidationResult.Invalid("Only http and https links are supported.")
        }

        val withScheme = if (hasSupportedScheme) trimmed else "https://$trimmed"
        val uri = try {
            URI(withScheme)
        } catch (e: Exception) {
            return UrlValidationResult.Invalid("That does not look like a valid link.")
        }

        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") {
            return UrlValidationResult.Invalid("Only http and https links are supported.")
        }

        val host = uri.host?.lowercase()
        if (host.isNullOrBlank()) {
            return UrlValidationResult.Invalid("That link is missing a website address.")
        }
        if (!host.contains('.') && host != "localhost") {
            return UrlValidationResult.Invalid("That does not look like a valid website address.")
        }

        val normalized = buildNormalized(scheme, host, uri)
        return UrlValidationResult.Valid(
            normalizedUrl = normalized,
            originalUrl = withScheme,
            domain = host.removePrefix("www."),
            suggestedTitle = suggestTitle(host, uri)
        )
    }

    private fun buildNormalized(scheme: String, host: String, uri: URI): String {
        val defaultPort = (scheme == "http" && uri.port == 80) || (scheme == "https" && uri.port == 443)
        val portPart = if (uri.port != -1 && !defaultPort) ":${uri.port}" else ""
        val pathPart = uri.rawPath?.trimEnd('/')?.takeUnless { it.isEmpty() } ?: ""
        val queryPart = uri.rawQuery?.let { "?$it" } ?: ""
        val fragmentPart = uri.rawFragment?.let { "#$it" } ?: ""
        return "$scheme://$host$portPart$pathPart$queryPart$fragmentPart"
    }

    private fun suggestTitle(host: String, uri: URI): String {
        val segments = uri.path
            ?.split('/')
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        val candidate = if (segments.isNotEmpty()) segments.last() else host.removePrefix("www.")
        val humanized = candidate
            .substringBefore('.')
            .replace('-', ' ')
            .replace('_', ' ')
            .trim()
        return humanized.ifBlank { host.removePrefix("www.") }
    }
}

sealed interface UrlValidationResult {
    data class Valid(
        val normalizedUrl: String,
        val originalUrl: String,
        val domain: String,
        val suggestedTitle: String
    ) : UrlValidationResult

    data class Invalid(val reason: String) : UrlValidationResult
}
