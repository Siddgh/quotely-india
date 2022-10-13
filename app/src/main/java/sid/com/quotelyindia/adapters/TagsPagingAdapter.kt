package sid.com.quotelyindia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.paging.DatabasePagingOptions
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter
import com.firebase.ui.database.paging.LoadingState
import kotlinx.android.synthetic.main.layout_tags_list.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.models.data.TagsListModel
import sid.com.quotelyindia.utils.NavigationUtils

class TagsPagingAdapter(
    options: DatabasePagingOptions<TagsListModel>,
    private val c: Context,
    private val isQuoteCard: Boolean
) :
    FirebaseRecyclerPagingAdapter<TagsListModel, TagsPagingAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_tags_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: TagsListModel) {
        holder.itemView.apply {
            tv_layout_tags_list.text = model.name
        }
        holder.itemView.setOnClickListener {
            NavigationUtils.goToExplore(
                c,
                holder.itemView.tv_layout_tags_list.text.toString(),
                isQuoteCard
            )
        }
    }

    override fun onLoadingStateChanged(state: LoadingState) {

    }
}