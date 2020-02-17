package com.barreto.android.presentation.content.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.barreto.android.domain.content.model.ContentItem
import com.barreto.android.R
import com.barreto.android.presentation.base.adapter.BaseViewHolder
import com.barreto.android.presentation.base.ui.CircleTransform
import com.squareup.picasso.Picasso
import io.reactivex.Observable

class ListViewHolder(view: View) : BaseViewHolder<ContentItem>(view) {

    private val titleText by lazy { itemView.findViewById<TextView>(R.id.titleText) }
    private val descText by lazy { itemView.findViewById<TextView>(R.id.descText) }
    private val thumbnail by lazy { itemView.findViewById<ImageView>(R.id.thumbnail) }

    init {
        itemView.setOnClickListener {
            currentItem?.run {
                notifyItemClick.onNext(this)
            }
        }
    }

    override fun bindItem(item: ContentItem?) {
        super.bindItem(item)

        currentItem = item
        currentItem?.also {
            titleText.text = it.name
            descText.text = it.description

            Picasso.with(itemView.context)
                .load(it.thumbnail.replace("http", "https"))
                .noFade()
                .transform(CircleTransform())
                .noPlaceholder()
                .error(R.drawable.ic_image_black_24dp)
                .fit()
                .tag("placholder")
                .into(thumbnail)
        }
    }

    fun getItemClickObservable(): Observable<ContentItem> {
        return notifyItemClick
    }
}