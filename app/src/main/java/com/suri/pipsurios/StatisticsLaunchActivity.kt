package com.suri.pipsurios

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/** Entry point used by the official SuriOS watchface STATUS button. */
class StatisticsLaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MainActivity::class.java).putExtra(
                MainActivity.EXTRA_START_DESTINATION,
                MainActivity.START_DESTINATION_STATISTICS
            )
        )
        finish()
    }
}
