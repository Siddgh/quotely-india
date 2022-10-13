package sid.com.quotelyindia.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ShareCompat
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.constants.PermissionsConstants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object StorageUtils {

    fun getMimeType(uri: Uri, context: Context): String? {
        val resolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(resolver?.getType(uri))
    }

    fun downloadQuoteCard(
        fileName: String,
        movie: String,
        context: Context,
        activity: Activity
    ) {
        FirebaseStorageUtils.getQuoteCardsFullSizeReferenceById(fileName).downloadUrl.addOnSuccessListener {
            val uri = Uri.parse(it.toString())
            val downloadManager =
                activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            try {
                val downloadManagerRequest = DownloadManager.Request(uri)
                downloadManagerRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setTitle(fileName)
                    .setDescription(StringUtils.getDownloadNotificationTitle(movie))
                    .setAllowedOverMetered(true).setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        StringUtils.getPNGFileName(fileName)
                    )
                    .setMimeType(getMimeType(uri, context))
                downloadManager.enqueue(downloadManagerRequest)
                Toast.makeText(context, MessagesConstants.downloadStarted, Toast.LENGTH_LONG).show()
                FirebaseAnalyticsUtils.logQuoteAction(FirebaseReferenceConstants.FirebaseAnalyticsActionOnQuoteDownload)
            } catch (e: Exception) {
                Toast.makeText(context, MessagesConstants.somethingWentWrong, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun shareStuff(textToShare: String?, activity: Activity) {
        val chooserTitle = MessagesConstants.caringIsSharingTitle
        ShareCompat.IntentBuilder
            .from(activity)
            .setText(textToShare)
            .setType(PermissionsConstants.shareFormat)
            .setChooserTitle(chooserTitle)
            .startChooser()
    }

    fun copyToClipboard(activity: Activity, toCopy: String) {
        val clipboard =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(MessagesConstants.clipboardCopySuccess, toCopy)
        clipboard.setPrimaryClip(clip)
    }

}