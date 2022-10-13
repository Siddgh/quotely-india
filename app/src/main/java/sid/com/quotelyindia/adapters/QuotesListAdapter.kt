package sid.com.quotelyindia.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.layout_quote_list_card.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.utils.FirebaseAuthUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils
import sid.com.quotelyindia.utils.NavigationUtils
import sid.com.quotelyindia.utils.StringUtils
import sid.com.quotelyindia.utils.StringUtils.quote


class QuotesListAdapter(
    options: FirestorePagingOptions<QuotesMeta>,
    private val activity: Activity,
    private val isMovieSection: Boolean,
    private val supportFragmentManager: FragmentManager
) :
    FirestorePagingAdapter<QuotesMeta, QuotesListAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_quote_list_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: QuotesMeta) {
        holder.itemView.apply {
            tv_quote_list_card_quote.text = model.quotes.quote()
            tv_quote_list_likes.text = StringUtils.displayLikes(model.likes)
            tv_quote_list_card_category.text = model.tag[0]
            if (isMovieSection) {
                tv_quote_list_movie.visibility = View.GONE
                iv_quote_list_movie.visibility = View.GONE
            } else {
                tv_quote_list_movie.text = model.movie
            }

            FirebaseDatabaseUtils.userDatabaseReference.child(
                FirebaseAuthUtils.getUserMeta().uid ?: FirebaseReferenceConstants.anon
            ).child(
                FirebaseReferenceConstants.likedQuotes
            ).child(model.quoteId)
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
                    model,
                    false
                )
            }
        }
    }


}