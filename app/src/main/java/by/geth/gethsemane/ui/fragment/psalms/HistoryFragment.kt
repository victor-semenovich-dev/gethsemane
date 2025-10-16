package by.geth.gethsemane.ui.fragment.psalms

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import by.geth.gethsemane.R
import by.geth.gethsemane.app.GlideApp
import by.geth.gethsemane.data.MusicGroup
import by.geth.gethsemane.databinding.FragmentArticleBinding
import by.geth.gethsemane.ui.activity.PhotoSimpleFullscreenActivity
import by.geth.gethsemane.ui.fragment.worship.WorshipFragment
import by.geth.gethsemane.ui.fragment.article.ArticleDetailsFragment
import by.geth.gethsemane.ui.fragment.base.BaseFragment
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotosGridFragment
import by.geth.gethsemane.ui.fragment.news.NewsDetailsFragment
import by.geth.gethsemane.util.pxToDp
import java.io.InputStream
import java.net.URL
import java.util.regex.Pattern

class HistoryFragment: BaseFragment() {
    companion object {
        private const val ARGS_GROUP_TITLE = "groupTitle"
        private const val ARGS_HISTORY = "history"
        private const val ARGS_IMAGE_URL = "imageUrl"
        private const val ARGS_LEADER = "leader"

        fun newInstance(group: MusicGroup): HistoryFragment {
            val fragment = HistoryFragment()
            fragment.arguments = Bundle().apply {
                putString(ARGS_GROUP_TITLE, group.title)
                putString(ARGS_HISTORY, group.history)
                putString(ARGS_IMAGE_URL, group.image)
                putString(ARGS_LEADER, group.leader)
            }
            return fragment
        }
    }

    private lateinit var groupTitle: String
    private lateinit var history: String
    private lateinit var imageUrl: String
    private lateinit var leader: String

    private lateinit var binding: FragmentArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupTitle = requireArguments().getString(ARGS_GROUP_TITLE, "")
        history = requireArguments().getString(ARGS_HISTORY, "")
        imageUrl = requireArguments().getString(ARGS_IMAGE_URL, "")
        leader = requireArguments().getString(ARGS_LEADER, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.toolbar.title = groupTitle

        GlideApp.with(this).load(imageUrl).into(binding.imageView)
//        imageView.setOnClickListener {
//            imageUrl.let { url ->
//                PhotoSimpleFullscreenActivity.start(requireContext(), url)
//            }
//        }

        binding.titleView.visibility = View.GONE
        if (leader.isBlank()) {
            binding.signatureView.visibility = View.GONE
        } else {
            binding.signatureView.visibility = View.VISIBLE
            binding.signatureView.text = "$leader, руководитель музыкальной группы"
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                injectCSS()
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    val urlObj = URL(url)
                    val uri = Uri.parse(urlObj.toURI().toString())
                    processUri(uri)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
                return true
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                processUri(request.url)
                return true
            }

            private fun processUri(uri: Uri) {
                val path = uri.path
                val id = try {
                    uri.lastPathSegment?.toLong()
                } catch (e: NumberFormatException) {
                    null
                }

                try {
                    when {
                        path?.startsWith("/photo/") == true ->
                            PhotoSimpleFullscreenActivity.start(requireContext(), uri.toString())
                        path?.startsWith("/gallery/album/") == true ->
                            showFragment(GalleryPhotosGridFragment.newInstance(id!!), false)
                        path?.startsWith("/news/") == true ->
                            showFragment(NewsDetailsFragment.newInstance(id!!), false)
                        path?.startsWith("/worship-notes/") == true ->
                            showFragment(ArticleDetailsFragment.newInstance(id!!, ArticleDetailsFragment.BASE_URL_WORSHIP_NOTES), false)
                        path?.startsWith("/to-see-christ/") == true ->
                            showFragment(ArticleDetailsFragment.newInstance(id!!, ArticleDetailsFragment.BASE_URL_TO_SEE_CHRIST), false)
                        path?.startsWith("/worships/") == true ->
                            showFragment(WorshipFragment.newInstance(id!!), false)
                        else -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }

        var newHtml = history
        // fix iframes (for example, youtube embedded videos) to be match_parent
        val windowSize = Point()
        requireActivity().windowManager.defaultDisplay.getSize(windowSize)
        // web view uses sizes in dp
        val iframeWidth = windowSize.x.pxToDp - 16 // 8dp sides padding
        val iframeHeight = iframeWidth * 9 / 16
        val iframePattern = Pattern.compile("<iframe.*></iframe>")
        val iframeMatcher = iframePattern.matcher(history)
        while (iframeMatcher.find()) {
            val iframe = iframeMatcher.group()
            val newIFrame = iframe
                    .replaceFirst("width=\"\\d+\"".toRegex(), "width=\"$iframeWidth\"")
                    .replaceFirst("height=\"\\d+\"".toRegex(), "height=\"$iframeHeight\"")
            newHtml = newHtml.replace(iframe, newIFrame)
        }
        // add css styling (to correct display images)

        newHtml = "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\" />${newHtml}"
        binding.webView.loadDataWithBaseURL("http://geth.by/", newHtml,
                "text/html", "UTF-8", null)
    }

    override fun isShowToolbar(): Boolean {
        return false
    }

    private fun injectCSS() {
        try {
            val inputStream: InputStream = requireContext().assets.open("styles.css")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded: String = Base64.encodeToString(buffer, Base64.NO_WRAP)
            binding.webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +  // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
