package com.barreto.android.domain.content.model

import java.io.Serializable

data class ContentItem(

        var id: Int = 0,

        var name: String = "",

        var description: String = "",

        var thumbnail: String = ""

) : Serializable