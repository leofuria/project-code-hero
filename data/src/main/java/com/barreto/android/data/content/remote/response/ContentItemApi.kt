package com.barreto.android.data.content.remote.response

import android.net.Uri
import com.barreto.android.domain.content.model.ContentItem
import com.squareup.moshi.Json

class ContentItemApi(

    var id: Int = 0,

    var name: String = "",

    var description: String = "",

    var thumbnail: ContentItemThumbnail? = null

) {

    fun toContent(): ContentItem {

        return ContentItem(
            id,
            name,
            description,
            String.format("%s/portrait_medium.%s", thumbnail?.path, thumbnail?.extension)
        )
    }
}

class ContentItemThumbnail(
    var path: String? = null,

    var extension: String? = null
)