package com.happycola233.bilitools.ui

import android.app.Application
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], application = Application::class)
class ExternalDownloadContractTest {
    @Test
    fun shareUsesSupportedVideoLinkInsteadOfUnrelatedFirstUrl() {
        val intent = Intent(Intent.ACTION_SEND).putExtra(
            Intent.EXTRA_TEXT,
            "来源 https://example.com/ 【滚石爱情故事】 https://b23.tv/bMUQPo3",
        )
        assertEquals("https://b23.tv/bMUQPo3", intent.extractExternalDownloadUrl())
    }

    @Test
    fun clipDataShareUsesTheSameParser() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            clipData = ClipData.newPlainText("视频", "分享：b23.tv/bMUQPo3")
        }
        assertEquals("https://b23.tv/bMUQPo3", intent.extractExternalDownloadUrl())
    }

    @Test
    fun openLinkPreservesEpisodeSelection() {
        val url = "https://www.bilibili.com/video/BV1WZjp6SEf5?p=20"
        assertEquals(url, Intent(Intent.ACTION_VIEW, Uri.parse(url)).extractExternalDownloadUrl())
    }

    @Test
    fun emptyShareDoesNotOpenDownloadPanel() {
        assertNull(Intent(Intent.ACTION_SEND).extractExternalDownloadUrl())
    }
}
