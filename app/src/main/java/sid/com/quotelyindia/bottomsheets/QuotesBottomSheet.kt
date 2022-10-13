package sid.com.quotelyindia.bottomsheets

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.bottom_sheet_text_quotes.*
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.MessagesConstants
import sid.com.quotelyindia.constants.PassingDataConstants
import sid.com.quotelyindia.constants.PermissionsConstants
import sid.com.quotelyindia.contents.Premium
import sid.com.quotelyindia.models.groupiemodels.QuotesMoreGroupieModel
import sid.com.quotelyindia.utils.*
import sid.com.quotelyindia.utils.StringUtils.quote

class QuotesBottomSheet(val progressDialog: ProgressDialog) :
    BottomSheetDialogFragment() {

    lateinit var quotesData: ArrayList<String>
    lateinit var quote: String
    lateinit var movie: String
    lateinit var year: String
    lateinit var saidby: String
    lateinit var tags: String
    lateinit var quoteId: String
    private var isLiked: Boolean = false
    private var isQuoteCard: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_text_quotes, container, false)
        quotesData =
            arguments?.getStringArrayList(PassingDataConstants.PassQuotesInformation) ?: ArrayList()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog.dismissDialog()

        setUpViews()
        displayBannerAds()

        if (isQuoteCard) {
            FirebaseAnalyticsUtils.logQuoteViewedClicks(
                movie,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeCard
            )
        } else {
            FirebaseAnalyticsUtils.logQuoteViewedClicks(
                movie,
                FirebaseReferenceConstants.FirebaseAnalyticsLogViewTypeText
            )
        }

        FirebaseDatabaseUtils.fetchUserLikedQuotesReferenceQuery(isQuoteCard).child(quoteId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    isLiked = p0.exists()

                    if (isLiked) {

                        fab_bottom_sheet_text_quotes_like.setImageResource(R.drawable.ic_fav_filled)

                    } else {
                        fab_bottom_sheet_text_quotes_like.setImageResource(R.drawable.ic_fav)
                    }

                    fab_bottom_sheet_text_quotes_like.setOnClickListener {
                        if (!isLiked) {

                            FirebaseDatabaseUtils.writeUserLikedQuoteToFirebaseDatabase(
                                quoteId,
                                isQuoteCard
                            )

                            FirestoreUtils.addALike(
                                quoteId = quoteId,
                                isQuoteCard = isQuoteCard
                            )
                            FirebaseAnalyticsUtils.logQuoteAction(FirebaseReferenceConstants.FirebaseAnalyticsActionOnQuoteLike)
                            fab_bottom_sheet_text_quotes_like.setImageResource(R.drawable.ic_fav_filled)
                            isLiked = true

                        } else {

                            FirebaseDatabaseUtils.removeUserLikedQuoteFromFirebaseDatabase(
                                quoteId,
                                isQuoteCard
                            )

                            FirestoreUtils.removeALike(
                                quoteId = quoteId,
                                isQuoteCard = isQuoteCard
                            )
                            FirebaseAnalyticsUtils.logQuoteAction(FirebaseReferenceConstants.FirebaseAnalyticsActionOnQuoteUnLike)
                            fab_bottom_sheet_text_quotes_like.setImageResource(R.drawable.ic_fav)
                            isLiked = false

                        }

                    }

                }

            })

        fab_bottom_sheet_text_quotes_copy.setOnClickListener {
            copyToClipboard()
        }

        fab_bottom_sheet_text_quotes_share.setOnClickListener {
            startShare()
        }

        setUpMoreOptions()

    }

    private fun setUpMoreOptions() {
        val items = mutableListOf<Item>()

        items.add(
            QuotesMoreGroupieModel(
                StringUtils.quotesFromHeader(movie),
                tags,
                movie,
                saidby,
                requireActivity(),
                year,
                isQuoteCard
            )
        )

        if (!isQuoteCard) {
            items.add(
                QuotesMoreGroupieModel(
                    StringUtils.bottomSheetMoreOptionsTitlesTags(tags),
                    tags,
                    movie,
                    saidby,
                    requireActivity(),
                    year,
                    isQuoteCard
                )
            )
        } else {
            items.add(
                QuotesMoreGroupieModel(
                    StringUtils.bottomSheetMoreOptionsTitlesTags(saidby),
                    saidby,
                    movie,
                    tags,
                    requireActivity(),
                    year,
                    isQuoteCard
                )
            )
        }


        val section = Section(items)
        rv_bottom_sheet_text_quotes_more.apply {
            layoutManager =
                LinearLayoutManager(
                    activity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            adapter = GroupAdapter<GroupieViewHolder>().apply {
                add(section)
            }
            isNestedScrollingEnabled = false
        }
    }

    private fun setUpViews() {
        quote = quotesData[0].quote()
        movie = quotesData[1]
        year = quotesData[2]
        saidby = quotesData[3]
        tags = quotesData[4]
        quoteId = quotesData[5]
        isQuoteCard = quotesData[6].toBoolean()

        if (!isQuoteCard) {

            iv_bottom_sheet_quote_card.visibility = View.GONE
            tv_bottom_sheet_text_quote.text = quote
            tv_more_movie_info_bottom_sheet_text_quotes_tags.text = tags

        } else {

            tv_bottom_sheet_text_quote.visibility = View.GONE
            fab_bottom_sheet_text_quotes_copy.setImageResource(R.drawable.ic_download)
            tv_more_movie_info_bottom_sheet_text_quotes_tags.text = saidby
            fab_bottom_sheet_text_quotes_share.visibility = View.GONE
            Glide.with(requireContext())
                .load(FirebaseStorageUtils.getQuoteCardsFullSizeReferenceById(quoteId))
                .fitCenter()
                .into(iv_bottom_sheet_quote_card)

        }

        tv_movie_info_bottom_sheet_text_quotes.text = movie
        tv_more_movie_info_bottom_sheet_text_quotes_year.text = year


    }

    private fun copyToClipboard() {
        if (!isQuoteCard) {
            val toCopy = "$quote\n\n\uD83C\uDFAC  $movie"
            StorageUtils.copyToClipboard(requireActivity(), toCopy)
            FirebaseAnalyticsUtils.logQuoteAction(FirebaseReferenceConstants.FirebaseAnalyticsActionOnQuoteCopy)
            Toast.makeText(activity, MessagesConstants.clipboardCopySuccess, Toast.LENGTH_SHORT)
                .show()
        } else {
            AdsUtils.showAdsOrNot(requireContext()) {
                if (it) {
                    initDownload()
                } else {
                    if (isAdded) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle(MessagesConstants.rewardAdsTitle)
                        builder.setMessage(MessagesConstants.rewardAdsDescription)
                        builder.setIcon(R.drawable.ic_pro)
                        builder.setPositiveButton(
                            MessagesConstants.rewardWatchAnAd
                        ) { _, _ ->
                            val dialog = ProgressDialog(requireActivity())
                            AdsUtils.displayRewardedAd(
                                requireContext(),
                                requireActivity(),
                                dialog
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    MessagesConstants.downloadAccessGranted,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                initDownload()
                            }
                        }
                        builder.setNegativeButton(
                            MessagesConstants.rewardBuyPremium
                        ) { _, _ ->
                            startActivity(Intent(requireContext(), Premium::class.java))
                        }
                        builder.show()
                    }
                }
            }
        }
    }

    private fun initDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                StorageUtils.downloadQuoteCard(quoteId, movie, requireContext(), requireActivity())
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PermissionsConstants.writePermissionRequestCode
                )
            }
        } else {
            StorageUtils.downloadQuoteCard(quoteId, movie, requireContext(), requireActivity())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsConstants.writePermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StorageUtils.downloadQuoteCard(quoteId, movie, requireContext(), requireActivity())
            } else {
                Toast.makeText(
                    requireContext(),
                    MessagesConstants.PermissionDeniedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startShare() {
        FirebaseAnalyticsUtils.logQuoteAction(FirebaseReferenceConstants.FirebaseAnalyticsActionOnQuoteShare)
        val toShare = "$quote\n\n🎬 $movie"
        StorageUtils.shareStuff(toShare, requireActivity())
    }

    private fun displayBannerAds() {
        AdsUtils.displayBannerAd(requireContext(), banner_bottom_sheet_text_quotes)
    }
}