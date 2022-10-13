package sid.com.quotelyindia.contents


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.activity_movies_search.*

import sid.com.quotelyindia.R
import sid.com.quotelyindia.adapters.MoviesSearchListAdapter
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.StringUtils

/**
 * A simple [Fragment] subclass.
 */
class MoviesSearch : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_search)
        AdsUtils.displayBannerAd(this, banner_movies_search)

        val isQuoteCard =
            intent.getBooleanExtra(PassingDataConstants.PassQuoteCardInformation, false)

        ed_movies_search.requestFocus()
        val imgr: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(ed_movies_search, InputMethodManager.SHOW_IMPLICIT)
        tv_movies_search_listing.text = MessagesConstants.SearchMessage
        ed_movies_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    setupRecyclerView(s.toString(), isQuoteCard)
                    tv_movies_search_listing.text = StringUtils.searchingForMessage(s.toString())
                } else {
                    tv_movies_search_listing.text = MessagesConstants.SearchingMessage
                }
            }

        })
    }

    private fun setupRecyclerView(searchText: String, isQuoteCard: Boolean) {

        var options: FirebaseRecyclerOptions<MoviesMeta> = if (!isQuoteCard) {
            FirebaseRecyclerOptions.Builder<MoviesMeta>().setLifecycleOwner(this).setQuery(
                FirebaseDatabaseUtils.fetchMoviesBySearchFirebaseDatabase(searchText),
                MoviesMeta::class.java
            ).build()
        } else {
            FirebaseRecyclerOptions.Builder<MoviesMeta>().setLifecycleOwner(this).setQuery(
                FirebaseDatabaseUtils.fetchQuoteCardsBySearchFirebaseDatabase(searchText),
                MoviesMeta::class.java
            ).build()
        }


        rv_movies_search_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this@MoviesSearch, 3)
            adapter = MoviesSearchListAdapter(options, context, isQuoteCard, this@MoviesSearch)
            hasFixedSize()
        }
    }


}
