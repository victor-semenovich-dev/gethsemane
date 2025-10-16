package by.geth.gethsemane.ui.fragment.worship

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import by.geth.gethsemane.data.Worship
import by.geth.gethsemane.domain.model.Author
import by.geth.gethsemane.domain.repository.AuthorsRepository
import by.geth.gethsemane.util.DBUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorshipViewModel(
    private val authorsRepository: AuthorsRepository,
): ViewModel() {
    data class DataState(
        val worship: Worship? = null,
        val authors: Map<Long, Author?> = emptyMap(),
    )

//    private val _eventLiveData = MutableLiveData<OneTimeEvent?>()

    private val _dataStateFlow = MutableStateFlow(DataState())
    val worship: Worship?
        get() = _dataStateFlow.value.worship
    val authors: Map<Long, Author?>
        get() = _dataStateFlow.value.authors

    fun getWorship(worshipId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val worship: Worship? = DBUtils.getWorship(worshipId)
            _dataStateFlow.emit(DataState(worship, emptyMap()))

            val authors = mutableMapOf<Long, Author?>()

            // get authors from local data
            val authorIdsSet = mutableSetOf<Long>()
            authorIdsSet.addAll(
                worship?.sermonList?.map { it.authorId.toLong() } ?: emptyList())
            authorIdsSet.addAll(
                worship?.witnessList?.map { it.authorId.toLong() } ?: emptyList())

            authorIdsSet.forEach { authorId ->
                authors[authorId] = authorsRepository.getSingleAuthor(authorId)
            }
            _dataStateFlow.emit(DataState(worship, authors.toMap()))

            // load missing authors from remote
            val missingAuthorIds = authors.filter { it.value == null }.keys
            missingAuthorIds.map { authorId ->
                async {
                    authorsRepository.loadSingleAuthor(authorId).onSuccess { author ->
                        authors[authorId] = author
                    }
                }
            }.awaitAll()
            _dataStateFlow.emit(DataState(worship, authors.toMap()))
        }
    }

    fun listenDataState(lifecycleOwner: LifecycleOwner, processState: (DataState) -> Unit) {
        _dataStateFlow.asLiveData().observe(lifecycleOwner) { dataState ->
            processState(dataState)
        }
    }

//    fun listenOneTimeEvents(lifecycleOwner: LifecycleOwner, processEvent: (OneTimeEvent) -> Unit) {
//        _eventLiveData.observe(lifecycleOwner) { event ->
//            event?.let { processEvent(it) }
//            _eventLiveData.value = null
//        }
//    }
}
