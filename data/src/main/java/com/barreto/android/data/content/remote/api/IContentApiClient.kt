package com.barreto.android.data.content.remote.api

import io.reactivex.Single
import com.barreto.android.data.content.remote.response.ContentListResponse
import com.barreto.android.data.content.remote.response.ContentResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface IContentApiClient {

    @GET("characters")
    fun fetchContentList(@QueryMap queryOptions: HashMap<String, Any>): Single<ContentListResponse>

    @GET("characters/{codeId}")
    fun fetchContent(@Path("codeId", encoded = true) codeId: Int,
                     @QueryMap queryOptions: HashMap<String, Any>): Single<ContentListResponse>
}