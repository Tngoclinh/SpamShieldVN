package vn.spamshield.data

data class BrandRule(
    val id: String,
    val brandName: String,
    val patterns: List<String>,
    val enabled: Boolean = true,
)

data class BlockedCall(
    val phoneNumber: String,
    val brandName: String,
    val blockedAtMillis: Long,
)
