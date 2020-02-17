package com.barreto.android.data.content.remote.response

import com.squareup.moshi.Json

data class ContentResponse(

    var limit: Int = 0,

    @Json(name = "total")
    var contentTotal: Int = 0,

    @Json(name = "count")
    var contentCount: Int = 0,

    @Json(name = "offset")
    var contentOffset: Int = 0,

    var results: List<ContentItemApi> = emptyList()
)