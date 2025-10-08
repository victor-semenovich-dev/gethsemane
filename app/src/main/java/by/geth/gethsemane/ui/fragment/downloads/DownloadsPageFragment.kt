package by.geth.gethsemane.ui.fragment.downloads

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.geth.gethsemane.R
import by.geth.gethsemane.data.base.AudioItem
import by.geth.gethsemane.databinding.FragmentDownloadsPageBinding
import by.geth.gethsemane.download.DownloadController
import by.geth.gethsemane.ui.adapter.AudioItemsListAdapter
import by.geth.gethsemane.ui.fragment.base.AudioFragment
import by.geth.gethsemane.ui.style.VerticalSpaceItemDecoration

@SuppressLint("NotifyDataSetChanged")
class DownloadsPageFragment: Fragment(), AudioItemsListAdapter.OnClickListener {
    enum class DownloadsType { SERMON, WITNESS, SONG }

    companion object {
        private const val ARGS_DOWNLOADS_TYPE = "ARGS_DOWNLOADS_TYPE"

        fun newInstance(type: DownloadsType): DownloadsPageFragment {
            val fragment = DownloadsPageFragment()
            fragment.arguments = Bundle().apply {
                putSerializable(ARGS_DOWNLOADS_TYPE, type)
            }
            return fragment
        }
    }

    private lateinit var type: DownloadsType
    private lateinit var viewBinding: FragmentDownloadsPageBinding
    private lateinit var viewModel: DownloadsPageViewModel
    private lateinit var itemsAdapter: AudioItemsListAdapter
    private lateinit var audioFragment: AudioFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = requireArguments().getSerializable(ARGS_DOWNLOADS_TYPE) as DownloadsType
        audioFragment = requireParentFragment() as AudioFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = DownloadsPageViewModelFactory(type)
        viewModel = ViewModelProvider(this, viewModelFactory)[DownloadsPageViewModel::class.java]
        viewBinding = FragmentDownloadsPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (type) {
            DownloadsType.SERMON -> viewBinding.emptyView.setText(R.string.downloads_empty_sermon)
            DownloadsType.WITNESS -> viewBinding.emptyView.setText(R.string.downloads_empty_witness)
            DownloadsType.SONG -> viewBinding.emptyView.setText(R.string.downloads_empty_song)
        }

        itemsAdapter = AudioItemsListAdapter(audioFragment)
        itemsAdapter.setOnClickListener(this)

        val outerSpace = resources.getDimensionPixelSize(R.dimen.space_8)
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        viewBinding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(0, outerSpace))
        viewBinding.recyclerView.adapter = itemsAdapter

        viewModel.audioItems.observe(viewLifecycleOwner) { itemsList ->
            itemsAdapter.audioItems = itemsList
            itemsAdapter.notifyDataSetChanged()

            viewBinding.emptyView.visibility = if (itemsList.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isInProgress.observe(viewLifecycleOwner) { isInProgress ->
            viewBinding.progressView.visibility = if (isInProgress) View.VISIBLE else View.GONE
        }

        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.registerReceiver(fileDeletedReceiver, IntentFilter(DownloadController.ACTION_ITEM_DELETED))

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val lbm = LocalBroadcastManager.getInstance(requireContext())
        lbm.unregisterReceiver(fileDeletedReceiver)
    }

    override fun onItemClick(audioItem: AudioItem) {
    }

    override fun onAudioClick(audioItem: AudioItem) {
        audioFragment.playStop(audioItem)
    }

    override fun onDownloadClick(audioItem: AudioItem) {
        audioFragment.download(audioItem)
    }

    override fun onDeleteClick(audioItem: AudioItem) {
        audioFragment.delete(audioItem)
    }

    fun updateUiState() {
        if (this::itemsAdapter.isInitialized) {
            itemsAdapter.notifyDataSetChanged()
        }
    }

    private val fileDeletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.loadData()
        }
    }
}
