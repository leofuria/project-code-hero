package com.barreto.android.domain.content.usecase

import com.barreto.android.domain.base.BaseListModel
import com.barreto.android.domain.base.Event
import com.barreto.android.domain.content.IContentRepository
import com.barreto.android.domain.content.model.ContentItem
import io.reactivex.Observable

class GetContentUseCase(
        private val repository: IContentRepository
) {

    fun execute(codeId: Int, queryOptions: HashMap<String, Any>): Observable<Event<BaseListModel<ContentItem>>> {

        return Observable.concat(
                Observable.just(Event.loading()),
                getContent(codeId, queryOptions))
    }

    private fun getContent(codeId: Int, queryOptions: HashMap<String, Any>): Observable<Event<BaseListModel<ContentItem>>> {

        return repository.getContent(codeId, queryOptions)
                .map { Event.data(it) }
                .onErrorReturn { Event.error(it) }
                .toObservable()
    }
}