package sid.com.quotelyindia.contents


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_movies.*

import sid.com.quotelyindia.R
import sid.com.quotelyindia.adapters.MovieListAdapter
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.NavigationUtils

/**
 * A simple [Fragment] subclass.
 */
class Movies : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)
        AdsUtils.displayBannerAd(this, banner_movies)

        val isQuoteCard =
            intent.getBooleanExtra(PassingDataConstants.PassQuoteCardInformation, false)

        if (!isQuoteCard) {
            setUpSpinner(isQuoteCard)
            FirebaseDatabaseUtils.endYearDatabaseReference.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    spinnerClickChangeInitiate(p0.getValue(Int::class.java) ?: 2010, isQuoteCard)
                }

            })
        } else {
            spinner_year_movies.visibility = View.GONE
            setUpRecyclerView(0, isQuoteCard)
        }

        tv_movies_search.setOnClickListener {
            NavigationUtils.goToMoviesSearch(baseContext, isQuoteCard)
        }
    }

    private fun setUpSpinner(isQuoteCard: Boolean) {
        FirebaseDatabaseUtils.endYearDatabaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val endYear: Int? = p0.getValue(Int::class.java)
                val items = mutableListOf<String>()
                for (i in endYear!! downTo FirebaseReferenceConstants.startYear) {
                    items.add(i.toString())
                }
                val spinnerAdapter =
                    ArrayAdapter(baseContext, R.layout.spinner_year_list_layout, items)
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_year_list_layout)
                spinner_year_movies.adapter = spinnerAdapter
            }

        })

        spinner_year_movies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerClickChangeInitiate(
                    parent?.getItemAtPosition(position).toString().toInt(),
                    isQuoteCard
                )
            }

        }

    }

    private fun spinnerClickChangeInitiate(selectedText: Int, isQuoteCard: Boolean) {
        setUpRecyclerView(selectedText, isQuoteCard)
    }

    private fun setUpRecyclerView(year: Int, isQuoteCard: Boolean) {
        lateinit var options: DatabasePagingOptions<MoviesMeta>

        if (!isQuoteCard) {

            options =
                DatabasePagingOptions.Builder<MoviesMeta>().setLifecycleOwner(this).setQuery(
                    FirebaseDatabaseUtils.fetchMoviesByYearDatabaseReference(year),
                    FirebaseDatabaseUtils.movieFetchConfig,
                    MoviesMeta::class.java
                ).build()


        } else {

            options =
                DatabasePagingOptions.Builder<MoviesMeta>().setLifecycleOwner(this).setQuery(
                    FirebaseDatabaseUtils.fetchMoviesForQuoteCardsDatabaseReference(),
                    FirebaseDatabaseUtils.movieFetchConfig,
                    MoviesMeta::class.java
                ).build()

        }

        rv_movies_search_list.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(this@Movies, 3)
            adapter = MovieListAdapter(options, context, isQuoteCard, this@Movies)
            hasFixedSize()
        }
    }

    override fun onResume() {
        super.onResume()
    }

}
