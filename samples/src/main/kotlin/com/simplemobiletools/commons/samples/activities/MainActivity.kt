package com.simplemobiletools.commons.samples.activities

import android.content.Intent
import android.os.Bundle
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.appLaunched
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.FAQItem
import com.simplemobiletools.commons.samples.BuildConfig
import com.simplemobiletools.commons.samples.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseSimpleActivity() {
    override fun getAppLauncherName() = getString(R.string.smtco_app_name)

    override fun getAppIconIDs(): ArrayList<Int> {
        val ids = ArrayList<Int>()
        ids.add(R.mipmap.commons_launcher)
        return ids
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appLaunched(BuildConfig.APPLICATION_ID)

        main_color_customization.setOnClickListener {
            startCustomizationActivity()
        }

        about.setOnClickListener {
            val licenses = LICENSE_GLIDE or LICENSE_PATTERN or LICENSE_REPRINT or LICENSE_GESTURE_VIEWS or LICENSE_PDF_VIEWER or LICENSE_AUTOFITTEXTVIEW
            val faqItems = arrayListOf(
                FAQItem(R.string.faq_3_title_commons, R.string.faq_3_text_commons),
                FAQItem(R.string.faq_9_title_commons, R.string.faq_9_text_commons)
            )
            startAboutActivity(R.string.smtco_app_name, licenses, BuildConfig.VERSION_NAME, faqItems, true)
        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //startCustomizationActivity()
        //startAboutActivity(R.string.smtco_app_name, 3, "0.2", arrayListOf(FAQItem(R.string.faq_1_title_commons, R.string.faq_1_text_commons)), false)

        /*val letters = arrayListOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q")
        StringsAdapter(this, letters, media_grid, media_refresh_layout) {
        }.apply {
            media_grid.adapter = this
        }

        media_refresh_layout.setOnRefreshListener {
            Handler().postDelayed({
                media_refresh_layout.isRefreshing = false
            }, 1000L)
        }*/
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(main_toolbar)
    }
}
