package com.barreto.android.presentation.content

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.barreto.android.domain.base.Event
import com.barreto.android.domain.content.model.ContentItem
import com.barreto.android.R
import com.barreto.android.presentation.MainActivity
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ItemActivity : AppCompatActivity() {

    private var codeId = 0

    private val viewModel by viewModel<ContentViewModel>()

    protected val disposable = CompositeDisposable()

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val titleText by lazy { findViewById<TextView>(R.id.titleText) }
    private val descText by lazy { findViewById<TextView>(R.id.descText) }
    private val thumbnail by lazy { findViewById<ImageView>(R.id.thumbnail) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        buildToolbar()

        initialize()
    }

    private fun initialize() {
        viewModel.contentList.observe(this, Observer { onContentEvent(it) })
    }

    private fun buildToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(MainActivity.TOOLBAR_TITLE) ?: getString(R.string.app_name)
        codeId = intent.getIntExtra(CODE_ID, 0)
    }

    override fun onDestroy() {

        disposable.clear()
        super.onDestroy()
    }

    private fun onContentEvent(event: Event<List<ContentItem>>?) {

        when (event) {
            is Event.Idle -> viewModel.getContent(codeId)
            is Event.Data -> {
                titleText.text = event.data[0].name
                descText.text = event.data[0].description

                Picasso.with(this)
                    .load(event.data[0].thumbnail
                        .replace("http", "https")
                        .replace("portrait_medium", "landscape_xlarge")
                    )
                    .noFade()
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .fit()
                    .tag("placholder")
                    .into(thumbnail)
            }
            is Event.Error -> showError(event)
        }
    }

    private fun showError(error: Event.Error?) {

        if (error == null) return

        Timber.d(error.error)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val CODE_ID = "codeId"

        fun buildIntent(
            context: Context,
            codeId: Int,
            title: String
        ): Intent {

            return Intent(context, ItemActivity::class.java)
                .apply {
                    putExtra(CODE_ID, codeId)
                    putExtra(MainActivity.TOOLBAR_TITLE, title)
                }
        }
    }
}