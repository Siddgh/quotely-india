package sid.com.quotelyindia.contents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.activity_explore_movies.*
import kotlinx.android.synthetic.main.merge_header_list_layout.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.adapters.PopularQuoteCardsListAdapter
import sid.com.quotelyindia.adapters.PopularQuotesListAdapter
import sid.com.quotelyindia.adapters.RecentMoviesListAdapter
import sid.com.quotelyindia.adapters.TagsPagingAdapter
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.models.data.TagsListModel
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.FirestoreUtils
import sid.com.quotelyindia.utils.NavigationUtils

class Explore : AppCompatActivity() {

    var isQuoteCard: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_movies)
        val exploreTo = intent.getStringExtra(PassingDataConstants.PassExploreTo) ?: ""
        isQuoteCard = intent.getBooleanExtra(PassingDataConstants.PassQuoteCardInformation, false)
        tv_explore_big_title.text = exploreTo
        setupRecyclerViewForRecentMovies(exploreTo)
        clickAction(exploreTo)
        displayAds()
    }

    private fun displayAds() {
        AdsUtils.displayBannerAd(context = this, adView = include_explore_recentlyadded.banner_list)
    }

    private fun clickAction(exploreTo: String) {
        cv_movies_all_text_quotes.setOnClickListener {
            when {
                exploreTo.contentEquals(PassingDataConstants.PassMovies) -> {
                    NavigationUtils.goToMovies(baseContext, false)
                }
                exploreTo.contentEquals(PassingDataConstants.PassFavourites) -> {
                    NavigationUtils.goToTags(
                        baseContext,
                        PassingDataConstants.PassFavourites,
                        false
                    )
                }
                else -> {
                    AdsUtils.displayInterstitialAd(baseContext, this) {
                        NavigationUtils.goToTags(baseContext, exploreTo, false)
                    }
                }
            }
        }

        cv_movies_all_quotes_cards.setOnClickListener {
            when {
                exploreTo.contentEquals(PassingDataConstants.PassMovies) -> {
                    NavigationUtils.goToMovies(baseContext, true)
                }
                exploreTo.contentEquals(PassingDataConstants.PassFavourites) -> {
                    NavigationUtils.goToTags(baseContext, PassingDataConstants.PassFavourites, true)
                }
                else -> {
                    AdsUtils.displayInterstitialAd(baseContext, this) {
                        NavigationUtils.goToTags(baseContext, exploreTo, true)
                    }
                }
            }
        }

    }

    private fun hideQuotes(isQuoteCard: Boolean) {
        if (!isQuoteCard) {
            cv_movies_all_text_quotes.visibility = View.VISIBLE
        } else {
            cv_movies_all_quotes_cards.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerViewForRecentMovies(exploreTo: String) {

        if (!exploreTo.contentEquals(PassingDataConstants.PassMovies)) {
            include_explore_tags_list.visibility = View.GONE
            include_explore_recentlyadded.visibility = View.GONE
            include_explore_quote_card_tags_list.visibility = View.GONE

            if (exploreTo.contentEquals(PassingDataConstants.PassFavourites)) {
                include_explore_popular_quotes.visibility = View.GONE
                cv_movies_all_text_quotes.visibility = View.GONE
                cv_movies_all_quotes_cards.visibility = View.GONE
                include_explore_popular_quote_cards.tv_header.visibility = View.GONE
                FirebaseDatabaseUtils.hasUserLikedQuote(false, ::hideQuotes)
                FirebaseDatabaseUtils.hasUserLikedQuote(true, ::hideQuotes)
            }

            if (!isQuoteCard) {
                include_explore_popular_quote_cards.tv_header.visibility = View.GONE
                include_explore_popular_quote_cards.rv_list.visibility = View.GONE
                cv_movies_all_quotes_cards.visibility = View.GONE
            }

        } else {
            include_explore_recentlyadded.tv_header.text = getString(R.string.recently_added_header)
            include_explore_tags_list.tv_header.text = getString(R.string.tags_list_header)
            include_explore_quote_card_tags_list.tv_header.text =
                getString(R.string.quote_card_tags_list_header)

            val query = FirebaseDatabaseUtils.recentDatabaseReference
            val recentlyAddedOptions =
                FirebaseRecyclerOptions.Builder<MoviesMeta>().setLifecycleOwner(this)
                    .setQuery(query, MoviesMeta::class.java)
                    .build()

            val recentMoviesListAdapter =
                RecentMoviesListAdapter(recentlyAddedOptions, baseContext, false, this)
            include_explore_recentlyadded.rv_list.apply {
                layoutManager =
                    LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = recentMoviesListAdapter
                isNestedScrollingEnabled = false
            }

            val tagsPagingQuery: Query = FirebaseDatabaseUtils.tagsDatabaseReference
            val tagsPagingConfig =
                PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(1)
                    .setPageSize(1).build()

            val options = DatabasePagingOptions.Builder<TagsListModel>().setLifecycleOwner(this)
                .setQuery(tagsPagingQuery, tagsPagingConfig, TagsListModel::class.java).build()

            val tagsPagingAdapter = TagsPagingAdapter(options, baseContext, false)
            include_explore_tags_list.rv_list.apply {
                layoutManager =
                    LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                adapter = tagsPagingAdapter
                isNestedScrollingEnabled = false
            }

            val quoteCardTagsPagingQuery: Query =
                FirebaseDatabaseUtils.tagsQuoteCardsDatabaseReference
            val quoteCardTagsPagingConfig =
                PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(1)
                    .setPageSize(1).build()

            val quoteCardTagsOptions =
                DatabasePagingOptions.Builder<TagsListModel>().setLifecycleOwner(this)
                    .setQuery(
                        quoteCardTagsPagingQuery,
                        quoteCardTagsPagingConfig,
                        TagsListModel::class.java
                    ).build()

            val quoteCardTagsPagingAdapter =
                TagsPagingAdapter(quoteCardTagsOptions, baseContext, true)
            include_explore_quote_card_tags_list.rv_list.apply {
                layoutManager =
                    LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                adapter = quoteCardTagsPagingAdapter
                isNestedScrollingEnabled = false
            }

        }


        //      Setting Up Popular Text Quotes Recycler View

        include_explore_popular_quotes.tv_header.text = getString(R.string.popular_quotes_header)

        val popularQuotesOptions =
            FirestoreRecyclerOptions.Builder<QuotesMeta>().setLifecycleOwner(this)
                .setQuery(FirestoreUtils.fetchPopularQuotes(exploreTo), QuotesMeta::class.java)
                .build()

        val popularQuotesListAdapter = PopularQuotesListAdapter(
            popularQuotesOptions,
            this,
            false,
            supportFragmentManager
        )

        include_explore_popular_quotes.rv_list.apply {
            isNestedScrollingEnabled = false
            adapter = popularQuotesListAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        }

        //      Setting Up Popular Quote Cards Recycler View

        include_explore_popular_quote_cards.tv_header.text =
            getString(R.string.popular_quotes_card_header)

        val popularQuotesCardOptions =
            FirestoreRecyclerOptions.Builder<QuotesCardMeta>().setLifecycleOwner(this)
                .setQuery(
                    FirestoreUtils.fetchPopularQuoteCards(exploreTo),
                    QuotesCardMeta::class.java
                ).build()

        val popularQuoteCardsListAdapter = PopularQuoteCardsListAdapter(
            popularQuotesCardOptions,
            this,
            false,
            supportFragmentManager
        )

        include_explore_popular_quote_cards.rv_list.apply {
            isNestedScrollingEnabled = false
            adapter = popularQuoteCardsListAdapter
            layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        }

    }

}