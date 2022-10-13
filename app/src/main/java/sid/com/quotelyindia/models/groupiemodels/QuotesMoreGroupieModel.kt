package sid.com.quotelyindia.models.groupiemodels

import android.app.Activity
import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_tags_chip.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.NavigationUtils
import sid.com.quotelyindia.utils.StringUtils.movieNameAsId

class QuotesMoreGroupieModel(
    private val toDisplay: String,
    private val tags: String,
    private val movie: String,
    private val saidBy: String,
    private val activity: Activity,
    private val year: String,
    private val isQuoteCard: Boolean
) : Item() {

    override fun getLayout() = R.layout.layout_tags_chip

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            chip_layout_tags_chip.text = toDisplay
            chip_layout_tags_chip.setOnClickListener {
                when (position) {
                    0 -> {
                        val moviesMeta =
                            MoviesMeta(
                                year.toInt(),
                                movie,
                                movie.movieNameAsId()
                            )
                        AdsUtils.displayInterstitialAd(activity.baseContext, activity) {
                            NavigationUtils.goToMovie(activity.baseContext, moviesMeta, isQuoteCard)
                        }
                    }
                    1 -> {
                        NavigationUtils.goToExplore(activity.baseContext, tags, isQuoteCard)
                    }
                }
            }

        }
    }

}