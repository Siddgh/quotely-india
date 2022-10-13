package sid.com.quotelyindia.models.groupiemodels

import android.app.Activity
import android.content.Context
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.layout_tags_chip.view.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.utils.AdsUtils
import sid.com.quotelyindia.utils.NavigationUtils

class TagsGroupieModel(private val tag: String, private val activity: Activity) : Item() {
    override fun getLayout() = R.layout.layout_tags_chip

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val toDisplay = tag
        viewHolder.itemView.apply {
            chip_layout_tags_chip.text = toDisplay
            chip_layout_tags_chip.setOnClickListener {
                AdsUtils.displayInterstitialAd(activity.baseContext, activity) {
                    NavigationUtils.goToTags(
                        activity.baseContext,
                        tag = toDisplay,
                        isQuoteCard = false
                    )
                }
            }
        }
    }
}