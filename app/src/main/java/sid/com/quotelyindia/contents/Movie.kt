package sid.com.quotelyindia.contents


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_movie.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.adapters.PopularQuoteCardsListAdapter
import sid.com.quotelyindia.adapters.QuotesListAdapter
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.groupiemodels.MovieTagsGroupieModel
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.utils.*

/**
 * A simple [Fragment] subclass.
 */
class Movie : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        val isQuoteCard =
            intent.getBooleanExtra(PassingDataConstants.PassQuoteCardInformation, false)

        addMovieInformation(isQuoteCard, getMoviesMeta())
        AdsUtils.displayBannerAd(this, banner_movie)
    }


    fun setUpRecyclerView(category: String, movie: String, isQuoteCard: Boolean) {
        tv_activity_movies_list.text = StringUtils.quotesTagsHeader(category)

        if (!isQuoteCard) {
            val quotesOptions: FirestorePagingOptions<QuotesMeta> =
                FirestorePagingOptions.Builder<QuotesMeta>().setLifecycleOwner(this).setQuery(
                    FirestoreUtils.fetchQuotesQuery(movie, category),
                    FirestoreUtils.quotesConfigFetch,
                    QuotesMeta::class.java
                ).build()

            val quotesListAdapter = QuotesListAdapter(
                options = quotesOptions,
                activity = this,
                isMovieSection = true,
                supportFragmentManager = supportFragmentManager
            )

            rv_activity_movie_quotes_list.apply {
                isNestedScrollingEnabled = false
                adapter = quotesListAdapter
                layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }

        } else {

            val quotesOptions =
                FirestoreRecyclerOptions.Builder<QuotesCardMeta>().setLifecycleOwner(this)
                    .setQuery(
                        FirestoreUtils.fetchQuotesCardQuery(movie, category),
                        QuotesCardMeta::class.java
                    ).build()

            val popularQuoteCardsListAdapter = PopularQuoteCardsListAdapter(
                quotesOptions,
                this,
                false,
                supportFragmentManager
            )

            rv_activity_movie_quotes_list.apply {
                isNestedScrollingEnabled = false
                adapter = popularQuoteCardsListAdapter
                layoutManager = GridLayoutManager(baseContext, 2)
            }

        }

    }

    // Setup View for Movie Tags
    private fun setUpViews(moviesMeta: MoviesMeta, isQuoteCard: Boolean) {


        setUpRecyclerView(FirebaseReferenceConstants.all, moviesMeta.movie, isQuoteCard)

        val items = mutableListOf<Item>()
        items.add(
            MovieTagsGroupieModel(
                FirebaseReferenceConstants.all,
                moviesMeta.movie,
                baseContext,
                isQuoteCard
            )
        )

        FirebaseDatabaseUtils.getTagsFromMoviesQueryFirebaseDatabase(
            moviesMeta.movieid,
            isQuoteCard
        )
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        for (tag in p0.children) {
                            items.add(
                                MovieTagsGroupieModel(
                                    tag.value.toString(),
                                    moviesMeta.movie,
                                    baseContext,
                                    isQuoteCard
                                )
                            )
                        }
                    }
                    val section = Section(items)
                    rv_activity_movie_category_tags.apply {
                        layoutManager =
                            LinearLayoutManager(
                                baseContext,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        adapter = GroupAdapter<GroupieViewHolder>().apply {
                            add(section)
                        }
                        isNestedScrollingEnabled = false
                    }
                }

            })

    }


    private fun addMovieInformation(isQuoteCard: Boolean, movieMeta: MoviesMeta) {
        tv_movie_title.text = movieMeta.movie
        tv_movie_year.text = movieMeta.year.toString()
        Glide.with(baseContext)
            .load(FirebaseStorageUtils.getMoviePosterReferenceById(movieMeta.movieid))
            .into(iv_movie_card)
        setUpViews(movieMeta, isQuoteCard)
    }


    private fun getMoviesMeta(): MoviesMeta {
        return MoviesMeta(
            movie = intent.getStringExtra(PassingDataConstants.PassMovieName) ?: "",
            movieid = intent.getStringExtra(PassingDataConstants.PassMovieId) ?: "",
            year = intent.getIntExtra(PassingDataConstants.PassMovieYear, 0)
        )
    }

}
