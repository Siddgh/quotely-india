package sid.com.quotelyindia.constants

object FirebaseReferenceConstants {

    // Realtime Database References
    const val QuoteOfTheWeekDatabaseReference = "quote-of-the-week"

    // Text Quotes Database References
    const val TextQuotesMetaDatabaseReference = "text-quotes-card"
    const val RecentMoviesDatabaseReference = "recent"
    const val TagsDatabaseReference = "tags"
    const val MovieMetaDatabaseReference = "moviemeta"
    const val EndYearDatabaseReference = "endyear"
    const val MoviesDatabaseReference = "movies"
    const val MoviesInYearsDatabaseReference = "moviesinyears"
    const val MoviesQuotesCountDatabaseReference = "quotescounts"
    const val MoviesQuotesTagsDatabaseReference = "movietags"

    // User Database Reference
    const val UserDatabaseReference = "users"
    const val profileInformation = "profile-info"
    const val likedQuotes = "liked-quotes"
    const val likedQuoteCards = "liked-quote-cards"
    const val PremiumPurchases = "premium-purchase-information"

    // Quote Cards Database References
    const val QuoteCardsMetaDatabaseReference = "quote-cards-meta"
    const val MoviesSearchQuoteCardsDatabaseReference = "movies-search"
    const val MoviesQuoteCardsTagsDatabaseReference = "movie-tags"

    // Articles Database References
    const val Articles = "articles"
    const val ArticlesOnDisplay = "articles-on-display"
    const val ArticleMeta = "meta"
    const val ArticleContents = "contents"

    const val ARTICLE_TYPE_POPULAR_MOVIE = "popular-movie"
    const val ARTICLE_TYPE_POPULAR_PERSON = "popular-person"
    const val ARTICLE_TYPE_LESSONS = "lessons"
    const val ARTICLE_TYPE_COLLECTIONS = "collections"
    const val ARTICLE_TYPE_GENERIC = "generic"

    const val ARTICLE_MODE_TEXT = "text"
    const val ARTICLE_MODE_CARD = "card"


    // Storage Reference
    const val PosterStorageReference = "posters"
    const val QuoteCardsStorageReference = "quotecards"
    const val QuoteCardsFullSizeStorageReference = "quote_cards_full_size"
    const val QuoteCardsFullThumnailsStorageReference = "quote_cards_thumbnails"

    // Firestore Reference
    const val MoviesFirestoreReference = "movies"
    const val QuotesFirestoreReference = "quotes"
    const val QuoteCardsFirestoreReference = "quotecards"


    // Firebase Analytics Keys
    const val FirebaseAnalyticsLogAdsType = "ads_display_type"
    const val FirebaseAnalyticsLogPremiumVersion = "bought_premium_version"
    const val FirebaseAnalyticsUserProperty = "app_version"
    const val FirebaseAnalyticsLogMovieClick = "movie_viewed"
    const val FirebaseAnalyticsLogTagsClick = "tag_viewed"
    const val FirebaseAnalyticsLogQuoteViewClick = "quote_viewed"
    const val FirebaseAnalyticsUsingWidgetUserProperty = "widget_usage"
    const val FirebaseAnalyticsLogActionOnQuote = "actiion_on_quote"

    // Firebase Analytics Values
    const val FirebaseAnalyticsAdsBanner = "Banner Ad"
    const val FirebaseAnalyticsAdsInterstitial = "Interstitial Ad"
    const val FirebaseAnalyticsAdsReward = "Reward Ad"
    const val FirebaseAnalyticsFreemiumUser = "Freemium"
    const val FirebaseAnalyticsPremiumUser = "Premium"
    const val FirebaseAnalyticsWidgetUsed = "Widget On"
    const val FirebaseAnalyticsWidgetNotUsed = "Widget Off"
    const val FirebaseAnalyticsLogViewTypeCard = "Card"
    const val FirebaseAnalyticsLogViewTypeText = "Text"
    const val FirebaseAnalyticsLogClickType = "type"
    const val FirebaseAnalyticsActionOnQuoteCopy = "Copy"
    const val FirebaseAnalyticsActionOnQuoteShare = "Share"
    const val FirebaseAnalyticsActionOnQuoteLike = "Like"
    const val FirebaseAnalyticsActionOnQuoteUnLike = "Unliked"
    const val FirebaseAnalyticsActionOnQuoteDownload = "Download"

    // Firebase Generic Keys
    const val yearFirestoreKey = "year"
    const val likesFirestoreKey = "likes"
    const val movieFirestoreKey = "movie"
    const val tagFirestoreKey = "tag"
    const val quoteIdKey = "quoteId"
    const val startYear = 1950
    const val movieIdFirestoreKey = "movieid"
    const val content1Key = "content1"
    const val saidByFirestoreKey = "saidBy"
    const val inFrameFirestoreKey = "inframe"
    const val isPaid = "isPaid"

    const val anon = "anon"
    const val all = "All"
    const val name = "name"

    // Notification Constants
    const val DataQuote = "DATA_QUOTE"
    const val DataMovie = "DATA_MOVIE"
    const val DataYear = "DATA_YEAR"
    const val DataSaidBy = "DATA_SAIDBY"
    const val DataTag = "DATA_TAG"
    const val DataQuoteId = "DATA_QUOTE_ID"
    const val DataLikes = "DATA_LIKES"

}