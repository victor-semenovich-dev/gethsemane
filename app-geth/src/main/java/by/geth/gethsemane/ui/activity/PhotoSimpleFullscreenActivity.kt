package by.geth.gethsemane.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import by.geth.gethsemane.R
import by.geth.gethsemane.databinding.ActivityPhotoSimpleFullscreenBinding
import by.geth.gethsemane.ui.activity.base.FullscreenActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class PhotoSimpleFullscreenActivity: FullscreenActivity() {
    companion object {
        private const val EXTRA_URL = "EXTRA_URL"

        fun start(context: Context, photoUrl: String) {
            val intent = Intent(context, PhotoSimpleFullscreenActivity::class.java)
            intent.putExtra(EXTRA_URL, photoUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPhotoSimpleFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_URL)
        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                binding.progressView.visibility = View.GONE
                binding.photoView.setImageBitmap(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                Toast.makeText(this@PhotoSimpleFullscreenActivity, R.string.error_data_load, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}