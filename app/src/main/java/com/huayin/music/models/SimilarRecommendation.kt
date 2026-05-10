package com.huayin.music.models

import com.arturo254.innertube.models.YTItem
import com.huayin.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
