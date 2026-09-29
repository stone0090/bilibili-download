package com.happycola233.bilitools.data

import com.happycola233.bilitools.platform.BilibiliLinks
import com.happycola233.bilitools.platform.supportedPlatformLinks
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

internal object MediaInputUrlParser {
    // B 站仓库保留原始 URL 回退，以便统一报告非法输入。
    fun parse(raw: String): HttpUrl? =
        supportedPlatformLinks.resolve(raw)
            ?.takeIf { it.platformId == BilibiliLinks.id }
            ?.url ?: raw.toHttpUrlOrNull()
}

internal fun HttpUrl.isBiliMediaHost(): Boolean = BilibiliLinks.accepts(this)
