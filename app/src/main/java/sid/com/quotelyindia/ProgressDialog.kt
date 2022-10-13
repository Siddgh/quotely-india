package sid.com.quotelyindia

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog

class ProgressDialog(val activity: Activity) {

    lateinit var alertDialog: AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.activity_progress_dialog, null))
        builder.setCancelable(false)

        alertDialog = builder.create()
        if (!activity.isFinishing) {
            alertDialog.show()
        }
    }

    fun dismissDialog() {
        if (alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }

}
