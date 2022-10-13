package sid.com.quotelyindia.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.layout_movie_card.view.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.utils.FirebaseStorageUtils
import sid.com.quotelyindia.utils.NavigationUtils

class MoviesSearchListAdapter(
    options: FirebaseRecyclerOptions<MoviesMeta>,
    private val c: Context,
    private val isQuoteCard: Boolean,
    private val activity: Activity
) :
    FirebaseRecyclerAdapter<MoviesMeta, MoviesSearchListAdapter.ViewHolder>(options) {

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_movie_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: MoviesMeta) {

        holder.itemView.apply {
            Glide.with(c).load(FirebaseStorageUtils.getMoviePosterReferenceById(model.movieid))
                .centerCrop()
                .into(iv_layout_movie_poster)
            setOnClickListener {
                NavigationUtils.AddToMovie(activity, context, model, isQuoteCard)
            }
            tv_layout_movie_title.text = model.movie
        }

    }
}