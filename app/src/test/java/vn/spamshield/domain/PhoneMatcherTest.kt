package vn.spamshield.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import vn.spamshield.data.BrandRule

class PhoneMatcherTest {
    private val rule = BrandRule(
        id = "1",
        brandName = "Quảng cáo BĐS",
        patterns = listOf("0247300*", "0901 234 567"),
    )

    @Test
    fun matchesExactNumberAfterRemovingFormatting() {
        assertEquals(rule, PhoneMatcher.findMatchingRule("090-123-4567", listOf(rule)))
    }

    @Test
    fun matchesPrefixWildcard() {
        assertEquals(rule, PhoneMatcher.findMatchingRule("02473001234", listOf(rule)))
    }

    @Test
    fun treatsVietnamCountryCodeAsEquivalentToLeadingZero() {
        assertEquals(rule, PhoneMatcher.findMatchingRule("+84 901 234 567", listOf(rule)))
    }

    @Test
    fun ignoresDisabledRule() {
        assertNull(PhoneMatcher.findMatchingRule("0901234567", listOf(rule.copy(enabled = false))))
    }

    @Test
    fun doesNotMatchUnrelatedNumber() {
        assertNull(PhoneMatcher.findMatchingRule("0919999999", listOf(rule)))
    }
}
