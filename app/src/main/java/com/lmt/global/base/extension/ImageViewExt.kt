package com.lmt.global.base.extension

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lmt.global.base.helper.firebase.RemoteConfigManagement
import com.google.common.net.HttpHeaders
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

fun ImageView.loadImageWithGlide(thumb: Any?, @DrawableRes error: Int? = null) {
    Glide.with(this)
        .load(thumb)
        .error(error)
        .into(this)
}

fun ImageView.loadGlide(
    thumb: Any?,
    onReady: ((Any?) -> Unit)? = null,
    onFailed: (() -> Unit)? = null
) {
    Glide.with(this).load(thumb).listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            onFailed?.invoke()
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            onReady?.invoke(resource)
            return false
        }
    }).into(this)
}

//fun String.toGlideUrl(): GlideUrl {
//    return GlideUrl(
//        "${BuildConfig.BASE_URL}${this}".trim().replace("//", "/"), LazyHeaders.Builder()
//            .addHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3.raw")
//            .addHeader(HttpHeaders.AUTHORIZATION, "token ${BuildConfig.TOKEN}")
//            .build()
//    )
//}

fun BlurView.setEnable(enable: Boolean, viewGroup: ViewGroup) {
    if (!enable) {
        this.beGone()
        return
    }
    val blurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        RenderEffectBlur()
    } else {
        @Suppress("DEPRECATION")
        RenderScriptBlur(context)
    }
    this.setupWith(viewGroup, blurAlgorithm).setBlurRadius(1f)
}

@OptIn(ObsoleteCoroutinesApi::class)
fun BlurView.delayTimeTicker(
    lifecycleOwner: LifecycleOwner,
    delayMillis: Long,
    onTick: (Long) -> Boolean,
    onFinished: () -> Unit
) {
    var remaining = 0L
    var job: Job? = null
    try {
        job = lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            ticker(200L).consumeAsFlow().collect {
                if (!isActive) return@collect
                if (remaining >= delayMillis) {
                    post(onFinished)
                    beGone()
                    job?.takeIf { it.isActive }?.let {
                        job?.cancel()
                        job = null
                    }
                    return@collect
                }
                remaining += 200L
                val isCancel = onTick(remaining)
                if (isCancel) {
                    beGone()
                    job?.takeIf { it.isActive }?.let {
                        job?.cancel()
                        job = null
                    }
                    return@collect
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        onFinished.invoke()
        job?.takeIf { it.isActive }?.let {
            job?.cancel()
            job = null
        }
    }
}

/**
 * Tính toán kích thước phù hợp cho View để giới hạn bitmap size
 */
private fun View.getOptimalSize(): Pair<Int, Int> {
    val maxWidth = 4096
    val maxHeight = 4096

    // Lấy kích thước từ layout params nếu có
    val layoutWidth = layoutParams?.width ?: 0
    val layoutHeight = layoutParams?.height ?: 0

    // Sử dụng kích thước view nếu đã được measure
    val viewWidth = if (width > 0) width else layoutWidth
    val viewHeight = if (height > 0) height else layoutHeight

    // Nếu không có kích thước cụ thể, sử dụng screen size
    val targetWidth = when {
        viewWidth > 0 && viewWidth != ViewGroup.LayoutParams.MATCH_PARENT && viewWidth != ViewGroup.LayoutParams.WRAP_CONTENT ->
            viewWidth.coerceAtMost(maxWidth)

        else -> widthScreen().coerceAtMost(maxWidth)
    }

    val targetHeight = when {
        viewHeight > 0 && viewHeight != ViewGroup.LayoutParams.MATCH_PARENT && viewHeight != ViewGroup.LayoutParams.WRAP_CONTENT ->
            viewHeight.coerceAtMost(maxHeight)

        else -> heightScreen().coerceAtMost(maxHeight)
    }

    return Pair(targetWidth, targetHeight)
}


fun String.toGlideUrl(): GlideUrl {
    val remoteConfig = GlobalContext.get().get<RemoteConfigManagement>()
    val fullUrl = if (this.startsWith("http://") || this.startsWith("https://")) {
        this
    } else {
        "${remoteConfig.baseGithubURL}${this}".trim().replace("//", "/")
    }

    return GlideUrl(
        fullUrl,
        LazyHeaders.Builder()
            .addHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3.raw")
            .addHeader(HttpHeaders.AUTHORIZATION, "token ${remoteConfig.baseGithubToken}")
            .build()
    )
}


fun ImageView.loadImageFromGitHub(pathOrUrl: String, @DrawableRes error: Int? = null) {

    try {
        val (targetWidth, targetHeight) = getOptimalSize()

        val glideUrl = pathOrUrl.toGlideUrl()

        Glide.with(this)
            .load(glideUrl)
            .override(targetWidth, targetHeight)
            .error(error)
            .into(this)


    } catch (e: Exception) {

        try {
            val glideUrl = pathOrUrl.toGlideUrl()

            Glide.with(this)
                .load(glideUrl)
                .error(error)
                .listener(object :
                    RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(this)

        } catch (e2: Exception) {
            error?.let {
                setImageResource(it)
            }
        }
    }
}


fun ImageView.loadImageFromGitHubNotResize(pathOrUrl: String, @DrawableRes error: Int? = null) {
    try {
        val glideUrl = pathOrUrl.toGlideUrl()
        Glide.with(context)
            .asDrawable()
//        .override(90, 160)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(CenterCrop())
            .skipMemoryCache(false)
            .error(error)
            .load(glideUrl)
            .into(this)
    } catch (e: Exception) {
        error?.let { setImageResource(it) }
    }
}
