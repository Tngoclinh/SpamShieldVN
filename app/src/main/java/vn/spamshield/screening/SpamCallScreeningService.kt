package vn.spamshield.screening

import android.telecom.Call
import android.telecom.CallScreeningService
import vn.spamshield.data.SpamRepository
import vn.spamshield.domain.PhoneMatcher

class SpamCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

        val phoneNumber = callDetails.handle?.schemeSpecificPart.orEmpty()
        val repository = SpamRepository(applicationContext)
        val matchingRule = PhoneMatcher.findMatchingRule(phoneNumber, repository.loadRules())
        val shouldBlock = matchingRule != null

        if (shouldBlock) {
            repository.addBlockedCall(phoneNumber, matchingRule.brandName)
        }

        val response = CallResponse.Builder()
            .setDisallowCall(shouldBlock)
            .setRejectCall(shouldBlock)
            .setSilenceCall(shouldBlock)
            .setSkipNotification(shouldBlock)
            .setSkipCallLog(false)
            .build()

        respondToCall(callDetails, response)
    }
}
