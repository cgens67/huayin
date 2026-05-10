package com.huayin.music.innertube.models.body

import com.huayin.music.innertube.models.Context
import com.huayin.music.innertube.models.Continuation
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
    val continuation: String?
)
