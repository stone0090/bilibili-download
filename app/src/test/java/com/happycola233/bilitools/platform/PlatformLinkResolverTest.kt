package com.happycola233.bilitools.platform

import okhttp3.HttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlatformLinkResolverTest {
    @Test
    fun resolvesTwentyEpisodeShareText() {
        val result = supportedPlatformLinks.resolve(
            "【【滚石爱情故事】（2016）全20集-哔哩哔哩】 https://b23.tv/bMUQPo3",
        )
        assertEquals("bilibili", result?.platformId)
        assertEquals("https://b23.tv/bMUQPo3", result?.url.toString())
    }

    @Test
    fun skipsUnrelatedLinksAndKeepsSelectedPart() {
        val result = supportedPlatformLinks.resolve(
            "来源 https://example.com/ 视频https://www.bilibili.com/video/BV1WZjp6SEf5?p=20，打开观看",
        )
        assertEquals("20", result?.url?.queryParameter("p"))
        assertEquals("/video/BV1WZjp6SEf5", result?.url?.encodedPath)
    }

    @Test
    fun ignoresTrailingSharePunctuation() {
        assertEquals(
            "https://b23.tv/bMUQPo3",
            supportedPlatformLinks.resolve("(https://b23.tv/bMUQPo3).")?.url.toString(),
        )
    }

    @Test
    fun rejectsOtherPlatformsAndLookalikeHosts() {
        listOf(
            "https://v.qq.com/x/cover/123.html",
            "https://www.iqiyi.com/v_123.html",
            "https://v.youku.com/v_show/id_123.html",
            "https://bilibili.com.evil.test/video/av1",
            "https://evilbilibili.com/video/av1",
            "https://b23.tv@evil.test/video/av1",
            "https://evil.test/b23.tv/abc",
        ).forEach { assertNull(it, supportedPlatformLinks.resolve(it)) }
    }

    @Test
    fun routesRegisteredAdapterWithoutChangingBilibiliAdapter() {
        val sample = object : PlatformLinkAdapter {
            override val id = "sample"
            override val schemeLessUrl = Regex("(?!)")
            override fun accepts(url: HttpUrl) = url.host == "media.example.com"
        }
        val resolver = PlatformLinkResolver(listOf(BilibiliLinks, sample))
        assertEquals("sample", resolver.resolve("分享 https://media.example.com/123")?.platformId)
        assertEquals("bilibili", resolver.resolve("分享 b23.tv/bMUQPo3")?.platformId)
    }
}
