package sid.com.quotelyindia


import com.google.firebase.database.FirebaseDatabase


class FirebaseOffline : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}