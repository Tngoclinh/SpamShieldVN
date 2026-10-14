package vn.spamshield.domain

import vn.spamshield.data.BrandRule

object PhoneMatcher {
    fun findMatchingRule(phoneNumber: String, rules: List<BrandRule>): BrandRule? {
        val candidates = equivalentNumbers(phoneNumber)
        return rules.firstOrNull { rule ->
            rule.enabled && rule.patterns.any { pattern ->
                equivalentPatterns(pattern).any { normalizedPattern ->
                    candidates.any { number -> matches(number, normalizedPattern) }
                }
            }
        }
    }

    fun sanitizePattern(raw: String): String {
        val trimmed = raw.trim()
        val startsWithWildcard = trimmed.startsWith('*')
        val endsWithWildcard = trimmed.endsWith('*')
        val digits = trimmed.filter(Char::isDigit)
        if (digits.isEmpty()) return ""
        return buildString {
            if (startsWithWildcard) append('*')
            append(digits)
            if (endsWithWildcard) append('*')
        }
    }

    private fun matches(number: String, pattern: String): Boolean {
        val core = pattern.trim('*')
        return when {
            pattern.startsWith('*') && pattern.endsWith('*') -> number.contains(core)
            pattern.startsWith('*') -> number.endsWith(core)
            pattern.endsWith('*') -> number.startsWith(core)
            else -> number == core
        }
    }

    private fun equivalentPatterns(raw: String): Set<String> {
        val pattern = sanitizePattern(raw)
        if (pattern.isEmpty()) return emptySet()
        val prefix = if (pattern.startsWith('*')) "*" else ""
        val suffix = if (pattern.endsWith('*')) "*" else ""
        val digits = pattern.trim('*')
        return equivalentDigits(digits).mapTo(linkedSetOf()) { "$prefix$it$suffix" }
    }

    private fun equivalentNumbers(raw: String): Set<String> = equivalentDigits(raw.filter(Char::isDigit))

    private fun equivalentDigits(digits: String): Set<String> = buildSet {
        if (digits.isEmpty()) return@buildSet
        add(digits)
        // Vietnamese mobile/landline numbers may arrive as either 0... or +84...
        if (digits.startsWith("84") && digits.length > 2) add("0${digits.drop(2)}")
        if (digits.startsWith('0') && digits.length > 1) add("84${digits.drop(1)}")
    }
}
