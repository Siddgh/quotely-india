package sid.com.quotelyindia.constants

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionsConstants {

    val writePermissionRequestCode by lazy {
        100
    }

    const val shareFormat = "text/plain"

}