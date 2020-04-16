package com.auru.betterme.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.auru.betterme.R
import com.auru.betterme.domain.MovieShared
import com.auru.betterme.presentation.base.SectionsPagerAdapter
import com.auru.betterme.presentation.viewutils.ResultSealed
import com.auru.betterme.utils.EventBusUtils
import com.auru.betterme.utils.eventbusevents.ShareMovieEvent
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var errorSnackbar: Snackbar? =
        null //it is to be used to lead user to settings in case of WiFi absence

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EventBusUtils.registerEventBusSafely(this)

        viewModel.getMovieHomepageLiveData().observe(this, Observer { result ->
            when (result) {
                is ResultSealed.Success -> {
                    shareMovie(result.data as MovieShared)
                }
                is ResultSealed.Failure -> {
                    //TODO better use snackBar
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
                //            if(isLoading) {
//                hideErrorSnackBar()
//            }
            }

        })

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
    }


    override fun onDestroy() {

        EventBusUtils.unRegisterEventBusSafely(this)

        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShareMovieEvent(movieEvent: ShareMovieEvent) {
        viewModel.getMovieHomepage(movieEvent.movie)
    }

    private fun shareMovie(movieSh: MovieShared) {
        if (movieSh.bitmap == null && movieSh.homepage.isNullOrBlank()) {
            return
        }
        val isSharingWithoutBitmap = !movieSh.homepage.isNullOrBlank()
        var shareIntent: Intent? = null
        if (!movieSh.homepage.isNullOrBlank()) {
            shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, movieSh.homepage)
            }, null)
        } else if (movieSh.bitmap != null) {
            shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, movieSh.name ?: "")
            }, null)
        }
//            val share = Intent.createChooser(Intent().apply {
//                action = if(isSharingWithoutBitmap) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE
//                type = if(isSharingWithoutBitmap) "text/plain" else ""//todo
//                putExtra(Intent.EXTRA_TEXT, homePage)
////                putExtra(Intent.EXTRA_, homePage)
//
////            // (Optional) Here we're setting the title of the content
////            putExtra(Intent.EXTRA_TITLE, "Introducing content previews")
////
////            // (Optional) Here we're passing a content URI to an image to be displayed
////            data = contentUri
////            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            }, null)
        shareIntent?.let {
            startActivity(it)
        }
    }
}


//    private fun showErrorSnackBar(message: String) {
//        errorSnackbar =
//            Snackbar.make(coordinator_layout, message, Snackbar.LENGTH_INDEFINITE)
//
//        errorSnackbar?.apply {
//            setAction(R.string.close) {}
//            setActionTextColor(ResourcesCompat.getColor(resources, R.color.yellow, null))
//                .show()
//        }
//    }
//
//    private fun hideErrorSnackBar() {
//        errorSnackbar?.let {
//            if (it.isShown) {
//                it.dismiss()
//            }
//        }
//    }

}