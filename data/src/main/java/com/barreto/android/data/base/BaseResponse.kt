package com.barreto.android.data.base

import com.barreto.android.domain.base.BaseListModel

abstract class BaseResponse<T> (

    var code: Int = 0,

    var status: String? = null,

    var total: Int = 0,

    var count: Int = 0,

    var offset: Int = 0

) {

    fun toListModel() : BaseListModel<T> {
        return BaseListModel(
            code,
            status,
            total,
            count,
            offset,
            parseItems()
        )
    }

    abstract fun parseItems(): List<T>
}