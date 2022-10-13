package sid.com.quotelyindia.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import sid.com.quotelyindia.R
import sid.com.quotelyindia.models.data.UserMeta

object FirebaseAuthUtils {

    fun getUserMeta(): UserMeta {
        val user = FirebaseAuth.getInstance().currentUser
        return UserMeta(
            user?.displayName,
            user?.email,
            user?.photoUrl.toString(),
            user?.uid,
            DateUtils.currentDate
        )
    }

    fun setUserProfile(
        userName: TextView,
        userEmail: TextView,
        userProfile: ImageView,
        context: Context
    ) {
        val userMeta = getUserMeta()
        userName.text = userMeta.displayName
        userEmail.text = userMeta.email
        Glide.with(context).load(userMeta.photoUrl).placeholder(R.drawable.ic_user)
            .into(userProfile)
    }

}