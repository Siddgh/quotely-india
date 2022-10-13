package sid.com.quotelyindia.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_articles_overview.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.contents.Home
import sid.com.quotelyindia.models.data.ArticleMeta
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.models.data.TagsListModel
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.NavigationUtils

class ArticlesOnDisplayAdapter(
    options: DatabasePagingOptions<TagsListModel>,
    private val c: Context,
    private val supportFragmentManager: FragmentManager,
    private val activity: Activity
) :
    FirebaseRecyclerPagingAdapter<TagsListModel, ArticlesOnDisplayAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_articles_overview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: TagsListModel) {
        FirebaseDatabaseUtils.getArticlesMetaDatabaseReference().child(model.name ?: "article01")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val articleMeta = p0.getValue(ArticleMeta::class.java)
                        holder.itemView.tv_layout_articles_overview.text = articleMeta?.title ?: ""

                        when {
                            articleMeta?.type ?: FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC
                                    == FirebaseReferenceConstants.ARTICLE_TYPE_COLLECTIONS || articleMeta?.type ?: FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC
                                    == FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC || articleMeta?.type ?: FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC
                                    == FirebaseReferenceConstants.ARTICLE_TYPE_LESSONS -> {

                                if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_TEXT) {

                                    val articleCollectionsListAdapter =
                                        ArticleCollectionsListAdapter(
                                            activity = activity,
                                            supportFragmentManager = supportFragmentManager,
                                            options = (activity as Home).fetchTextQuotesRecyclerOptionsForGenericPurpose(
                                                articleMeta.id
                                            ),
                                            isQuoteCard = false
                                        )

                                    holder.itemView.rv_layout_articles_overview.apply {
                                        isNestedScrollingEnabled = false
                                        adapter = articleCollectionsListAdapter
                                        layoutManager = StaggeredGridLayoutManager(
                                            2,
                                            LinearLayoutManager.VERTICAL
                                        )

                                    }

                                } else if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_CARD) {

                                    val articleCollectionsListAdapter =
                                        ArticleCollectionsListAdapter(
                                            activity = activity,
                                            supportFragmentManager = supportFragmentManager,
                                            options = (activity as Home).fetchTextQuotesRecyclerOptionsForGenericPurpose(
                                                articleMeta.id
                                            ),
                                            isQuoteCard = true
                                        )

                                    holder.itemView.rv_layout_articles_overview.apply {
                                        isNestedScrollingEnabled = false
                                        adapter = articleCollectionsListAdapter
                                        layoutManager = LinearLayoutManager(
                                            c,
                                            LinearLayoutManager.HORIZONTAL,
                                            false
                                        )

                                    }
                                }

                            }
                            articleMeta?.type ?: FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC
                                    == FirebaseReferenceConstants.ARTICLE_TYPE_POPULAR_MOVIE -> {

                                FirebaseDatabaseUtils.getArticleContentDatabaseReference().child(
                                    articleMeta?.id ?: ""
                                ).child(FirebaseReferenceConstants.content1Key)
                                    .child(FirebaseReferenceConstants.name)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                val movieName = p0.getValue(String::class.java)
                                                if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_TEXT) {

                                                    holder.itemView.mb_layout_articles_overview.visibility =
                                                        View.VISIBLE

                                                    holder.itemView.mb_layout_articles_overview.text =
                                                        "More Quotes from $movieName"

                                                    holder.itemView.mb_layout_articles_overview.setOnClickListener {
                                                        FirebaseDatabaseUtils.getSingleMovieDatabaseReference(
                                                            movieName ?: "",
                                                            FirebaseReferenceConstants.ARTICLE_MODE_TEXT
                                                        ).addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {
                                                                if (p0.exists()) {
                                                                    val moviesMeta =
                                                                        p0.getValue(MoviesMeta::class.java)
                                                                            ?: MoviesMeta(0, "", "")
                                                                    NavigationUtils.AddToMovie(
                                                                        activity,
                                                                        activity.baseContext,
                                                                        moviesMeta,
                                                                        false
                                                                    )
                                                                }
                                                            }

                                                        })
                                                    }

                                                    val popularQuotesListAdapter =
                                                        PopularQuotesListAdapter(
                                                            (activity as Home).fetchTextQuotesRecyclerOptions(
                                                                movieName ?: ""
                                                            ),
                                                            activity,
                                                            false,
                                                            supportFragmentManager
                                                        )

                                                    holder.itemView.rv_layout_articles_overview.apply {
                                                        isNestedScrollingEnabled = false
                                                        adapter = popularQuotesListAdapter
                                                        layoutManager = StaggeredGridLayoutManager(
                                                            2,
                                                            LinearLayoutManager.VERTICAL
                                                        )

                                                    }

                                                } else if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_CARD) {

                                                    holder.itemView.mb_layout_articles_overview.visibility =
                                                        View.VISIBLE

                                                    holder.itemView.mb_layout_articles_overview.text =
                                                        "More Quotes from $movieName"

                                                    holder.itemView.mb_layout_articles_overview.setOnClickListener {


                                                        FirebaseDatabaseUtils.getSingleMovieDatabaseReference(
                                                            movieName ?: "",
                                                            FirebaseReferenceConstants.ARTICLE_MODE_CARD
                                                        ).addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onCancelled(p0: DatabaseError) {

                                                            }

                                                            override fun onDataChange(p0: DataSnapshot) {

                                                                if (p0.exists()) {
                                                                    val moviesMeta =
                                                                        p0.getValue(MoviesMeta::class.java)
                                                                            ?: MoviesMeta(0, "", "")

                                                                    NavigationUtils.AddToMovie(
                                                                        activity,
                                                                        activity.baseContext,
                                                                        moviesMeta,
                                                                        true
                                                                    )
                                                                }
                                                            }

                                                        })
                                                    }

                                                    val popularQuoteCardsListAdapter =
                                                        PopularQuoteCardsListAdapter(
                                                            (activity as Home).fetchQuoteCardRecyclerOptions(
                                                                movieName ?: ""
                                                            ),
                                                            activity,
                                                            false,
                                                            supportFragmentManager
                                                        )

                                                    holder.itemView.rv_layout_articles_overview.apply {
                                                        isNestedScrollingEnabled = false
                                                        adapter = popularQuoteCardsListAdapter
                                                        layoutManager = LinearLayoutManager(
                                                            c,
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false
                                                        )
                                                    }

                                                }
                                            }
                                        }

                                    })

                            }
                            articleMeta?.type ?: FirebaseReferenceConstants.ARTICLE_TYPE_GENERIC
                                    == FirebaseReferenceConstants.ARTICLE_TYPE_POPULAR_PERSON -> {

                                FirebaseDatabaseUtils.getArticleContentDatabaseReference().child(
                                    articleMeta?.id ?: ""
                                ).child(FirebaseReferenceConstants.content1Key)
                                    .child(FirebaseReferenceConstants.name)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                val saidBy = p0.getValue(String::class.java)
                                                if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_TEXT) {

                                                    val popularQuotesListAdapter =
                                                        PopularQuotesListAdapter(
                                                            (activity as Home).fetchTextQuotesRecyclerOptionsForPopularPerson(
                                                                saidBy ?: ""
                                                            ),
                                                            activity,
                                                            false,
                                                            supportFragmentManager
                                                        )

                                                    holder.itemView.rv_layout_articles_overview.apply {
                                                        isNestedScrollingEnabled = false
                                                        adapter = popularQuotesListAdapter
                                                        layoutManager = StaggeredGridLayoutManager(
                                                            2,
                                                            LinearLayoutManager.VERTICAL
                                                        )
                                                    }

                                                } else if (articleMeta?.mode == FirebaseReferenceConstants.ARTICLE_MODE_CARD) {

                                                    val popularQuoteCardsListAdapter =
                                                        PopularQuoteCardsListAdapter(
                                                            (activity as Home).fetchQuoteCardRecyclerOptionsFromPopularPerson(
                                                                saidBy ?: ""
                                                            ),
                                                            activity,
                                                            false,
                                                            supportFragmentManager
                                                        )

                                                    holder.itemView.rv_layout_articles_overview.apply {
                                                        isNestedScrollingEnabled = false
                                                        adapter = popularQuoteCardsListAdapter
                                                        layoutManager = LinearLayoutManager(
                                                            c,
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false
                                                        )
                                                    }

                                                }
                                            }
                                        }

                                    })

                            }

                        }
                    }
                }
            })
    }

    override fun onLoadingStateChanged(state: LoadingState) {

    }
}

