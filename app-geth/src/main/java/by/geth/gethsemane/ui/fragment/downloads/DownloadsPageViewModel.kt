package by.geth.gethsemane.ui.fragment.downloads

import androidx.lifecycle.*
import by.geth.gethsemane.data.Sermon
import by.geth.gethsemane.data.Song
import by.geth.gethsemane.data.Witness
import by.geth.gethsemane.data.base.AudioItem
import com.activeandroid.query.Select
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadsPageViewModel(val type: DownloadsPageFragment.DownloadsType): ViewModel() {
    private val _audioItems = MutableLiveData<List<AudioItem>>()
    val audioItems: LiveData<List<AudioItem>>
        get() = _audioItems

    private val _isInProgress = MutableLiveData<Boolean>()
    val isInProgress: LiveData<Boolean>
        get() = _isInProgress

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isInProgress.postValue(true)

            when (type) {
                DownloadsPageFragment.DownloadsType.SERMON -> {
                    val sermons: List<Sermon> = Select()
                        .from(Sermon::class.java)
                        .where("${Sermon.COLUMN_AUDIO_LOCAL} IS NOT NULL")
                        .execute()
                    _audioItems.postValue(sermons)
                }
                DownloadsPageFragment.DownloadsType.WITNESS -> {
                    val witnesses: List<Witness> = Select()
                        .from(Witness::class.java)
                        .where("${Witness.COLUMN_AUDIO_LOCAL} IS NOT NULL")
                        .execute()
                    _audioItems.postValue(witnesses)
                }
                DownloadsPageFragment.DownloadsType.SONG -> {
                    val songs: List<Witness> = Select()
                        .from(Song::class.java)
                        .where("${Song.COLUMN_AUDIO_LOCAL} IS NOT NULL")
                        .execute()
                    _audioItems.postValue(songs)
                }
            }

            _isInProgress.postValue(false)
        }
    }
}

class DownloadsPageViewModelFactory(val type: DownloadsPageFragment.DownloadsType): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadsPageViewModel::class.java)) {
            return DownloadsPageViewModel(type) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
