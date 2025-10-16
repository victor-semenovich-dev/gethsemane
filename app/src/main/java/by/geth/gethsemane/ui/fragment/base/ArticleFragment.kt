package by.geth.gethsemane.ui.fragment.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import by.geth.gethsemane.R
import by.geth.gethsemane.api.BaseRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.app.GlideApp
import by.geth.gethsemane.databinding.FragmentArticleBinding
import by.geth.gethsemane.ui.activity.PhotoSimpleFullscreenActivity
import by.geth.gethsemane.ui.fragment.worship.WorshipFragment
import by.geth.gethsemane.ui.fragment.article.ArticleDetailsFragment
import by.geth.gethsemane.ui.fragment.gallery.GalleryPhotosGridFragment
import by.geth.gethsemane.ui.fragment.news.NewsDetailsFragment
import by.geth.gethsemane.util.ConnectionUtils
import by.geth.gethsemane.util.pxToDp
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

abstract class ArticleFragment: BaseFragment() {

    abstract val baseUrl: String

    abstract val id: Long
    abstract val category: String?
    abstract val previewImageUrl: String?
    abstract val imageUrl: String?
    abstract val title: String?
    abstract val html: String?
    abstract val author: String?
    abstract val date: Date?

    private var isStarted = false
    private var isPostponeClose = false

    private lateinit var binding: FragmentArticleBinding

    override fun isShowToolbar() = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Server.addCallback(serverCallback)
        binding.webView.settings.javaScriptEnabled = true

        if (ConnectionUtils.isNetworkConnected(context)) {
            loadData()
        } else if (html == null) {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
            fragmentManager?.popBackStack()
        }

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
                Log.d("MyTag", "uri: $uri")
                Log.d("MyTag", "path: $path")
                val id = try {
                    uri.lastPathSegment?.toLong()
                } catch (e: NumberFormatException) {
                    null
                }

                try {
                    when {
                        path?.startsWith("/photo/") == true ->
                            PhotoSimpleFullscreenActivity.start(requireContext(), uri.toString())
                        path?.startsWith("/images/") == true ->
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

        binding.imageView.setOnClickListener {
            imageUrl?.let { url ->
                PhotoSimpleFullscreenActivity.start(requireContext(), url)
            }
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.toolbar.inflateMenu(R.menu.fragment_article)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "$baseUrl$id")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Server.removeCallback(serverCallback)
    }

    override fun onStart() {
        super.onStart()
        isStarted = true
        if (isPostponeClose)
            fragmentManager?.popBackStack()
        if (html != null)
            updateUI()
    }

    override fun onStop() {
        super.onStop()
        isStarted = false
        binding.webView.reload()
    }

    abstract fun isUpdating(): Boolean

    abstract fun loadData()

    abstract fun getRequest(): BaseRequest

    abstract fun applyData(result: Any?)

    private fun updateUI() {
        if (previewImageUrl.isNullOrBlank()) {
            binding.imageView.visibility = View.GONE
        } else {
            binding.imageView.visibility = View.VISIBLE
            GlideApp.with(this).load(previewImageUrl).into(binding.imageView)
        }

        binding.titleView.text = title
        binding.toolbar.title = category

        html?.let { html ->
            var newHtml = html

            // fix iframes (for example, youtube embedded videos) to be match_parent
            val windowSize = Point()
            requireActivity().windowManager.defaultDisplay.getSize(windowSize)
            // web view uses sizes in dp
            val iframeWidth = windowSize.x.pxToDp - 16 // 8dp sides padding
            val iframeHeight = iframeWidth * 9 / 16
            val iframePattern = Pattern.compile("<iframe.*></iframe>")
            val iframeMatcher = iframePattern.matcher(html)
            while (iframeMatcher.find()) {
                val iframe = iframeMatcher.group()
                val newIFrame = iframe
                        .replaceFirst("width=\"\\d+\"".toRegex(), "width=\"$iframeWidth\"")
                        .replaceFirst("height=\"\\d+\"".toRegex(), "height=\"$iframeHeight\"")
                newHtml = newHtml.replace(iframe, newIFrame)
            }
            // add css styling (to correct display images)

            newHtml = "<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\" />${newHtml}"
//            newHtml += "<a href=\"http://geth.by/photo/5d959c4f75a24.jpg\">http://geth.by/photo/5d959c4f75a24.jpg</a><br/><br/><br/>"
//            newHtml += "<a href=\"http://geth.by/gallery/album/5\">http://geth.by/gallery/album/5</a><br/><br/><br/>"
//            newHtml += "<a href=\"http://geth.by/news/412\">http://geth.by/news/412</a><br/><br/><br/>"
//            newHtml += "<a href=\"http://geth.by/worship-notes/313\">http://geth.by/worship-notes/313</a><br/><br/><br/>"
//            newHtml += "<a href=\"http://geth.by/worships/17311\">http://geth.by/worships/17311</a><br/><br/><br/>"
            binding.webView.loadDataWithBaseURL("http://geth.by/", newHtml,
                    "text/html", "UTF-8", null)
        }

        date?.let { date ->
            author?.let { author ->
                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("ru"))
                binding.signatureView?.text = String.format("%s, %s", author, dateFormat.format(date))
            }
        }
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

    private val serverCallback = object : Server.Callback {
        override fun onStarted(request: BaseRequest) {}

        override fun onSuccess(request: BaseRequest, result: Any?) {
            if (request == getRequest()) {
                applyData(result)
                updateUI()
            }
        }

        override fun onFailure(request: BaseRequest, code: Int, message: String) {
            if (request == getRequest()) {
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show()
                if (html == null) {
                    if (isStarted) {
                        fragmentManager?.popBackStack()
                    } else {
                        isPostponeClose = true
                    }
                }
            }
        }

        override fun onFailure(request: BaseRequest, t: Throwable) {
            if (request == getRequest()) {
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show()
                if (html == null) {
                    if (isStarted) {
                        fragmentManager?.popBackStack()
                    } else {
                        isPostponeClose = true
                    }
                }
            }
        }
    }
}