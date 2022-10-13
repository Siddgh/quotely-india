package sid.com.quotelyindia.contents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import kotlinx.android.synthetic.main.activity_tags.*
import kotlinx.android.synthetic.main.merge_header_list_layout.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.adapters.FavourtiesListAdapter
import sid.com.quotelyindia.adapters.PopularQuoteCardsListAdapter
import sid.com.quotelyindia.adapters.QuotesListAdapter
import sid.com.quotelyindia.adapters.TagsListAdapter
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.QuoteIdModel
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.FirestoreUtils
import sid.com.quotelyindia.utils.StringUtils

class Tags : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)

        AdsUtils.displayBannerAd(this, include_tags_all_quotes.banner_list)
        val exploreTo = intent.getStringExtra(PassingDataConstants.PassExploreTo) ?: ""
        val isQuoteCard =
            intent.getBooleanExtra(PassingDataConstants.PassQuoteCardInformation, false)
        setUpRecyclerView(exploreTo, isQuoteCard)
    }

    private fun setUpRecyclerView(exploreTo: String, isQuoteCard: Boolean) {

        tv_tags_big_title.text = exploreTo

        if (exploreTo.contentEquals(PassingDataConstants.PassFavourites)) {

            if (!isQuoteCard) {
                include_tags_all_quotes.tv_header.text = MessagesConstants.ListOfFavouriteQuotes
                include_tags_all_quotes.tv_header.text = StringUtils.quotesTagsHeader(exploreTo)

                val options: DatabasePagingOptions<QuoteIdModel> =
                    DatabasePagingOptions.Builder<QuoteIdModel>().setLifecycleOwner(this).setQuery(
                        FirebaseDatabaseUtils.fetchLikedQuotesFromUser(),
                        FirebaseDatabaseUtils.movieFetchConfig,
                        QuoteIdModel::class.java
                    ).build()

                val favouritesListAdapter = FavourtiesListAdapter(
                    activity = this,
                    supportFragmentManager = supportFragmentManager,
                    options = options,
                    isQuoteCard = false
                )

                include_tags_all_quotes.rv_list.apply {
                    isNestedScrollingEnabled = false
                    adapter = favouritesListAdapter
                    layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

                }
            } else {
                include_tags_all_quotes.tv_header.text =
                    MessagesConstants.ListOfFavouritesQuoteCards
                include_tags_all_quotes.tv_header.text = StringUtils.quoteCardTagsHeader(exploreTo)

                val options: DatabasePagingOptions<QuoteIdModel> =
                    DatabasePagingOptions.Builder<QuoteIdModel>().setLifecycleOwner(this).setQuery(
                        FirebaseDatabaseUtils.fetchLikedQuoteCardFromUser(),
                        FirebaseDatabaseUtils.movieFetchConfig,
                        QuoteIdModel::class.java
                    ).build()

                val favouritesListAdapter = FavourtiesListAdapter(
                    activity = this,
                    supportFragmentManager = supportFragmentManager,
                    options = options,
                    isQuoteCard = true
                )

                include_tags_all_quotes.rv_list.apply {
                    isNestedScrollingEnabled = false
                    adapter = favouritesListAdapter
                    layoutManager = GridLayoutManager(baseContext, 2)
                }

            }


        } else {

            if (!isQuoteCard) {
                include_tags_all_quotes.tv_header.text = StringUtils.quotesTagsHeader(exploreTo)

                val quotesOptions: FirestorePagingOptions<QuotesMeta> =
                    FirestorePagingOptions.Builder<QuotesMeta>().setLifecycleOwner(this).setQuery(
                        FirestoreUtils.fetchAllQuotesByTagsQuery(exploreTo),
                        FirestoreUtils.quotesConfigFetch,
                        QuotesMeta::class.java
                    ).build()

                val quotesListAdapter = TagsListAdapter(
                    options = quotesOptions,
                    activity = this,
                    supportFragmentManager = supportFragmentManager
                )

                include_tags_all_quotes.rv_list.apply {
                    isNestedScrollingEnabled = false
                    adapter = quotesListAdapter
                    layoutManager =
                        LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
                }
            } else {

                include_tags_all_quotes.tv_header.text = StringUtils.quoteCardTagsHeader(exploreTo)

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

                include_tags_all_quotes.rv_list.apply {
                    isNestedScrollingEnabled = false
                    adapter = popularQuoteCardsListAdapter
                    layoutManager = GridLayoutManager(baseContext, 2)
                }

            }
        }


    }

    override fun onResume() {
        super.onResume()
    }

}
