package sid.com.quotelyindia.models.data

import android.net.Uri
import java.text.SimpleDateFormat
import java.time.LocalDate

data class UserMeta(
    val displayName: String? = "",
    val email: String? = "",
    val photoUrl: String? = "",
    val uid: String? = "",
    val lastVisit: String? = ""
)