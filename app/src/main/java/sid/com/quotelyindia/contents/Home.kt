package sid.com.quotelyindia.contents

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.merge_qotw_header.*
import kotlinx.android.synthetic.main.merge_header_list_layout.*
import kotlinx.android.synthetic.main.merge_header_list_layout.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.SignIn
import sid.com.quotelyindia.adapters.ArticlesOnDisplayAdapter
import sid.com.quotelyindia.adapters.RecentMoviesListAdapter
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.models.data.TagsListModel
import sid.com.quotelyindia.utils.*
import sid.com.quotelyindia.utils.StringUtils.quote


class Home : AppCompatActivity() {

    private lateinit var quoteBottomSheet: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        displayBannerAds()
        FirebaseMessagingUtils.subscribeToNotificationTopic(this)

        main_toolbar.title = ""
        setSupportActionBar(main_toolbar)

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            main_drawer_layout,
            main_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        main_drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        main_navigation_menu.setNavigationItemSelectedListener {
            main_drawer_layout.closeDrawers()
            when (it.itemId) {
                R.id.nav_favourites -> {
                    NavigationUtils.goToExplore(
                        baseContext,
                        PassingDataConstants.PassFavourites,
                        true
                    )
                }
                R.id.nav_movies -> {
                    NavigationUtils.goToExplore(baseContext, PassingDataConstants.PassMovies, true)
                }
                R.id.nav_premium -> {
                    startActivity(Intent(baseContext, Premium::class.java))
                }
                R.id.nav_instagram -> {
                    val uri = Uri.parse(getString(R.string.instagram_profile))
                    val goToInstagram = Intent(Intent.ACTION_VIEW, uri)
                    goToInstagram.setPackage(getString(R.string.instagram_package_name))
                    try {
                        startActivity(goToInstagram)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.instagram_profile_web))
                            )
                        )
                    }

                }
                R.id.nav_settings -> {
                    val i = Intent(this, Settings::class.java)
                    startActivity(i)
                }
                R.id.nav_logout -> {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        Toast.makeText(this, MessagesConstants.UserSignedOut, Toast.LENGTH_LONG)
                            .show()
                        val intent = Intent(this, SignIn::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            true
        }

        FirestoreUtils.readQuoteOfTheWeek {
            val quotesMeta = it
            tv_quoteoftheweek_movie.text =
                quotesMeta?.movie ?: MessagesConstants.FailedToGetMovieName
            tv_quoteoftheweek_quote.text =
                quotesMeta?.quotes?.quote()
                    ?: MessagesConstants.FailedToGetQuote
            tv_quoteoftheweek_like.text =
                StringUtils.displayLikes(quotesMeta?.likes ?: 0)
            ViewSetupUtils.setUpTagsRecyclerView(
                rv_qotw_tags,
                quotesMeta!!,
                this
            )

            cl_quoteoftheweek.setOnClickListener {
                val progressDialog = ProgressDialog(this)
                progressDialog.startLoadingDialog()
                NavigationUtils.showQuotesBottomSheetFragment(
                    supportFragmentManager,
                    progressDialog,
                    quotesMeta,
                    false
                )

            }
        }

        setupRecyclerViewForRecentMovies()
        setUpUserProfile(main_navigation_menu)
        setUpArticles()
    }

    private fun setUpArticles() {

        val articlesOverViewQuery: Query = FirebaseDatabaseUtils.getArticleOnDisplayReference()
        val articlesPagingConfig =
            PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(1)
                .setPageSize(1).build()

        val options = DatabasePagingOptions.Builder<TagsListModel>().setLifecycleOwner(this)
            .setQuery(articlesOverViewQuery, articlesPagingConfig, TagsListModel::class.java)
            .build()

        val articlesOverViewAdapter =
            ArticlesOnDisplayAdapter(options, baseContext, supportFragmentManager, this)
        rv_activity_home_articles_overview.apply {
            layoutManager =
                LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
            adapter = articlesOverViewAdapter
            isNestedScrollingEnabled = false
        }

    }

    fun fetchQuoteCardRecyclerOptions(movieName: String): FirestoreRecyclerOptions<QuotesCardMeta> {
        return FirestoreRecyclerOptions.Builder<QuotesCardMeta>().setLifecycleOwner(this)
            .setQuery(
                FirestoreUtils.fetchPopularQuotesFromAMovie(
                    movieName, true
                ),
                QuotesCardMeta::class.java
            ).build()
    }

    fun fetchTextQuotesRecyclerOptions(movieName: String): FirestoreRecyclerOptions<QuotesMeta> {
        return FirestoreRecyclerOptions.Builder<QuotesMeta>().setLifecycleOwner(this)
            .setQuery(
                FirestoreUtils.fetchPopularQuotesFromAMovie(
                    movieName = movieName,
                    isQuoteCard = false
                ), QuotesMeta::class.java
            )
            .build()
    }

    fun fetchQuoteCardRecyclerOptionsFromPopularPerson(saidBy: String): FirestoreRecyclerOptions<QuotesCardMeta> {
        return FirestoreRecyclerOptions.Builder<QuotesCardMeta>().setLifecycleOwner(this)
            .setQuery(
                FirestoreUtils.fetchPopularQuotesFromPopularPerson(
                    saidBy, true
                ),
                QuotesCardMeta::class.java
            ).build()
    }

    fun fetchTextQuotesRecyclerOptionsForPopularPerson(saidBy: String): FirestoreRecyclerOptions<QuotesMeta> {
        return FirestoreRecyclerOptions.Builder<QuotesMeta>().setLifecycleOwner(this)
            .setQuery(
                FirestoreUtils.fetchPopularQuotesFromPopularPerson(
                    saidBy = saidBy,
                    isQuoteCard = false
                ), QuotesMeta::class.java
            )
            .build()
    }

    fun fetchTextQuotesRecyclerOptionsForGenericPurpose(articleId: String): DatabasePagingOptions<TagsListModel> {
        return DatabasePagingOptions.Builder<TagsListModel>()
            .setLifecycleOwner(this).setQuery(
                FirebaseDatabaseUtils.fetchArticleQueryReferenceQuery(articleId),
                FirebaseDatabaseUtils.movieFetchConfig,
                TagsListModel::class.java
            ).build()
    }


    private fun setUpUserProfile(mainNavigationMenu: NavigationView?) {
        val headerView = mainNavigationMenu?.getHeaderView(0)
        FirebaseAuthUtils.setUserProfile(
            headerView!!.findViewById(R.id.tv_user_name),
            headerView.findViewById(R.id.tv_user_email),
            headerView.findViewById(R.id.iv_user_profile),
            baseContext
        )
    }

    private fun setupRecyclerViewForRecentMovies() {

        tv_header.text = getString(R.string.recently_added_header)

        val query = FirebaseDatabaseUtils.recentDatabaseReference
        val options =
            FirebaseRecyclerOptions.Builder<MoviesMeta>().setLifecycleOwner(this)
                .setQuery(query, MoviesMeta::class.java)
                .build()

        val recentMoviesListAdapter = RecentMoviesListAdapter(options, baseContext, false, this)
        rv_list.apply {
            layoutManager =
                LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentMoviesListAdapter
            isNestedScrollingEnabled = false
        }

    }

    private fun displayBannerAds() {
        AdsUtils.displayBannerAd(this, include_home_recently_added.banner_list)
        AdsUtils.showAdsOrNot(this) {
            if (it) {
                val menu = main_navigation_menu.menu
                menu.findItem(R.id.nav_premium).isVisible = false
            }
        }
    }

}
