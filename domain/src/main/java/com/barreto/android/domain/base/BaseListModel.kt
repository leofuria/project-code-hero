package com.barreto.android.domain.base

open class BaseListModel<T> (

        var code: Int = 0,

        var msg: String? = null,

        var total: Int = 0,

        var count: Int = 0,

        var offset: Int = 0,

        var items: List<T>? = null
)