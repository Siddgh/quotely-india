package sid.com.quotelyindia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import sid.com.quotelyindia.constants.AuthConstants
import sid.com.quotelyindia.contents.Home
import sid.com.quotelyindia.utils.AppShortcutsUtils
import sid.com.quotelyindia.utils.FirebaseAuthUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.StringUtils


class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        AppShortcutsUtils.setUpAppShortcuts(this)
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val uid = auth.currentUser!!.uid
            goToHome(uid)
            writeUser()
        } else {

            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )


            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setTheme(R.style.FirebaseUITheme)
                    .setLogo(R.drawable.ic_quotely_in_logo)
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(true)
                    .build(),
                AuthConstants.RC_SIGN_IN
            )
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("DEBUGGING", "${data?.dataString}")
        if (requestCode == AuthConstants.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(
                    this,
                    StringUtils.welcomeMessage(user?.displayName ?: ""),
                    Toast.LENGTH_SHORT
                ).show()

                writeUser()
                goToHome(user!!.uid)

            } else {
                FirebaseCrashlytics.getInstance()
                    .log("FirebaseAuth UI Sign-In Checks --> Result Code Not Okay --> $resultCode ")
            }
        }
    }

    private fun writeUser() {
        FirebaseDatabaseUtils.writeUserToFirebaseDatabase(FirebaseAuthUtils.getUserMeta())
    }

    private fun goToHome(uid: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra(AuthConstants.USER_ID, uid)
        startActivity(intent)
        finish()
    }
}
