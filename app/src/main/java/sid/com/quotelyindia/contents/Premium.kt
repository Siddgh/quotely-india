package sid.com.quotelyindia.contents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_premium.*
import sid.com.quotelyindia.R
import sid.com.quotelyindia.constants.FirebaseReferenceConstants
import sid.com.quotelyindia.models.data.PaidMeta
import sid.com.quotelyindia.utils.FirebaseAnalyticsUtils
import sid.com.quotelyindia.utils.FirebaseDatabaseUtils


class Premium : AppCompatActivity(), PurchasesUpdatedListener {

    lateinit var billingClient: BillingClient
    lateinit var skuList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)
        mcv_buy_premium.visibility = View.GONE
        val product = getString(R.string.quotely_india_premium_in_app)
        mcv_buy_premium.visibility = View.GONE
        skuList = mutableListOf(product)
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient =
            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                FirebaseCrashlytics.getInstance()
                    .log("Billing Service was disconnected while performing Premium/Freemium Checks")
            }

            override fun onBillingSetupFinished(p0: BillingResult?) {
                if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                    loadAllSkus()
                }
            }

        })
    }

    private fun loadAllSkus() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams.newBuilder().setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP).build()

            billingClient.querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(
                    p0: BillingResult?,
                    p1: MutableList<SkuDetails>?
                ) {
                    if (p0?.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {
                        for (skuDetails in p1) {
                            mcv_buy_premium.visibility = View.VISIBLE
                            mcv_buy_premium.setOnClickListener {
                                val billingFlowParams =
                                    BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
                                billingClient.launchBillingFlow(this@Premium, billingFlowParams)
                            }
                        }
                    }
                }

            })
        }
    }


    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        when {
            p0?.responseCode == BillingClient.BillingResponseCode.OK && p1 != null -> {
                for (purchase in p1) {
                    handlePurchase(purchase)
                }
            }
            p0?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED && p1 != null -> {
                FirebaseCrashlytics.getInstance()
                    .log("In App Purchase Checks --> Item Already Purchased by User")
            }
            p0?.responseCode == BillingClient.BillingResponseCode.USER_CANCELED && p1 != null -> {
                FirebaseCrashlytics.getInstance()
                    .log("In App Purchase Checks --> User Cancelled Purchase")
            }
            else -> {
                FirebaseCrashlytics.getInstance()
                    .log("In App Purchase Checks --> Something went wrong during the purchase --> ${p0?.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        FirebaseCrashlytics.getInstance()
            .log("In App Purchase Checks --> Purchase Successful --> ${purchase.orderId}")

        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(
                acknowledgePurchaseParams
            ) { p0 ->
                FirebaseCrashlytics.getInstance()
                    .log("In App Purchase Checks --> Acknowledgement Done ${p0?.debugMessage}")

                FirebaseDatabaseUtils.writePremiumPurchaseInformationToFirebase(
                    PaidMeta(
                        true,
                        purchase.orderId,
                        FirebaseAuth.getInstance().uid ?: FirebaseReferenceConstants.anon
                    )
                )

                FirebaseAnalyticsUtils.logPremiumPurchase()
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

            }
        }
    }

}
