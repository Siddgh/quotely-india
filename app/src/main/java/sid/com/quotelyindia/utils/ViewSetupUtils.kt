package sid.com.quotelyindia.utils

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import sid.com.quotelyindia.models.data.QuotesMeta
import sid.com.quotelyindia.models.groupiemodels.TagsGroupieModel
import java.io.IOException


object ViewSetupUtils {

    fun setUpTagsRecyclerView(
        recyclerView: RecyclerView,
        quotesMeta: QuotesMeta,
        activity: Activity
    ) {
        val items = mutableListOf<Item>()
        quotesMeta.tag.forEach {
            items.add(
                TagsGroupieModel(
                    it,
                    activity
                )
            )
        }

        val section = Section(items)
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(
                    activity.baseContext,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                add(section)
            }
            isNestedScrollingEnabled = false
        }
    }

    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }

}


