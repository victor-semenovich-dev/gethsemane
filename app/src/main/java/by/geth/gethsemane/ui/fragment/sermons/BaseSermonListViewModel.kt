package by.geth.gethsemane.ui.fragment.sermons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.api.Server
import by.geth.gethsemane.api.response.SermonResponse
import by.geth.gethsemane.data.base.AudioItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseSermonListViewModel<T: AudioItem>: ViewModel() {
    companion object {
        const val PAGE_SIZE = 30
    }

    abstract val sermonCategory: Int

    val itemList: LiveData<List<T>>
        get() = _itemList
    protected val _itemList = MutableLiveData<List<T>>().apply { value = listOf() }

    val isInProgress: LiveData<Boolean>
        get() = _isInProgress
    private val _isInProgress = MutableLiveData<Boolean>().apply { value = false }

    val eventsData: LiveData<AudioListEvent?>
        get() = _eventsData
    private val _eventsData = MutableLiveData<AudioListEvent?>()

    private val _pagesLoaded = AtomicInteger(0)
    private val _pageLoading = AtomicInteger(0)

    private val _mutex = Mutex()

    protected var _query = ""

    fun consumeEvent() {
        _eventsData.value = null
    }

    fun fetchData(showMessageOnError: Boolean = false, query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            _mutex.withLock {
                try {
                    _isInProgress.postValue(true)
                    _query = query
                    if (_query.isEmpty()) {
                        loadFromDatabase()
                    }
                    _pagesLoaded.set(0)
                    loadFromServer()
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (showMessageOnError) {
                        _eventsData.postValue(AudioListEvent.LOAD_ERROR)
                    }
                } finally {
                    _isInProgress.postValue(false)
                }
            }
        }
    }

    fun loadNextPage() {
        val nextPage = (itemList.value?.size ?: 0) / PAGE_SIZE + 1
        if (nextPage > _pagesLoaded.get() && nextPage != _pageLoading.get()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _isInProgress.postValue(true)
                    loadFromServer(page = nextPage)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isInProgress.postValue(false)
                }
            }
        }
    }

    abstract suspend fun loadFromDatabase()

    suspend fun loadFromServer(page: Int = 1) {
        try {
            _pageLoading.set(page)
            val responseList = Server.api.getSermons(sermonCategory, page, PAGE_SIZE, _query)
            if (page == 1) {
                clearDatabase()
            }
            responseList.forEach {
                applyResponse(it)
            }
            loadFromDatabase()
            _pagesLoaded.set(page)
        } finally {
            _pageLoading.set(0)
        }
    }

    abstract suspend fun clearDatabase()

    abstract suspend fun applyResponse(response: SermonResponse)
}

enum class AudioListEvent { LOAD_ERROR }
