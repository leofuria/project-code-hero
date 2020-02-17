package com.barreto.android.presentation.content

import androidx.lifecycle.MutableLiveData
import com.barreto.android.BuildConfig
import com.barreto.android.domain.base.Event
import com.barreto.android.domain.content.model.ContentItem
import com.barreto.android.domain.content.usecase.GetContentListUseCase
import com.barreto.android.domain.content.usecase.GetContentUseCase
import com.barreto.android.domain.util.ISchedulerProvider
import com.barreto.android.presentation.base.BaseViewModel
import io.reactivex.rxkotlin.addTo
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ContentViewModel(
    scheduler: ISchedulerProvider,
    private val getContentListUseCase: GetContentListUseCase,
    private val getContentUseCase: GetContentUseCase
) : BaseViewModel(scheduler) {

    private var offset: Int = 0
    private var limit: Int = 10
    private var totalItemCount: Int = 0
    private var currentItemCount: Int = 0
    private var queryOptions = HashMap<String, Any>()
    private var queryText = ""

    val hasNextPage = MutableLiveData<Boolean>().apply { value = true }

    val error = MutableLiveData<Event.Error?>().apply { value = null }

    val total = MutableLiveData<Int>().apply { value = 0 }

    val contentList = MutableLiveData<Event<List<ContentItem>>>().apply { value = Event.idle() }

    val content = MutableLiveData<Event<ContentItem>>().apply { value = Event.idle() }

    fun md5(string: String): String {
        val hash: ByteArray
        hash = try {
            MessageDigest.getInstance("MD5").digest(string.toByteArray(charset("UTF-8")))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Huh, MD5 should be supported?", e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Huh, UTF-8 should be supported?", e)
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            val i: Int = (b.toInt() and 0xFF)
            if (i < 0x10) hex.append('0')
            hex.append(Integer.toHexString(i))
        }
        return hex.toString()
    }

    private fun queryParams(): HashMap<String, Any> {
        val timestamp = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault()).format(Date())
        val publicKey = BuildConfig.MARVEL_PUBLIC_KEY
        val hash = md5(timestamp+BuildConfig.MARVEL_PRIVATE_KEY+publicKey)

        return HashMap<String, Any>().apply {
            this["ts"] = timestamp
            this["apikey"] = publicKey
            this["hash"] = hash
            this["limit"] = limit
            queryText.takeIf { it.isNotBlank() }?.let { this["nameStartsWith"] = it }
        }
    }

    fun getContentList(queryText: String = "") {

        offset = 0

        this.queryText = queryText

        getContentListUseCase.execute(queryParams())
            .subscribeOn(scheduler.backgroundThread())
            .observeOn(scheduler.mainThread())
            .subscribe {
                when (it) {
                    is Event.Data -> {
                        offset = it.data.offset + limit
                        totalItemCount = it.data.total
                        total.postValue(totalItemCount)
                        currentItemCount = it.data.count
                        hasNextPage.postValue(currentItemCount < totalItemCount)
                        contentList.postValue(Event.data(it.data.items ?: emptyList()))
                    }
                    is Event.Loading -> contentList.postValue(it)
                    is Event.Error -> contentList.postValue(it)
                }
            }
            .addTo(disposables)
    }

    fun loadMore() {
        if (currentItemCount < totalItemCount) {

            queryOptions = queryParams().apply {
                this["offset"] = offset
            }

            getContentListUseCase.execute(queryOptions)
                .subscribeOn(scheduler.backgroundThread())
                .observeOn(scheduler.mainThread())
                .subscribe {
                    when (it) {
                        is Event.Data -> {
                            offset = it.data.offset + limit
                            totalItemCount = it.data.total
                            total.postValue(totalItemCount)
                            currentItemCount += it.data.count
                            hasNextPage.postValue(currentItemCount < totalItemCount)

                            val cList = (contentList.value as? Event.Data)?.data ?: emptyList()
                            val nList = it.data.items ?: emptyList()

                            contentList.postValue(Event.data(cList + nList))
                        }
                        is Event.Error -> error.postValue(it)
                    }
                }
                .addTo(disposables)
        } else {
            hasNextPage.postValue(false)
        }
    }

    fun getContent(codeId: Int) {

        getContentUseCase.execute(codeId, queryParams())
            .subscribeOn(scheduler.backgroundThread())
            .observeOn(scheduler.mainThread())
            .subscribe {
                when (it) {
                    is Event.Data -> {
                        totalItemCount = it.data.total
                        total.postValue(totalItemCount)
                        currentItemCount = it.data.count
                        hasNextPage.postValue(false)
                        contentList.postValue(Event.data(it.data.items ?: emptyList()))
                    }
                    is Event.Loading -> contentList.postValue(it)
                    is Event.Error -> contentList.postValue(it)
                }
            }
            .addTo(disposables)
    }
}