package com.happycola233.bilitools.ui

import android.content.Intent
import android.net.Uri
import com.happycola233.bilitools.platform.supportedPlatformLinks

internal object ExternalDownloadContract {
    const val EXTRA_URL = "com.happycola233.bilitools.extra.EXTERNAL_URL"
}

private val HTTP_URL_REGEX = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)

internal fun Intent.extractExternalDownloadUrl(): String? {
    val raw = when (action) {
        Intent.ACTION_VIEW -> dataString
        Intent.ACTION_SEND -> {
            getStringExtra(Intent.EXTRA_TEXT)
                ?: clipData
                    ?.takeIf { it.itemCount > 0 }
                    ?.getItemAt(0)
                    ?.text
                    ?.toString()
        }
        else -> null
    }
    if (raw.isNullOrBlank()) return null

    // 与首页粘贴使用同一个路由，跳过分享文案中的无关网址。
    supportedPlatformLinks.resolve(raw)?.let { return it.url.toString() }
    val candidate = HTTP_URL_REGEX.find(raw)?.value ?: raw
    return normalizeHttpUrl(candidate)
}

internal fun normalizeHttpUrl(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val trimmed = raw
        .trim()
        .trimEnd(
            '.',
            ',',
            ';',
            ':',
            '!',
            '?',
            ')',
            ']',
            '}',
            '>',
            '。',
            '，',
            '；',
            '：',
            '！',
            '？',
            '）',
            '】',
            '》',
        )
    if (trimmed.isBlank()) return null

    val uri = runCatching { Uri.parse(trimmed) }.getOrNull() ?: return null
    val scheme = uri.scheme?.lowercase()
    val host = uri.host
    if (scheme != "http" && scheme != "https") return null
    if (host.isNullOrBlank()) return null

    return trimmed
}
