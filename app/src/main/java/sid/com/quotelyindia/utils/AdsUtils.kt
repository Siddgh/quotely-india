package sid.com.quotelyindia.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.billingclient.api.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import sid.com.quotelyindia.ProgressDialog
import sid.com.quotelyindia.R
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.constants.MessagesConstants


object AdsUtils {

    private fun initAds(context: Context) {
        MobileAds.initialize(context.applicationContext) {}
    }

    fun displayBannerAd(context: Context, adView: AdView) {
        showAdsOrNot(context) {
            if (it) {
                // IS A PREMIUM USER
                adView.visibility = View.GONE
            } else {
                val from = context.javaClass.name
                adView.visibility = View.VISIBLE
                initAds(context = context)
                adView.loadAd(AdRequest.Builder().build())
                adView.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        FirebaseAnalyticsUtils.logAdsDisplay(
                            FirebaseReferenceConstants.FirebaseAnalyticsAdsBanner
                        )
                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        FirebaseCrashlytics.getInstance()
                            .log("Failed to load Banner Ads --> $p0 in $from")
                    }
                }
            }
        }
    }

    fun displayInterstitialAd(context: Context, activity: Activity, onSuccess: () -> Unit) {
        val dialog = ProgressDialog(activity)
        showAdsOrNot(context) {
            if (it) {
                onSuccess()
            } else {
                dialog.startLoadingDialog()
                val from = context.javaClass.name
                initAds(context)
                val mInterstitialAd = InterstitialAd(context)
                mInterstitialAd.adUnitId = context.getString(R.string.admob_interstitial_id)
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        if (mInterstitialAd.isLoaded) {
                            dialog.dismissDialog()
                            FirebaseAnalyticsUtils.logAdsDisplay(
                                FirebaseReferenceConstants.FirebaseAnalyticsAdsInterstitial
                            )

                            mInterstitialAd.show()

                        }
                    }

                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        Log.d("DEBUGGING", "Ad Failed to load with --> $p0")
                        if (p0 == 3) {
                            dialog.dismissDialog()
                            onSuccess()
                        } else {
                            if (!ViewSetupUtils.isConnected()) {
                                FirebaseCrashlytics.getInstance()
                                    .log("Failed to load Interstitial Ads due to Network Connectivity Issue")
                                dialog.dismissDialog()
                                Toast.makeText(
                                    context,
                                    MessagesConstants.NoInternetConnection,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                FirebaseCrashlytics.getInstance()
                                    .log("Failed to load Interstitial Ads --> $p0 in $from")
                            }
                        }
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        onSuccess()
                    }
                }

            }
        }
    }

    fun displayRewardedAd(
        context: Context,
        activity: Activity,
        dialog: ProgressDialog,
        onSuccess: () -> Unit
    ) {
        dialog.startLoadingDialog()
        val from = context.javaClass.name
        val rewardedAd = RewardedAd(
            context,
            context.getString(R.string.admob_reward_id)
        )

        val adCallback = object : RewardedAdCallback() {
            override fun onUserEarnedReward(p0: RewardItem) {
                onSuccess()
            }

            override fun onRewardedAdOpened() {
                super.onRewardedAdOpened()
                FirebaseAnalyticsUtils.logAdsDisplay(
                    FirebaseReferenceConstants.FirebaseAnalyticsAdsReward
                )
            }

            override fun onRewardedAdClosed() {
                super.onRewardedAdClosed()
                FirebaseCrashlytics.getInstance()
                    .log("Reward Ad displayed but Closed by the User")
            }

            override fun onRewardedAdFailedToShow(p0: Int) {
                super.onRewardedAdFailedToShow(p0)
                FirebaseCrashlytics.getInstance()
                    .log("Failed to show Reward Ads --> $p0 in $from")
            }
        }

        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                dialog.dismissDialog()
                rewardedAd.show(activity, adCallback)
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                FirebaseCrashlytics.getInstance()
                    .log("Failed to load Reward Ads --> $errorCode in $from")

                dialog.dismissDialog()
                if (!ViewSetupUtils.isConnected()) {
                    Toast.makeText(
                        context,
                        MessagesConstants.NoInternetConnection,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }


    fun showAdsOrNot(context: Context, onSuccess: (Boolean) -> Unit) {
        val billingClient =
            BillingClient.newBuilder(context).enablePendingPurchases()
                .setListener { _, _ -> }.build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                FirebaseCrashlytics.getInstance()
                    .log("Billing Service was disconnected while performing Premium/Freemium Checks")
                onSuccess(false)
            }

            override fun onBillingSetupFinished(p0: BillingResult?) {
                if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                    val purchasesResult: Purchase.PurchasesResult =
                        billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                    if (purchasesResult.purchasesList != null) {
                        if (purchasesResult.purchasesList.size > 0) {
                            for (purchase in purchasesResult.purchasesList) {
                                if (purchase != null) {
                                    onSuccess(true)
                                    FirebaseAnalyticsUtils.setAppVersionType(
                                        FirebaseReferenceConstants.FirebaseAnalyticsPremiumUser
                                    )
                                } else {
                                    onSuccess(false)
                                    FirebaseAnalyticsUtils.setAppVersionType(
                                        FirebaseReferenceConstants.FirebaseAnalyticsFreemiumUser
                                    )
                                }
                            }
                        } else {
                            onSuccess(false)
                            FirebaseAnalyticsUtils.setAppVersionType(FirebaseReferenceConstants.FirebaseAnalyticsFreemiumUser)
                        }
                    } else {
                        onSuccess(false)
                    }
                } else {
                    onSuccess(false)
                    FirebaseAnalyticsUtils.setAppVersionType(FirebaseReferenceConstants.FirebaseAnalyticsFreemiumUser)
                }
            }

        })
    }

}