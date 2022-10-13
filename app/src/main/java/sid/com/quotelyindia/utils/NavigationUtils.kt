package sid.com.quotelyindia.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.bottomsheets.QuotesBottomSheet
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.contents.*
import sid.com.quotelyindia.models.data.MoviesMeta
import sid.com.quotelyindia.models.data.QuotesMeta

object NavigationUtils {


    fun goToExplore(c: Context, exploreTo: String, isQuoteCard: Boolean) {
        if (isQuoteCard) {
            FirebaseAnalyticsUtils.logTagsClicks(
                exploreTo,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeCard
            )
        } else {
            FirebaseAnalyticsUtils.logTagsClicks(
                exploreTo,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeText
            )
        }
        val intent = Intent(c, Explore::class.java)
        intent.putExtra(PassingDataConstants.PassExploreTo, exploreTo)
        intent.putExtra(PassingDataConstants.PassQuoteCardInformation, isQuoteCard)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        c.startActivity(intent)
    }

    fun goToMovie(c: Context, model: MoviesMeta, isQuoteCard: Boolean) {
        if (isQuoteCard) {
            FirebaseAnalyticsUtils.logMovieClicks(
                model.movie,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeCard
            )
        } else {
            FirebaseAnalyticsUtils.logMovieClicks(
                model.movie,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeText
            )
        }
        val intent = Intent(c, Movie::class.java)
        intent.putExtra(PassingDataConstants.PassMovieName, model.movie)
        intent.putExtra(PassingDataConstants.PassMovieId, model.movieid)
        intent.putExtra(PassingDataConstants.PassMovieYear, model.year)
        intent.putExtra(PassingDataConstants.PassQuoteCardInformation, isQuoteCard)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        c.startActivity(intent)
    }

    fun goToMovies(c: Context, isQuoteCard: Boolean) {
        val intent = Intent(c, Movies::class.java)
        intent.putExtra(PassingDataConstants.PassQuoteCardInformation, isQuoteCard)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        c.startActivity(intent)
    }


    fun goToMoviesSearch(c: Context, isQuoteCard: Boolean) {
        val intent = Intent(c, MoviesSearch::class.java)
        intent.putExtra(PassingDataConstants.PassQuoteCardInformation, isQuoteCard)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        c.startActivity(intent)
    }

    fun goToTags(c: Context, tag: String, isQuoteCard: Boolean) {
        if (isQuoteCard) {
            FirebaseAnalyticsUtils.logTagsClicks(
                tag,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeCard
            )
        } else {
            FirebaseAnalyticsUtils.logTagsClicks(
                tag,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeText
            )
        }
        val intent = Intent(c, Tags::class.java)
        intent.putExtra(PassingDataConstants.PassExploreTo, tag)
        intent.putExtra(PassingDataConstants.PassQuoteCardInformation, isQuoteCard)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        c.startActivity(intent)
    }

    fun AddToMovie(activity: Activity, context: Context, model: MoviesMeta, isQuoteCard: Boolean) {
        AdsUtils.displayInterstitialAd(context, activity) {
            goToMovie(context, model, isQuoteCard)
        }
    }

    fun showQuotesBottomSheetFragment(
        supportFragmentManager: FragmentManager,
        progressDialog: ProgressDialog,
        quotesMeta: QuotesMeta,
        isQuoteCard: Boolean
    ) {
        val quotesBottomSheet = QuotesBottomSheet(progressDialog)
        val dataToPass: ArrayList<String> = ArrayList()
        dataToPass.add(quotesMeta.quotes)
        dataToPass.add(quotesMeta.movie)
        dataToPass.add(quotesMeta.year.toString())
        dataToPass.add(quotesMeta.saidBy)
        dataToPass.add(quotesMeta.tag[0])
        dataToPass.add(quotesMeta.quoteId)
        dataToPass.add(isQuoteCard.toString())
        val args = Bundle()
        args.putStringArrayList(PassingDataConstants.PassQuotesInformation, dataToPass)
        quotesBottomSheet.arguments = args
        quotesBottomSheet.show(supportFragmentManager, quotesBottomSheet.tag)
    }

}