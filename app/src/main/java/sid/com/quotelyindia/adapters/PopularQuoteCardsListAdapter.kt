package sid.com.quotelyindia.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_quote_card.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.models.data.QuotesCardMeta
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.FirebaseStorageUtils
import sid.com.quotelyindia.utils.NavigationUtils
import sid.com.quotelyindia.utils.StringUtils
import sid.com.quotelyindia.utils.StringUtils.movieNameAsId

class PopularQuoteCardsListAdapter(
    options: FirestoreRecyclerOptions<QuotesCardMeta>,
    private val activity: Activity,
    private val isMovieSection: Boolean,
    private val supportFragmentManager: FragmentManager
) :
    FirestoreRecyclerAdapter<QuotesCardMeta, PopularQuoteCardsListAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_quote_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuotesCardMeta) {
        holder.itemView.apply {
            Glide.with(context)
                .load(FirebaseStorageUtils.getQuoteCardsThumbnailsReferenceById(model.quoteId))
                .centerCrop()
                .into(iv_layout_quote_card)

            tv_layout_quote_card_like.text = StringUtils.displayLikes(model.likes)

            FirebaseDatabaseUtils.moviesQuoteCardsDatabaseReference.child(model.movie.movieNameAsId())
                .child(FirebaseReferenceConstants.yearFirestoreKey)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val quotesMeta =
                                QuotesMeta(
                                    movie = model.movie,
                                    quoteId = model.quoteId,
                                    quotes = "",
                                    tag = model.inframe,
                                    year = p0.getValue(Int::class.java) ?: 0,
                                    saidBy = model.tag,
                                    likes = model.likes
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