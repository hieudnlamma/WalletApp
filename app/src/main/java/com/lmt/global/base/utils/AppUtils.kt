package com.lmt.global.base.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.lmt.global.base.BuildConfig
import com.lmt.global.base.R
import com.lmt.global.base.extension.showToast

object AppUtils {

    internal fun openLink(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal fun buildGithubImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) {
            path
        } else {
            "https://raw.githubusercontent.com/LMT2025/TCGScan_data/main/$path"
        }
    }

    internal fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT, context.getString(R.string.app_name)
            )
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendEmail(context: Context, emails: Array<String>, subject: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, emails)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(intent, "Send email via"))
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("No email app found")
        }
    }
}