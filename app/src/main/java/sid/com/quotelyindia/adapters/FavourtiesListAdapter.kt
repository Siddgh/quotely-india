package sid.com.quotelyindia.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_quote_card.view.*
import kotlinx.android.synthetic.main.layout_quote_list_card.view.*
import sid.com.quotelyindia.GlideApp
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.models.data.QuoteIdModel
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.utils.*
import sid.com.quotelyindia.utils.StringUtils.movieNameAsId
import sid.com.quotelyindia.utils.StringUtils.quote

class FavourtiesListAdapter(
    options: DatabasePagingOptions<QuoteIdModel>,
    private val activity: Activity,
    private val supportFragmentManager: FragmentManager,
    private val isQuoteCard: Boolean
) :
    FirebaseRecyclerPagingAdapter<QuoteIdModel, FavourtiesListAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (!isQuoteCard) ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_quote_list_card,
                parent,
                false
            )
        ) else ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_quote_card,
                parent,
                false
            )
        )
    }

    override fun onLoadingStateChanged(state: LoadingState) {

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, model: QuoteIdModel) {
        FirestoreUtils.getLikedQuotesFirestoreReference(isQuoteCard).document(model.quoteId).get()
            .addOnSuccessListener {

                if (!isQuoteCard) {
                    val quotesMeta = it.toObject(QuotesMeta::class.java)
                    viewHolder.itemView.apply {

                        tv_quote_list_card_quote.text = quotesMeta?.quotes?.quote()
                        tv_quote_list_likes.text = StringUtils.displayLikes(quotesMeta?.likes ?: 0)
                        tv_quote_list_card_category.text = quotesMeta?.tag?.get(0)

                        tv_quote_list_movie.text = quotesMeta?.movie


                        FirebaseDatabaseUtils.userDatabaseReference.child(
                            FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
                        ).child(
                            FirebaseReferenceConstants.likedQuotes
                        ).child(quotesMeta?.quoteId ?: FirebaseReferenceConstants.anon)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.exists()) {
                                        iv_quotes_list_likes.setImageResource(R.drawable.ic_fav_filled)
                                    } else {
                                        iv_quotes_list_likes.setImageResource(R.drawable.ic_fav)
                                    }

                                }

                            })

                        cv_quote_list_card.setOnClickListener {
                            val progressDialog = ProgressDialog(activity)
                            progressDialog.startLoadingDialog()
                            NavigationUtils.showQuotesBottomSheetFragment(
                                supportFragmentManager,
                                progressDialog,
                                quotesMeta!!,
                                isQuoteCard
                            )
                        }


                    }
                } else {
                    val quotesCardMeta = it.toObject(QuotesCardMeta::class.java)

                    viewHolder.itemView.apply {
                        GlideApp.with(context).load(
                            FirebaseStorageUtils.getQuoteCardsThumbnailsReferenceById(
                                quotesCardMeta?.quoteId ?: ""
                            )
                        ).into(iv_layout_quote_card)

                        tv_layout_quote_card_like.text =
                            StringUtils.displayLikes(quotesCardMeta?.likes ?: 0)

                        setOnClickListener {
                            FirebaseDatabaseUtils.moviesQuoteCardsDatabaseReference.child(
                                quotesCardMeta?.movie?.movieNameAsId() ?: ""
                            )
                                .child(FirebaseReferenceConstants.yearFirestoreKey)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()) {
                                            val quotesMeta =
                                                QuotesMeta(
                                                    movie = quotesCardMeta?.movie ?: "",
                                                    quoteId = quotesCardMeta?.quoteId ?: "",
                                                    quotes = "",
                                                    tag = quotesCardMeta?.inframe
                                                        ?: mutableListOf(),
                                                    year = p0.getValue(Int::class.java) ?: 0,
                                                    saidBy = quotesCardMeta?.tag ?: "",
                                                    likes = quotesCardMeta?.likes ?: 0
                                                )

                                            iv_layout_quote_card.setOnClickListener {
                                                val progressDialog = ProgressDialog(activity)
                                                progressDialog.startLoadingDialog()
                                                NavigationUtils.showQuotesBottomSheetFragment(
                                                    supportFragmentManager,
                                                    progressDialog,
                                                    quotesMeta,
                                                    true
                                                )
                                            }

                                        }
                                    }

                                })
                        }
                    }
                }
            }

    }

}