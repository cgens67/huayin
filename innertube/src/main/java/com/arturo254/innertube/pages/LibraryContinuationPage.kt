package com.huayin.music.innertube.pages

import com.huayin.music.innertube.models.YTItem

data class LibraryContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)
