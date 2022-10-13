package sid.com.quotelyindia.utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import sid.com.quotelyindia.constants.FirebaseReferenceConstants

object FirebaseStorageUtils {
    private val firebaseStorageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    fun getMoviePosterReferenceById(id: String): StorageReference {
        return firebaseStorageInstance.getReference(FirebaseReferenceConstants.PosterStorageReference)
            .child(StringUtils.getJPGFileName(id))
    }

    fun getQuoteCardsThumbnailsReferenceById(id: String): StorageReference {
        return firebaseStorageInstance.getReference(FirebaseReferenceConstants.QuoteCardsFullThumnailsStorageReference)
            .child(StringUtils.getJPEGFileName(id))
    }

    fun getQuoteCardsFullSizeReferenceById(id: String): StorageReference {
        return firebaseStorageInstance.getReference(FirebaseReferenceConstants.QuoteCardsFullSizeStorageReference)
            .child(StringUtils.getJPEGFileName(id))
    }


}