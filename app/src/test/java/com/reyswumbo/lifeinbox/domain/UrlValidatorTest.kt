package com.reyswumbo.lifeinbox.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {

    private fun valid(raw: String): UrlValidationResult.Valid =
        UrlValidator.validate(raw) as UrlValidationResult.Valid

    @Test
    fun acceptsFullHttpsUrl() {
        val result = valid("https://example.com/articles/hello-world")
        assertEquals("https://example.com/articles/hello-world", result.normalizedUrl)
        assertEquals("example.com", result.domain)
        assertEquals("hello world", result.suggestedTitle)
    }

    @Test
    fun addsHttpsSchemeWhenMissing() {
        assertEquals(
            "https://example.com",
            valid("example.com").normalizedUrl
        )
    }

    @Test
    fun lowercasesSchemeAndHost() {
        assertEquals(
            "https://example.com/Path",
            valid("HTTPS://Example.COM/Path").normalizedUrl
        )
    }

    @Test
    fun stripsDefaultPorts() {
        assertEquals("http://example.com", valid("http://example.com:80").normalizedUrl)
        assertEquals("https://example.com", valid("https://example.com:443").normalizedUrl)
    }

    @Test
    fun stripsTrailingSlashFromEmptyPath() {
        assertEquals("https://example.com", valid("https://example.com/").normalizedUrl)
    }

    @Test
    fun keepsNonDefaultPort() {
        assertEquals("https://example.com:8443/x", valid("https://example.com:8443/x").normalizedUrl)
    }

    @Test
    fun identicalLinksNormalizeEqually() {
        assertEquals(
            valid("HTTPS://Example.com/Path/").normalizedUrl,
            valid("https://example.com/Path").normalizedUrl
        )
    }

    @Test
    fun stripsWwwFromDomain() {
        assertEquals("example.com", valid("https://www.example.com").domain)
    }

    @Test
    fun rejectsBlankInput() {
        assertTrue(UrlValidator.validate("   ") is UrlValidationResult.Invalid)
    }

    @Test
    fun rejectsInputWithSpaces() {
        assertTrue(UrlValidator.validate("https://exa mple.com") is UrlValidationResult.Invalid)
    }

    @Test
    fun rejectsUnsupportedScheme() {
        assertTrue(UrlValidator.validate("ftp://example.com") is UrlValidationResult.Invalid)
    }

    @Test
    fun rejectsHostWithoutDot() {
        assertTrue(UrlValidator.validate("https://notaurl") is UrlValidationResult.Invalid)
    }
}
