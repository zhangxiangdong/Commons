package com.simplemobiletools.commons.samples.activities

import android.os.Bundle
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.samples.R

class SettingsActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(
        R.mipmap.commons_launcher
    )

    override fun getAppLauncherName() = getString(R.string.smtco_app_name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

}
