package by.geth.gethsemane.ui.fragment.base

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.geth.gethsemane.R
import by.geth.gethsemane.api.BaseRequest
import by.geth.gethsemane.api.GetPageRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.databinding.FragmentPagesBinding
import by.geth.gethsemane.util.ConnectionUtils
import kotlin.math.max

abstract class PagesFragment<T>: BaseFragment() {

    companion object {
        private const val PAGE_SIZE = 20

        private const val STATE_PAGES_LOADED = "STATE_PAGES_LOADED"
        private const val STATE_IS_INITIALIZED = "STATE_IS_INITIALIZED"
    }

    private var getDataTask: GetDataTask<T>? = null
    private var pagesLoaded: Int = 0
    private var isInitialized: Boolean = false

    private lateinit var binding: FragmentPagesBinding

    protected abstract val adapter: RecyclerView.Adapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        pagesLoaded = savedInstanceState?.getInt(STATE_PAGES_LOADED, 0) ?: 0
        isInitialized = savedInstanceState?.getBoolean(STATE_IS_INITIALIZED, false) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Server.addCallback(serverCallback)

        getDataTask = GetDataTask(::getData, ::onGetData).apply { execute() }

        if (isUpdating()) {
            binding.refreshLayout.post { binding.refreshLayout.isRefreshing = true }
        }

        binding.recyclerView.addOnScrollListener(onScrollListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Server.removeCallback(serverCallback)

        getDataTask?.cancel(true)
        getDataTask = null

        binding.recyclerView.removeOnScrollListener(onScrollListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_PAGES_LOADED, pagesLoaded)
        outState.putBoolean(STATE_IS_INITIALIZED, isInitialized)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_pages, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                if (ConnectionUtils.isNetworkConnected(requireContext())) {
                    loadData()
                } else {
                    Toast.makeText(requireContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> false
        }
    }

    abstract fun getData(): List<T>

    abstract fun loadData(page: Int = 1)

    abstract fun isUpdating(): Boolean

    abstract fun applyData(data: List<T>)

    abstract fun getItemCount(): Int

    abstract fun getRequest(): BaseRequest

    private fun onGetData(data: List<T>) {
        getDataTask = null
        applyData(data)
        binding.recyclerView.adapter = adapter

        if (!isInitialized)
            pagesLoaded = data.size / PAGE_SIZE

        if (!isInitialized && ConnectionUtils.isNetworkConnected(requireContext()))
            loadData()

        binding.refreshLayout.setOnRefreshListener {
            if (ConnectionUtils.isNetworkConnected(requireContext())) {
                loadData()
            } else {
                binding.refreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lastVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (pagesLoaded >= 0 && getItemCount() - lastVisiblePosition - 1 <= 10 && ConnectionUtils.isNetworkConnected(requireContext()))
                loadData(pagesLoaded + 1)
        }
    }

    private val serverCallback = object : Server.Callback {
        override fun onStarted(request: BaseRequest) {
            if (request == getRequest())
                binding.refreshLayout.post { binding.refreshLayout.isRefreshing = true }
        }

        @Suppress("UNCHECKED_CAST")
        override fun onSuccess(request: BaseRequest, result: Any?) {
            if (request == getRequest()) {
                val page = (request as GetPageRequest).page
                val pageSize = request.limit
                val loadResult = result as Server.PageResult<T>

                binding.refreshLayout.post { binding.refreshLayout.isRefreshing = false }
                applyData(loadResult.allItems)
                pagesLoaded = if (loadResult.loadedItems.size < pageSize) -1 else
                    max(loadResult.allItems.size / PAGE_SIZE, page)

                if (page == 1) {
                    isInitialized = true
                    binding.recyclerView.post{ binding.recyclerView.smoothScrollToPosition(0) }
                }
            }
        }

        override fun onFailure(request: BaseRequest, code: Int, message: String) {
            if (request == getRequest()) {
                binding.refreshLayout.post { binding.refreshLayout.isRefreshing = false }
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(request: BaseRequest, t: Throwable) {
            if (request == getRequest()) {
                binding.refreshLayout.post { binding.refreshLayout.isRefreshing = false }
                Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class GetDataTask<T>(
            private val loadData: () -> List<T>,
            private val onGetData: (List<T>) -> Unit) : AsyncTask<Unit, Unit, List<T>>() {

        override fun doInBackground(vararg params: Unit?): List<T> {
            return loadData()
        }

        override fun onPostExecute(result: List<T>) {
            onGetData(result)
        }
    }
}