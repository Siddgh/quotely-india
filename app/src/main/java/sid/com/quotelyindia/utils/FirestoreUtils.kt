package sid.com.quotelyindia.utils

import androidx.paging.PagedList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.QuotesMeta

object FirestoreUtils {
    val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val quotesConfigFetch: PagedList.Config by lazy {
        PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(5)
            .setPageSize(1).build()
    }

    val quotesFirestoreReference: CollectionReference by lazy {
        firestoreInstance.collection(FirebaseReferenceConstants.QuotesFirestoreReference)
    }

    val moviesFirestoreReference: CollectionReference by lazy {
        firestoreInstance.collection(FirebaseReferenceConstants.MoviesFirestoreReference)
    }

    val quoteCardsFirestoreReference: CollectionReference by lazy {
        firestoreInstance.collection(FirebaseReferenceConstants.QuoteCardsFirestoreReference)
    }

    fun fetchQuotesQuery(movieName: String, category: String): Query {
        return if (category.contentEquals(FirebaseReferenceConstants.all)) quotesFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ) else quotesFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ).whereArrayContains(FirebaseReferenceConstants.tagFirestoreKey, category)
    }

    fun fetchQuotesCardQuery(movieName: String, category: String): Query {
        return if (category.contentEquals(FirebaseReferenceConstants.all)) quoteCardsFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ) else quoteCardsFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ).whereEqualTo(FirebaseReferenceConstants.tagFirestoreKey, category)
    }

    fun fetchAllQuotesByTagsQuery(category: String): Query {
        return quotesFirestoreReference.whereArrayContains(
            FirebaseReferenceConstants.tagFirestoreKey,
            category
        ).orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING)
    }

    fun fetchAllQuoteCardsByTagQuery(category: String): Query {
        return quoteCardsFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.tagFirestoreKey,
            category
        ).orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING)
    }

    fun fetchPopularQuotes(exploreTo: String): Query {
        return if (exploreTo.contentEquals(PassingDataConstants.PassMovies)) quotesFirestoreReference.orderBy(
            FirebaseReferenceConstants.likesFirestoreKey,
            Query.Direction.DESCENDING
        ).limit(5) else quotesFirestoreReference.whereArrayContains(
            FirebaseReferenceConstants.tagFirestoreKey,
            exploreTo
        ).orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING).limit(5)
    }

    fun fetchPopularQuoteCards(exploreTo: String): Query {
        return if (exploreTo.contentEquals(PassingDataConstants.PassMovies)) quoteCardsFirestoreReference.orderBy(
            FirebaseReferenceConstants.likesFirestoreKey,
            Query.Direction.DESCENDING
        ).limit(5) else quoteCardsFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.tagFirestoreKey,
            exploreTo
        )
            .orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING)
            .limit(5)
    }

    fun fetchPopularQuotesFromAMovie(movieName: String, isQuoteCard: Boolean): Query {
        return if (!isQuoteCard) quotesFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ).orderBy(
            FirebaseReferenceConstants.likesFirestoreKey,
            Query.Direction.DESCENDING
        ).limit(5) else quoteCardsFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.movieFirestoreKey,
            movieName
        ).orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING).limit(5)
    }

    fun fetchPopularQuotesFromPopularPerson(saidBy: String, isQuoteCard: Boolean): Query {
        return if (!isQuoteCard) quotesFirestoreReference.whereEqualTo(
            FirebaseReferenceConstants.saidByFirestoreKey,
            saidBy
        ).orderBy(
            FirebaseReferenceConstants.likesFirestoreKey,
            Query.Direction.DESCENDING
        ).limit(5) else quoteCardsFirestoreReference.whereArrayContains(
            FirebaseReferenceConstants.inFrameFirestoreKey,
            saidBy
        ).orderBy(FirebaseReferenceConstants.likesFirestoreKey, Query.Direction.DESCENDING).limit(5)
    }

    fun addALike(quoteId: String, isQuoteCard: Boolean) {
        if (!isQuoteCard) {
            quotesFirestoreReference.document(quoteId)
                .update(FirebaseReferenceConstants.likesFirestoreKey, FieldValue.increment(1))
        } else {
            quoteCardsFirestoreReference.document(quoteId)
                .update(FirebaseReferenceConstants.likesFirestoreKey, FieldValue.increment(1))
        }
    }

    fun removeALike(quoteId: String, isQuoteCard: Boolean) {
        if (!isQuoteCard) {
            quotesFirestoreReference.document(quoteId)
                .update(FirebaseReferenceConstants.likesFirestoreKey, FieldValue.increment(-1))
        } else {
            quoteCardsFirestoreReference.document(quoteId)
                .update(FirebaseReferenceConstants.likesFirestoreKey, FieldValue.increment(-1))
        }
    }

    fun getLikedQuotesFirestoreReference(isQuoteCard: Boolean): CollectionReference {
        return if (!isQuoteCard) quotesFirestoreReference else quoteCardsFirestoreReference
    }

    fun readQuoteOfTheWeek(onSuccess: (QuotesMeta?) -> Unit) {
        FirebaseDatabaseUtils.getQuoteHightLightReference().addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val quoteId = p0.getValue(QuotesMeta::class.java)
                    FirestoreUtils.quotesFirestoreReference.document(
                        quoteId?.quoteId ?: FirebaseReferenceConstants.anon
                    ).get().addOnSuccessListener {
                        val quotesMeta = it.toObject(QuotesMeta::class.java)
                        onSuccess(quotesMeta)
                    }

                }
            }

        })
    }

}