package com.huayin.music.models

import com.huayin.music.innertube.models.YTItem
import com.huayin.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
