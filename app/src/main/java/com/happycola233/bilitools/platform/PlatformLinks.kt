package com.happycola233.bilitools.platform

import androidx.core.util.PatternsCompat
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/** 平台只声明链接归属；此层不持有账号、Cookie 或媒体下载状态。 */
internal interface PlatformLinkAdapter {
    val id: String
    val schemeLessUrl: Regex
    fun accepts(url: HttpUrl): Boolean
}

internal object BilibiliLinks : PlatformLinkAdapter {
    override val id = "bilibili"
    override val schemeLessUrl = Regex(
        "(?<![A-Za-z0-9._:/-])" +
            "(?:(?:[A-Za-z0-9-]+\\.)*bilibili\\.com|b23\\.tv)" +
            "/[A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
        RegexOption.IGNORE_CASE,
    )

    override fun accepts(url: HttpUrl): Boolean =
        url.host == "bilibili.com" || url.host.endsWith(".bilibili.com") || url.host == "b23.tv"
}

internal data class PlatformLink(val platformId: String, val url: HttpUrl)

/** 分享入口与粘贴入口共用的平台链接路由；仅注册已实现的平台。 */
internal class PlatformLinkResolver(private val adapters: List<PlatformLinkAdapter>) {
    fun resolve(raw: String): PlatformLink? {
        val matcher = PatternsCompat.WEB_URL.matcher(raw)
        while (matcher.find()) {
            route(matcher.group())?.let { return it }
        }
        // 中文紧贴无协议域名时，PatternsCompat 可能把中文算入域名。
        return adapters.asSequence()
            .flatMap { it.schemeLessUrl.findAll(raw) }
            .sortedBy { it.range.first }
            .mapNotNull { route(it.value) }
            .firstOrNull()
    }

    private fun route(candidate: String): PlatformLink? {
        val ascii = ASCII_URL_PREFIX.find(candidate)?.value
            ?.trimEnd('.', ',', ';', '!', ')', ']', '}') ?: return null
        val normalized = if (ascii.startsWith("http://", true) || ascii.startsWith("https://", true)) {
            ascii
        } else {
            "https://$ascii"
        }
        val url = normalized.toHttpUrlOrNull() ?: return null
        val adapter = adapters.firstOrNull { it.accepts(url) } ?: return null
        return PlatformLink(adapter.id, url)
    }

    private companion object {
        val ASCII_URL_PREFIX = Regex("^[A-Za-z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+")
    }
}

internal val supportedPlatformLinks = PlatformLinkResolver(listOf(BilibiliLinks))
