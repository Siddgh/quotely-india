package sid.com.quotelyindia.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import sid.com.quotelyindia.R
import sid.com.quotelyindia.SignIn
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.contents.Explore
import sid.com.quotelyindia.contents.Movies

object AppShortcutsUtils {

    private fun getFavouritesIntent(context: Context): Intent {
        val intent: Intent?
        return if (FirebaseAuth.getInstance().currentUser != null) {
            intent = Intent(context, Explore::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putExtra(PassingDataConstants.PassExploreTo, PassingDataConstants.PassFavourites)
            intent.putExtra(PassingDataConstants.PassQuoteCardInformation, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        } else {
            intent = Intent(context, SignIn::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        }

    }

    private fun getTextQuotesIntent(context: Context): Intent {
        val intent: Intent?
        return if (FirebaseAuth.getInstance().currentUser != null) {
            intent = Intent(context, Movies::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putExtra(PassingDataConstants.PassQuoteCardInformation, false)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        } else {
            intent = Intent(context, SignIn::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        }

    }

    private fun getQuoteCardIntent(context: Context): Intent {
        val intent: Intent?
        return if (FirebaseAuth.getInstance().currentUser != null) {
            intent = Intent(context, Movies::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.putExtra(PassingDataConstants.PassQuoteCardInformation, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        } else {
            intent = Intent(context, SignIn::class.java)
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent
        }

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun createFavouritesShortcut(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(context, context.getString(R.string.app_shortcut_favourites_id))
            .setShortLabel(context.getString(R.string.app_shortcut_favourites_title))
            .setLongLabel(context.getString(R.string.app_shortcut_favourites_title))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_fav_shortcut))
            .setIntent(getFavouritesIntent(context))
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun createTextQuotesShortcut(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(
            context,
            context.getString(R.string.app_shortcut_text_quotes_id)
        )
            .setShortLabel(context.getString(R.string.app_shortcut_text_quotes_title))
            .setLongLabel(context.getString(R.string.app_shortcut_text_quotes_title))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_text_quotes_shortcut))
            .setIntent(getTextQuotesIntent(context))
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun createQuoteCardShortcut(context: Context): ShortcutInfo {
        return ShortcutInfo.Builder(
            context,
            context.getString(R.string.app_shortcut_quote_card_id)
        )
            .setShortLabel(context.getString(R.string.app_shortcut_quote_card_title))
            .setLongLabel(context.getString(R.string.app_shortcut_quote_card_title))
            .setIcon(Icon.createWithResource(context, R.drawable.ic_quote_card_shortcut))
            .setIntent(getQuoteCardIntent(context))
            .build()
    }

    fun setUpAppShortcuts(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortCutManager = context.getSystemService(ShortcutManager::class.java)
            shortCutManager!!.dynamicShortcuts =
                mutableListOf(
                    createQuoteCardShortcut(context),
                    createTextQuotesShortcut(context),
                    createFavouritesShortcut(context)
                )
        }
    }
}