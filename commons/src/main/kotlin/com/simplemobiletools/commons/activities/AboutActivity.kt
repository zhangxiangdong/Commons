package com.simplemobiletools.commons.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Build
import android.os.Bundle
import androidx.core.net.toUri
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.dialogs.ConfirmationAdvancedDialog
import com.simplemobiletools.commons.dialogs.RateStarsDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.FAQItem
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseSimpleActivity() {

    companion object {
        private const val EASTER_EGG_TIME_LIMIT = 3000L
        private const val EASTER_EGG_REQUIRED_CLICKS = 7
    }

    private var appName = ""
    private var primaryColor = 0

    private var firstVersionClickTS = 0L
    private var clicksSinceFirstClick = 0

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        appName = intent.getStringExtra(APP_NAME) ?: ""
        val textColor = getProperTextColor()
        primaryColor = getProperPrimaryColor()

        arrayOf(
            about_faq_icon,
            about_rate_us_icon,
            about_invite_icon,
            about_more_apps_icon,
            about_email_icon,
            about_licenses_icon,
            about_version_icon
        ).forEach {
            it.applyColorFilter(textColor)
        }

        arrayOf(about_support, about_help_us, about_social, about_other).forEach {
            it.setTextColor(primaryColor)
        }
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(about_nested_scrollview)
        setupToolbar(about_toolbar, NavigationIcon.Arrow)

        setupFAQ()
        setupEmail()
        setupRateUs()
        setupInvite()
        setupTelegram()
        setupMoreApps()
        setupLicense()
        setupVersion()
    }

    private fun setupFAQ() {
        @Suppress("UNCHECKED_CAST", "DEPRECATION") val faqItems = intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        about_faq_holder.beVisibleIf(faqItems.isNotEmpty())
        about_faq_holder.setOnClickListener {
            Intent(applicationContext, FAQActivity::class.java).apply {
                putExtra(APP_ICON_IDS, getAppIconIDs())
                putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
                putExtra(APP_FAQ, faqItems)
                startActivity(this)
            }
        }
    }

    private fun setupEmail() {
        about_email_holder.setOnClickListener {
            val msg = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}"
            if (intent.getBooleanExtra(SHOW_FAQ_BEFORE_MAIL, false) && !baseConfig.wasBeforeAskingShown) {
                baseConfig.wasBeforeAskingShown = true
                ConfirmationAdvancedDialog(this, msg, 0, R.string.read_faq, R.string.skip) { success ->
                    if (success) {
                        about_faq_holder.performClick()
                    } else {
                        about_email_holder.performClick()
                    }
                }
            } else {
                val appVersion = String.format(getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME)))
                val deviceOS = String.format(getString(R.string.device_os), Build.VERSION.RELEASE)
                val newline = "\n"
                val separator = "------------------------------"
                val body = "$appVersion$newline$deviceOS$newline$separator$newline$newline"

                val address = getString(R.string.my_email)
                val selectorIntent = Intent(ACTION_SENDTO)
                    .setData("mailto:$address".toUri())
                val emailIntent = Intent(ACTION_SEND).apply {
                    putExtra(EXTRA_EMAIL, arrayOf(address))
                    putExtra(EXTRA_SUBJECT, appName)
                    putExtra(EXTRA_TEXT, body)
                    selector = selectorIntent
                }

                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.no_email_client_found)
                } catch (e: Exception) {
                    showErrorToast(e)
                }
            }
        }
    }

    private fun setupRateUs() {
        if (resources.getBoolean(R.bool.hide_google_relations)) {
            about_rate_us_holder.beGone()
        }

        about_rate_us_holder.setOnClickListener {
            if (baseConfig.wasBeforeRateShown) {
                if (baseConfig.wasAppRated) {
                    redirectToRateUs()
                } else {
                    RateStarsDialog(this)
                }
            } else {
                baseConfig.wasBeforeRateShown = true
                val msg = "${getString(R.string.before_rate_read_faq)}\n\n${getString(R.string.make_sure_latest)}"
                ConfirmationAdvancedDialog(this, msg, 0, R.string.read_faq, R.string.skip) { success ->
                    if (success) {
                        about_faq_holder.performClick()
                    } else {
                        about_rate_us_holder.performClick()
                    }
                }
            }
        }
    }

    private fun setupInvite() {
        if (resources.getBoolean(R.bool.hide_google_relations)) {
            about_invite_holder.beGone()
        }

        about_invite_holder.setOnClickListener {
            val text = String.format(getString(R.string.share_text), appName, getStoreUrl())
            Intent().apply {
                action = ACTION_SEND
                putExtra(EXTRA_SUBJECT, appName)
                putExtra(EXTRA_TEXT, text)
                type = "text/plain"
                startActivity(createChooser(this, getString(R.string.invite_via)))
            }
        }
    }

    private fun setupTelegram() {
        about_telegram_holder.setOnClickListener {
            launchViewIntent("https://t.me/SimpleMobileTools")
        }
    }

    private fun setupMoreApps() {
        if (resources.getBoolean(R.bool.hide_google_relations)) {
            about_more_apps_holder.beGone()
        }

        about_more_apps_holder.setOnClickListener {
            launchViewIntent("https://play.google.com/store/apps/dev?id=9070296388022589266")
        }
    }

    private fun setupLicense() {
        about_licenses_holder.setOnClickListener {
            Intent(applicationContext, LicenseActivity::class.java).apply {
                putExtra(APP_ICON_IDS, getAppIconIDs())
                putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
                putExtra(APP_LICENSES, intent.getLongExtra(APP_LICENSES, 0))
                startActivity(this)
            }
        }
    }

    private fun setupVersion() {
        var version = intent.getStringExtra(APP_VERSION_NAME) ?: ""
        if (baseConfig.appId.removeSuffix(".debug").endsWith(".pro")) {
            version += " ${getString(R.string.pro)}"
        }

        val fullVersion = String.format(getString(R.string.version_placeholder, version))
        about_version.text = fullVersion
        about_version_holder.setOnClickListener {
            if (firstVersionClickTS == 0L) {
                firstVersionClickTS = System.currentTimeMillis()
                about_version.postDelayed({
                    firstVersionClickTS = 0L
                    clicksSinceFirstClick = 0
                }, EASTER_EGG_TIME_LIMIT)
            }

            clicksSinceFirstClick++
            if (clicksSinceFirstClick >= EASTER_EGG_REQUIRED_CLICKS) {
                toast(R.string.hello)
                firstVersionClickTS = 0L
                clicksSinceFirstClick = 0
            }
        }
    }

}
