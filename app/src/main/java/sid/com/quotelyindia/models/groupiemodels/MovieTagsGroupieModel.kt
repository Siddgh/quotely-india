package sid.com.quotelyindia.models.groupiemodels

import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_tags_chip.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.contents.Movie
import java.util.*

class MovieTagsGroupieModel(
    private val tag: String,
    private val movie: String,
    private val context: Context,
    private val isQuoteCard: Boolean
) : Item() {
    override fun getLayout() = R.layout.layout_tags_chip

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val tag = tag
        val toDisplay = tag.toLowerCase(Locale.ENGLISH)
        viewHolder.itemView.apply {
            chip_layout_tags_chip.text = toDisplay
            chip_layout_tags_chip.setOnClickListener {
                val activity = context as Movie
                activity.setUpRecyclerView(tag, movie, isQuoteCard)
            }

        }
    }
}