package com.suri.surioswallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class WalletBridgeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val walletIntent = Intent("com.google.commerce.tapandpay.wear.cardlist.WALLET_LAUNCHER")
            .setPackage("com.google.android.apps.walletnfcrel")

        runCatching { startActivity(walletIntent) }
        finish()
    }
}
