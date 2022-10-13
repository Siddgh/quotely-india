package sid.com.quotelyindia.utils

import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.models.data.PaidMeta
import sid.com.quotelyindia.models.data.UserMeta
import sid.com.quotelyindia.utils.StringUtils.movieNameAsId

object FirebaseDatabaseUtils {

    private val firebaseDatabaseInstance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val quoteOfTheWeekDatabaseReference: DatabaseReference by lazy {
        firebaseDatabaseInstance.getReference(FirebaseReferenceConstants.QuoteOfTheWeekDatabaseReference)
    }

    val textQuotesMetaDatabaseReference: DatabaseReference by lazy {
        firebaseDatabaseInstance.getReference(FirebaseReferenceConstants.TextQuotesMetaDatabaseReference)
    }

    val userDatabaseReference: DatabaseReference by lazy {
        firebaseDatabaseInstance.getReference(FirebaseReferenceConstants.UserDatabaseReference)
    }

    val recentDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.RecentMoviesDatabaseReference)
    }

    val tagsDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.TagsDatabaseReference)
    }

    val moviesDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesDatabaseReference)
    }

    val moviesInYearsDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesInYearsDatabaseReference)
    }

    val endYearDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.MovieMetaDatabaseReference)
            .child(FirebaseReferenceConstants.EndYearDatabaseReference)
    }

    val moviesQuotesCountDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesQuotesCountDatabaseReference)
    }

    val moviesQuotesTagsDatabaseReference: DatabaseReference by lazy {
        textQuotesMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesQuotesTagsDatabaseReference)
    }

    val moviesQuoteCardsTagsDatabaseReference: DatabaseReference by lazy {
        quotesCardMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesQuoteCardsTagsDatabaseReference)
    }

    val tagsQuoteCardsDatabaseReference: DatabaseReference by lazy {
        quotesCardMetaDatabaseReference.child(FirebaseReferenceConstants.TagsDatabaseReference)
    }

    val moviesQuoteCardsDatabaseReference: DatabaseReference by lazy {
        quotesCardMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesFirestoreReference)
    }

    val quotesCardMetaDatabaseReference: DatabaseReference by lazy {
        firebaseDatabaseInstance.getReference(FirebaseReferenceConstants.QuoteCardsMetaDatabaseReference)
    }

    val articlesDatabaseReference: DatabaseReference by lazy {
        firebaseDatabaseInstance.getReference(FirebaseReferenceConstants.Articles)
    }

    val articlesOnDisplayDatabaseReference: DatabaseReference by lazy {
        articlesDatabaseReference.child(FirebaseReferenceConstants.ArticlesOnDisplay)
    }

    val articleMetaDatabaseReference: DatabaseReference by lazy {
        articlesDatabaseReference.child(FirebaseReferenceConstants.ArticleMeta)
    }

    val articleContentsDatabaseReference: DatabaseReference by lazy {
        articlesDatabaseReference.child(FirebaseReferenceConstants.ArticleContents)
    }

    fun getQuoteHightLightReference(): DatabaseReference {
        val reference = quoteOfTheWeekDatabaseReference
        reference.keepSynced(true)
        return reference
    }

    fun getSingleMovieDatabaseReference(movieName: String, mode: String): DatabaseReference {
        return if (mode == FirebaseReferenceConstants.ARTICLE_MODE_TEXT) {
            if (movieName.movieNameAsId().length >= 2) {
                moviesDatabaseReference.child(movieName.movieNameAsId().first().toString())
                    .child(movieName.movieNameAsId().take(2)).child(movieName.movieNameAsId())
            } else {
                moviesDatabaseReference.child(movieName.movieNameAsId().first().toString())
                    .child(movieName.movieNameAsId())
            }
        } else {
            moviesQuoteCardsDatabaseReference.child(movieName.movieNameAsId())
        }
    }

    fun getArticleContentDatabaseReference(): DatabaseReference {
        articleContentsDatabaseReference.keepSynced(true)
        return articleContentsDatabaseReference
    }

    fun getArticlesMetaDatabaseReference(): DatabaseReference {
        articleMetaDatabaseReference.keepSynced(true)
        return articleMetaDatabaseReference
    }

    fun writeUserToFirebaseDatabase(userMeta: UserMeta) {
        userDatabaseReference.child(userMeta.uid ?: FirebaseReferenceConstants.anon)
            .child(FirebaseReferenceConstants.profileInformation).setValue(userMeta)
    }

    fun writePremiumPurchaseInformationToFirebase(paidMeta: PaidMeta) {
        userDatabaseReference.child(
            FirebaseAuth.getInstance().uid ?: FirebaseReferenceConstants.anon
        ).child(FirebaseReferenceConstants.PremiumPurchases).setValue(paidMeta)
    }

    fun writeUserLikedQuoteToFirebaseDatabase(quoteId: String, isQuoteCard: Boolean) {

        if (!isQuoteCard) {
            userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            )
                .child(FirebaseReferenceConstants.likedQuotes)
                .child(quoteId).child(FirebaseReferenceConstants.quoteIdKey)
                .setValue(quoteId)
        } else {
            userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            )
                .child(FirebaseReferenceConstants.likedQuoteCards)
                .child(quoteId).child(FirebaseReferenceConstants.quoteIdKey)
                .setValue(quoteId)
        }

    }

    fun removeUserLikedQuoteFromFirebaseDatabase(quoteId: String, isQuoteCard: Boolean) {

        if (!isQuoteCard) {
            userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            )
                .child(FirebaseReferenceConstants.likedQuotes)
                .child(quoteId).child(FirebaseReferenceConstants.quoteIdKey)
                .setValue(null)
        } else {
            userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            )
                .child(FirebaseReferenceConstants.likedQuoteCards)
                .child(quoteId).child(FirebaseReferenceConstants.quoteIdKey)
                .setValue(null)
        }
    }

    val movieFetchConfig: PagedList.Config by lazy {
        PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(5)
            .setPageSize(5).build()
    }

    fun fetchMoviesByYearDatabaseReference(year: Int): Query {
        val databaseReference = moviesInYearsDatabaseReference.child(year.toString())
        databaseReference.keepSynced(true)
        return databaseReference
    }

    fun fetchMoviesForQuoteCardsDatabaseReference(): Query {
        val databaseReference =
            quotesCardMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesDatabaseReference)
        databaseReference.keepSynced(true)
        return databaseReference
    }

    fun fetchLikedQuotesFromUser(): Query {
        val reference =
            userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            )
                .child(FirebaseReferenceConstants.likedQuotes)
        reference.keepSynced(true)
        return reference
    }

    fun fetchLikedQuoteCardFromUser(): Query {
        val reference =
            fetchUserLikedQuotesReferenceQuery(true)
        reference.keepSynced(true)
        return reference
    }

    fun fetchMoviesBySearchFirebaseDatabase(searchText: String): Query {
        return moviesDatabaseReference.child(searchText.movieNameAsId().first().toString())
            .child(searchText.movieNameAsId().take(2))
            .orderByChild(FirebaseReferenceConstants.movieIdFirestoreKey)
            .startAt(searchText.movieNameAsId())
            .endAt(searchText.movieNameAsId() + "\uf8ff")
    }

    fun fetchQuoteCardsBySearchFirebaseDatabase(searchText: String): Query {
        return quotesCardMetaDatabaseReference.child(FirebaseReferenceConstants.MoviesSearchQuoteCardsDatabaseReference)
            .child(searchText.movieNameAsId().first().toString())
            .child(searchText.movieNameAsId().take(2))
            .orderByChild(FirebaseReferenceConstants.movieIdFirestoreKey)
            .startAt(searchText.movieNameAsId())
            .endAt(searchText.movieNameAsId() + "\uf8ff")
    }

    fun getTagsFromMoviesQueryFirebaseDatabase(movieId: String, isQuoteCard: Boolean): Query {
        return if (!isQuoteCard) moviesQuotesTagsDatabaseReference.child(movieId)
        else moviesQuoteCardsTagsDatabaseReference.child(movieId)
    }

    fun fetchUserLikedQuotesReferenceQuery(isQuoteCard: Boolean): DatabaseReference {
        return if (!isQuoteCard) userDatabaseReference.child(
            FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
        ).child(
            FirebaseReferenceConstants.likedQuotes
        ) else userDatabaseReference.child(
            FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
        ).child(
            FirebaseReferenceConstants.likedQuoteCards
        )
    }

    fun fetchArticleQueryReferenceQuery(articleId: String): DatabaseReference {
        return articleContentsDatabaseReference.child(articleId)
    }

    fun hasUserLikedQuote(isQuoteCard: Boolean, postCheck: (Boolean) -> Unit) {

        fetchUserLikedQuotesReferenceQuery(isQuoteCard).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        postCheck(isQuoteCard)
                    }
                }

            })
    }

    fun getArticleOnDisplayReference(): DatabaseReference {
        articlesOnDisplayDatabaseReference.keepSynced(true)
        return articlesOnDisplayDatabaseReference
    }

}