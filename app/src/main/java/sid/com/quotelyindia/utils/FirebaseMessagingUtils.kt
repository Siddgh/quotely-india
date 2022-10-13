package sid.com.quotelyindia.utils

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import sid.com.quotelyindia.R

object FirebaseMessagingUtils {

    fun subscribeToNotificationTopic(context: Context) {
        FirebaseMessaging.getInstance()
            .subscribeToTopic(context.getString(R.string.notification_topic))
    }

}