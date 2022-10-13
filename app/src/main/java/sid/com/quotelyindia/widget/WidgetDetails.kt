package sid.com.quotelyindia.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import sid.com.quotelyindia.R
import sid.com.quotelyindia.SignIn
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.contents.Home
import sid.com.quotelyindia.utils.FirebaseAnalyticsUtils
import sid.com.quotelyindia.utils.FirestoreUtils

class WidgetDetails : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Toast.makeText(context, "Widget updates every week", Toast.LENGTH_LONG).show()
        FirebaseAnalyticsUtils.setWidgetUsedProperty(true)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        FirebaseAnalyticsUtils.setWidgetUsedProperty(false)
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        var intent: Intent?
        appWidgetIds?.forEach { appWidgetId ->
            intent = if (FirebaseAuth.getInstance().currentUser != null) {
                Intent(context, Home::class.java)
            } else {
                Intent(context, SignIn::class.java)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                PassingDataConstants.WidgetsToAppRequestCode,
                intent,
                0
            )

            val views = RemoteViews(
                context?.packageName,
                R.layout.widget_layout
            )
            views.setOnClickPendingIntent(R.id.ll_widget_layout, pendingIntent)
            if (FirebaseAuth.getInstance().currentUser != null) {
                FirestoreUtils.readQuoteOfTheWeek {
                    val quotesMeta = it
                    val quote = quotesMeta?.quotes?.replace("...", "\n")
                    views.setCharSequence(R.id.tv_widget_layout_quote, "setText", quote)
                    views.setCharSequence(R.id.tv_widget_layout_movie, "setText", quotesMeta?.movie)
                    appWidgetManager?.updateAppWidget(appWidgetId, views)
                }
            } else {
                views.setCharSequence(
                    R.id.tv_widget_layout_quote,
                    "setText",
                    "You're not logged in\n\nPlease sign in to the app for Quotes to be displayed here\n\nOnce Signed In, Create a new widget"
                )
                views.setCharSequence(R.id.tv_widget_layout_movie, "setText", "")
                views.setViewVisibility(R.id.iv_widget_layout_movie, View.GONE)
                appWidgetManager?.updateAppWidget(appWidgetId, views)
            }
        }
    }

}