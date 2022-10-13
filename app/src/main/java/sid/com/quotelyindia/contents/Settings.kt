package sid.com.quotelyindia.contents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.merge_header_list_layout.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.models.groupiemodels.SettingsGroupieModel

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        include_settings.tv_header.text = getString(R.string.settings_header)
        include_settings.tv_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32F)
        val items = mutableListOf<SettingsGroupieModel>()
        for (title in resources.getStringArray(R.array.settings_titles)) {
            items.add(SettingsGroupieModel(this, title))
        }
        val section = Section(items)
        include_settings.rv_list.apply {
            layoutManager =
                LinearLayoutManager(
                    baseContext,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                add(section)
            }
            isNestedScrollingEnabled = false
        }
    }
}
