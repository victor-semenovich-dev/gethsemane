package by.geth.gethsemane.ui.fragment.psalms

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.geth.gethsemane.R
import by.geth.gethsemane.api.BaseRequest
import by.geth.gethsemane.api.GetSongsRequest
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.app.AppPreferences
import by.geth.gethsemane.data.MusicGroup
import by.geth.gethsemane.data.Song
import by.geth.gethsemane.databinding.FragmentSongsListBinding
import by.geth.gethsemane.download.DownloadController
import by.geth.gethsemane.ui.fragment.base.AudioFragment
import by.geth.gethsemane.ui.view.AnimatableImageView
import by.geth.gethsemane.util.ConnectionUtils
import by.geth.gethsemane.util.share
import com.activeandroid.query.Select
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SongsListFragment: AudioFragment() {

    enum class SortType { NAME, DATE }

    companion object {
        private const val ARGS_GROUP_ID = "groupId"

        fun newInstance(group: MusicGroup): SongsListFragment {
            val fragment = SongsListFragment()
            fragment.arguments = Bundle().apply {
                putString(ARGS_AB_TITLE, group.title)
                putInt(ARGS_UI_FLAGS, UI_FLAG_DISPLAY_HOME_AS_UP)
                putLong(ARGS_GROUP_ID, group.externalId)
            }
            return fragment
        }
    }

    private lateinit var group: MusicGroup
    private lateinit var adapter: SongsListAdapter

    private var allSongs: List<Song> = listOf()
    private var filterTask: FilterTask? = null
    private var query: String = ""

    private lateinit var mSearchView: SearchView
    private lateinit var mSortByNameItem: MenuItem
    private lateinit var mSortByDateItem: MenuItem

    private lateinit var binding: FragmentSongsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val groupId = arguments?.getLong(ARGS_GROUP_ID) ?: 0
        group = Select().from(MusicGroup::class.java)
            .where("${MusicGroup.COLUMN_EXTERNAL_ID} = $groupId").executeSingle()
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        filterTask?.cancel(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSongsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.adapter = SongsListAdapter(adapterCallback, this)
        binding.recyclerView.adapter = this.adapter
        this.adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.recyclerView.scrollToPosition(0)
            }
        })
        updateSongsList()

        binding.refreshLayout.setOnRefreshListener {
            if (ConnectionUtils.isNetworkConnected(context)) {
                Server.getSongs(group.externalId)
            } else {
                Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show()
                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_songs, menu)

        val item = menu.findItem(R.id.action_search)
        mSearchView = item.actionView as SearchView
        mSearchView.setOnQueryTextListener(mSearchQueryListener)

        mSortByNameItem = menu.findItem(R.id.action_sort_by_name)
        mSortByDateItem = menu.findItem(R.id.action_sort_by_date)

        when (AppPreferences.getInstance().songsSortType) {
            SortType.NAME -> mSortByNameItem.isChecked = true
            SortType.DATE -> mSortByDateItem.isChecked = true
            else -> {}
        }

        mSortByNameItem.setOnMenuItemClickListener {
            mSortByNameItem.isChecked = true
            AppPreferences.getInstance().songsSortType = SortType.NAME
            executeFilterTask()
            true
        }

        mSortByDateItem.setOnMenuItemClickListener {
            mSortByDateItem.isChecked = true
            AppPreferences.getInstance().songsSortType = SortType.DATE
            executeFilterTask()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                showFragment(HistoryFragment.newInstance(group))
                true
            }
            else -> false
        }
    }

    override fun onStart() {
        super.onStart()
        if (Server.isRunning(GetSongsRequest(group.externalId))) {
            binding.refreshLayout.isRefreshing = true
        }
        Server.addCallback(serverCallback)

        val dayBefore = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
        if (ConnectionUtils.isNetworkConnected(context) &&
            (group.lastUpdate == null || group.lastUpdate < dayBefore)) {

            Server.getSongs(group.externalId)
        }
    }

    override fun onStop() {
        super.onStop()
        Server.removeCallback(serverCallback)
    }

    override fun updateAudioUI() {
        this.adapter.notifyDataSetChanged()
    }

    override fun updateDownloadUI() {
        this.adapter.notifyDataSetChanged()
    }

    private fun updateSongsList() {
        allSongs = Select().from(Song::class.java)
            .where("${Song.COLUMN_GROUP_ID} = ${group.externalId}").execute()
        executeFilterTask()
    }

    private fun executeFilterTask() {
        if (filterTask != null) {
            filterTask?.cancel(true)
            filterTask = null
        }
        filterTask = FilterTask()
        filterTask?.execute()
    }

    private val adapterCallback = object : SongsListAdapter.Callback {
        override fun onAudioClick(song: Song) {
            playStop(song)
        }
        override fun onDownloadClick(song: Song) {
            download(song)
        }
        override fun onDeleteClick(song: Song) {
            delete(song)
        }
    }

    private val serverCallback = object : Server.Callback {
        override fun onStarted(request: BaseRequest) {
            if (request == GetSongsRequest(group.externalId)) {
                binding.refreshLayout.isRefreshing = true
            }
        }

        override fun onSuccess(request: BaseRequest, result: Any?) {
            if (request == GetSongsRequest(group.externalId)) {
                binding.refreshLayout.isRefreshing = false
                group.lastUpdate = Date()
                group.save()
                updateSongsList()
            }
        }

        override fun onFailure(request: BaseRequest, t: Throwable) {
            if (request == GetSongsRequest(group.externalId)) {
                binding.refreshLayout.isRefreshing = false
                Toast.makeText(context, R.string.error_data_load, Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(request: BaseRequest, code: Int, message: String) {
            if (request == GetSongsRequest(group.externalId)) {
                binding.refreshLayout.isRefreshing = false
                Toast.makeText(context, R.string.error_data_load, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class SongsListAdapter(private val callback: Callback, private val audioFragment: AudioFragment): ListAdapter<Song, SongsListAdapter.ViewHolder>(DIFF_CALLBACK) {
        companion object {
            val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.externalId() == newItem.externalId()

                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem == newItem
            }
        }

        interface Callback {
            fun onAudioClick(song: Song)
            fun onDownloadClick(song: Song)
            fun onDeleteClick(song: Song)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_media_card, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val song = getItem(position)
            holder.titleView.text = song.getTitle()
            holder.subtitleView.text = if (song.getDate() == null) "" else
                SimpleDateFormat("dd.MM.yyyy", Locale.US).format(song.getDate())

            if (audioFragment.isInProgress(song)) {
                holder.audioView.startAnimation()
            } else {
                holder.audioView.stopAnimation()
            }
            holder.audioView.setOnClickListener { callback.onAudioClick(song) }

            if (TextUtils.isEmpty(song.audioLocalUri)) {
                if (DownloadController.getInstance().isDownloadInProgress(song)) {
                    holder.downloadButton.visibility = View.GONE
                    holder.downloadProgressView.visibility = View.VISIBLE
                    holder.deleteView.visibility = View.GONE
                } else {
                    holder.downloadButton.visibility = View.VISIBLE
                    holder.downloadProgressView.visibility = View.GONE
                    holder.deleteView.visibility = View.GONE
                }
            } else {
                holder.downloadButton.visibility = View.GONE
                holder.downloadProgressView.visibility = View.GONE
                holder.deleteView.visibility = View.VISIBLE
            }
            holder.downloadButton.setOnClickListener { callback.onDownloadClick(song) }
            holder.deleteView.setOnClickListener { callback.onDeleteClick(song) }

            if (song.audioLocalUri == null) {
                holder.shareButton.visibility = View.GONE
            } else {
                holder.shareButton.visibility = View.VISIBLE
                holder.shareButton.setOnClickListener { share(song, holder.itemView.context) }
            }
        }

        private class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val audioView: AnimatableImageView = itemView.findViewById(R.id.media_audio_play)
            val titleView: TextView = itemView.findViewById(R.id.media_title)
            val subtitleView: TextView = itemView.findViewById(R.id.media_subtitle)
            val downloadButton: View = itemView.findViewById(R.id.media_download_button)
            val downloadProgressView: View = itemView.findViewById(R.id.media_download_progress)
            val deleteView: View = itemView.findViewById(R.id.media_download_delete)
            val shareButton: View = itemView.findViewById(R.id.media_share)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class FilterTask : AsyncTask<String?, Void?, List<Song>>() {
        override fun doInBackground(vararg params: String?): List<Song> {
            val filteredList = allSongs.filter { song ->
                song.getTitle().toLowerCase(Locale.getDefault())
                        .contains(query.toLowerCase(Locale.getDefault())) }.toMutableList()
            when (AppPreferences.getInstance().songsSortType) {
                SortType.NAME -> filteredList.sortBy { song -> song.getTitle() }
                SortType.DATE -> filteredList.sortByDescending { song -> song.getDate() }
                else -> {}
            }
            return filteredList
        }

        override fun onPostExecute(result: List<Song>?) {
            if (result != null) {
                adapter.submitList(result)
            }
            filterTask = null
        }
    }

    private val mSearchQueryListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            if (!TextUtils.isEmpty(query)) {
                mSearchView.post { mSearchView.clearFocus() }
            }
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            query = newText.trim().toLowerCase(Locale.getDefault())
            executeFilterTask()
            return true
        }
    }
}