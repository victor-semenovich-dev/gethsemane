package by.geth.gethsemane.ui.fragment.sermons

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.geth.gethsemane.R
import by.geth.gethsemane.data.base.AudioItem
import by.geth.gethsemane.databinding.FragmentSermonListBinding
import by.geth.gethsemane.ui.adapter.AudioItemsListAdapter
import by.geth.gethsemane.ui.fragment.base.AudioFragment
import by.geth.gethsemane.ui.style.VerticalSpaceItemDecoration
import by.geth.gethsemane.util.ConnectionUtils
import by.geth.gethsemane.util.debounce

@SuppressLint("NotifyDataSetChanged")
abstract class BaseSermonListFragment<T: AudioItem>: AudioFragment(), AudioItemsListAdapter.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private lateinit var searchView: SearchView

    private lateinit var sermonsAdapter: AudioItemsListAdapter

    private lateinit var viewModel: BaseSermonListViewModel<T>
    private lateinit var viewBinding: FragmentSermonListBinding

    abstract fun getViewModel(): BaseSermonListViewModel<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = getViewModel()
        viewBinding = FragmentSermonListBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.sermonsView.removeOnScrollListener(onScrollListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.refreshLayout.setOnRefreshListener(this)

        this.sermonsAdapter = AudioItemsListAdapter(this)
        this.sermonsAdapter.setOnClickListener(this)

        val outerSpace = resources.getDimensionPixelSize(R.dimen.space_8)
        viewBinding.sermonsView.layoutManager = LinearLayoutManager(context)
        viewBinding.sermonsView.addItemDecoration(VerticalSpaceItemDecoration(0, outerSpace))
        viewBinding.sermonsView.adapter = this.sermonsAdapter
        (viewBinding.sermonsView.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        viewBinding.sermonsView.addOnScrollListener(onScrollListener)

        viewModel.eventsData.observe(viewLifecycleOwner) {
            it?.let { event ->
                when (event) {
                    AudioListEvent.LOAD_ERROR -> {
                        Toast.makeText(requireContext(), R.string.error_data_load, Toast.LENGTH_LONG).show()
                    }
                }
                viewModel.consumeEvent()
            }
        }
        viewModel.itemList.observe(viewLifecycleOwner) { itemList ->
            this.sermonsAdapter.audioItems = itemList
            this.sermonsAdapter.notifyDataSetChanged()
        }
        viewModel.isInProgress.observe(viewLifecycleOwner) { isInProgress ->
            viewBinding.refreshLayout.isRefreshing = isInProgress
        }
        viewModel.fetchData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_sermon_list, menu)
        val item = menu.findItem(R.id.action_search)
        this.searchView = item.actionView as SearchView
        this.searchView.setOnQueryTextListener(searchQueryListener)
    }

    override fun onDestroyOptionsMenu() {
        this.searchView.post { this.searchView.clearFocus() }
        super.onDestroyOptionsMenu()
    }

    override fun updateAudioUI() {
        this.sermonsAdapter.notifyDataSetChanged()
    }

    override fun updateDownloadUI() {
        this.sermonsAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(item: AudioItem) {
        // do nothing
    }

    override fun onAudioClick(item: AudioItem) {
        playStop(item)
    }

    override fun onDownloadClick(item: AudioItem) {
        download(item)
    }

    override fun onDeleteClick(item: AudioItem) {
        delete(item)
    }

    override fun onRefresh() {
        viewModel.fetchData(showMessageOnError = true)
    }

    private val searchQueryListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!TextUtils.isEmpty(query)) {
                    searchView.post { searchView.clearFocus() }
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                handleOnQueryTextChanged(newText.trim())
                return true
            }
        }

    private val handleOnQueryTextChanged: (String) -> Unit
        get() = debounce(coroutineScope = viewLifecycleOwner.lifecycleScope) { query ->
            if (query.isEmpty() or (query.length >= 3)) {
                viewModel.fetchData(showMessageOnError = query.isNotEmpty(), query = query)
            }
        }

    private val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lastVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (sermonsAdapter.itemCount >= 0 &&
                sermonsAdapter.itemCount - lastVisiblePosition - 1 <= BaseSermonListViewModel.PAGE_SIZE / 2 &&
                ConnectionUtils.isNetworkConnected(requireContext())
            ) {
                viewModel.loadNextPage()
            }
        }
    }
}