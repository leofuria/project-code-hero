package com.barreto.android.data.content.remote.response

import com.barreto.android.data.base.BaseResponse
import com.barreto.android.domain.content.model.ContentItem
import com.squareup.moshi.Json

data class ContentListResponse(

    var data: ContentResponse? = null

) : BaseResponse<ContentItem>(
    total = data?.contentTotal ?: 0,
    count = data?.contentCount ?: 0,
    offset = data?.contentOffset ?: 0) {

    override fun parseItems(): List<ContentItem> {

        return data?.results?.map { it.toContent() } ?: emptyList()
    }
}
