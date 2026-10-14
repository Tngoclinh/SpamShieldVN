package vn.spamshield.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SpamRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Synchronized
    fun loadRules(): List<BrandRule> {
        val array = runCatching { JSONArray(preferences.getString(KEY_RULES, "[]")) }
            .getOrElse { JSONArray() }
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val patternsJson = item.optJSONArray("patterns") ?: JSONArray()
                val patterns = buildList {
                    for (patternIndex in 0 until patternsJson.length()) {
                        val pattern = patternsJson.optString(patternIndex).trim()
                        if (pattern.isNotEmpty()) add(pattern)
                    }
                }
                val brandName = item.optString("brandName").trim()
                if (brandName.isNotEmpty() && patterns.isNotEmpty()) {
                    add(
                        BrandRule(
                            id = item.optString("id", UUID.randomUUID().toString()),
                            brandName = brandName,
                            patterns = patterns,
                            enabled = item.optBoolean("enabled", true),
                        )
                    )
                }
            }
        }
    }

    @Synchronized
    fun addRule(brandName: String, patterns: List<String>): List<BrandRule> {
        val updated = loadRules() + BrandRule(
            id = UUID.randomUUID().toString(),
            brandName = brandName.trim(),
            patterns = patterns.distinct(),
        )
        saveRules(updated)
        return updated
    }

    @Synchronized
    fun setRuleEnabled(id: String, enabled: Boolean): List<BrandRule> {
        val updated = loadRules().map { if (it.id == id) it.copy(enabled = enabled) else it }
        saveRules(updated)
        return updated
    }

    @Synchronized
    fun deleteRule(id: String): List<BrandRule> {
        val updated = loadRules().filterNot { it.id == id }
        saveRules(updated)
        return updated
    }

    @Synchronized
    fun addBlockedCall(phoneNumber: String, brandName: String) {
        val history = listOf(BlockedCall(phoneNumber, brandName, System.currentTimeMillis())) + loadHistory()
        saveHistory(history.take(MAX_HISTORY))
    }

    @Synchronized
    fun loadHistory(): List<BlockedCall> {
        val array = runCatching { JSONArray(preferences.getString(KEY_HISTORY, "[]")) }
            .getOrElse { JSONArray() }
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    BlockedCall(
                        phoneNumber = item.optString("phoneNumber"),
                        brandName = item.optString("brandName"),
                        blockedAtMillis = item.optLong("blockedAtMillis"),
                    )
                )
            }
        }
    }

    @Synchronized
    fun clearHistory() {
        preferences.edit { remove(KEY_HISTORY) }
    }

    private fun saveRules(rules: List<BrandRule>) {
        val array = JSONArray()
        rules.forEach { rule ->
            array.put(
                JSONObject()
                    .put("id", rule.id)
                    .put("brandName", rule.brandName)
                    .put("enabled", rule.enabled)
                    .put("patterns", JSONArray(rule.patterns))
            )
        }
        preferences.edit { putString(KEY_RULES, array.toString()) }
    }

    private fun saveHistory(history: List<BlockedCall>) {
        val array = JSONArray()
        history.forEach { call ->
            array.put(
                JSONObject()
                    .put("phoneNumber", call.phoneNumber)
                    .put("brandName", call.brandName)
                    .put("blockedAtMillis", call.blockedAtMillis)
            )
        }
        preferences.edit { putString(KEY_HISTORY, array.toString()) }
    }

    private companion object {
        const val PREFS_NAME = "spam_shield_data"
        const val KEY_RULES = "brand_rules"
        const val KEY_HISTORY = "blocked_history"
        const val MAX_HISTORY = 100
    }
}
