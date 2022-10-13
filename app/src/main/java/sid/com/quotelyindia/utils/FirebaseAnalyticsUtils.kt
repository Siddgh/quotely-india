package sid.com.quotelyindia.utils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import sid.com.quotelyindia.constants.FirebaseReferenceConstants

object FirebaseAnalyticsUtils {
    private val firebaseAnalytics = Firebase.analytics

    fun logAdsDisplay(adType: String) {
        val params = Bundle()
        params.putString(FirebaseReferenceConstants.FirebaseAnalyticsLogAdsType, adType)
        firebaseAnalytics.logEvent(FirebaseReferenceConstants.FirebaseAnalyticsLogAdsType, params)
    }

    fun logPremiumPurchase() {
        val params = Bundle()
        params.putString(
            FirebaseReferenceConstants.FirebaseAnalyticsLogPremiumVersion,
            FirebaseReferenceConstants.FirebaseAnalyticsLogPremiumVersion
        )
        firebaseAnalytics.logEvent(
            FirebaseReferenceConstants.FirebaseAnalyticsLogPremiumVersion,
            params
        )
    }

    fun setAppVersionType(appType: String) {
        firebaseAnalytics.setUserProperty(
            FirebaseReferenceConstants.FirebaseAnalyticsUserProperty,
            appType
        )
    }

    fun logMovieClicks(movieName: String, type: String) {
        val params = Bundle()
        params.putString(FirebaseReferenceConstants.movieFirestoreKey, movieName)
        params.putString(FirebaseReferenceConstants.FirebaseAnalyticsLogClickType, type)
        firebaseAnalytics.logEvent(
            FirebaseReferenceConstants.FirebaseAnalyticsLogMovieClick,
            params
        )
    }

    fun logTagsClicks(tagName: String, type: String) {
        val params = Bundle()
        params.putString(FirebaseReferenceConstants.tagFirestoreKey, tagName)
        params.putString(FirebaseReferenceConstants.FirebaseAnalyticsLogClickType, type)
        firebaseAnalytics.logEvent(
            FirebaseReferenceConstants.FirebaseAnalyticsLogTagsClick,
            params
        )
    }

    fun logQuoteViewedClicks(movieName: String, type: String) {
        val params = Bundle()
        params.putString(FirebaseReferenceConstants.movieFirestoreKey, movieName)
        params.putString(FirebaseReferenceConstants.FirebaseAnalyticsLogClickType, type)
        firebaseAnalytics.logEvent(
            FirebaseReferenceConstants.FirebaseAnalyticsLogQuoteViewClick,
            params
        )
    }

    fun setWidgetUsedProperty(isUsing: Boolean) {
        if (isUsing) {
            firebaseAnalytics.setUserProperty(
                FirebaseReferenceConstants.FirebaseAnalyticsUsingWidgetUserProperty,
                FirebaseReferenceConstants.FirebaseAnalyticsWidgetUsed
            )
        } else {
            firebaseAnalytics.setUserProperty(
                FirebaseReferenceConstants.FirebaseAnalyticsUsingWidgetUserProperty,
                FirebaseReferenceConstants.FirebaseAnalyticsWidgetNotUsed
            )
        }
    }

    fun logQuoteAction(action: String) {
        val params = Bundle()
        params.putString(FirebaseReferenceConstants.FirebaseAnalyticsLogClickType, action)
        firebaseAnalytics.logEvent(
            FirebaseReferenceConstants.FirebaseAnalyticsLogActionOnQuote,
            params
        )
    }

}