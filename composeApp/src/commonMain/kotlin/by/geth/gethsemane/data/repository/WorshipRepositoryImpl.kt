package by.geth.gethsemane.data.repository

import by.geth.gethsemane.data.source.local.db.dao.EventsDao
import by.geth.gethsemane.data.source.local.db.dao.PhotosDao
import by.geth.gethsemane.data.source.local.db.dao.SongsDao
import by.geth.gethsemane.data.source.local.db.dao.SpeechDao
import by.geth.gethsemane.data.source.local.db.model.EventEntity
import by.geth.gethsemane.data.source.local.db.model.PhotoEntity
import by.geth.gethsemane.data.source.local.db.model.SongEntity
import by.geth.gethsemane.data.source.local.db.model.SpeechEntity
import by.geth.gethsemane.data.source.remote.model.WorshipDTO
import by.geth.gethsemane.data.source.remote.service.WorshipService
import by.geth.gethsemane.domain.model.Event
import by.geth.gethsemane.domain.model.Worship
import by.geth.gethsemane.domain.repository.AuthorsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.repository.WorshipRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

class WorshipRepositoryImpl(
    private val worshipService: WorshipService,
    private val authorsRepository: AuthorsRepository,
    private val musicGroupsRepository: MusicGroupsRepository,
    private val eventsDao: EventsDao,
    private val speechDao: SpeechDao,
    private val songsDao: SongsDao,
    private val photosDao: PhotosDao,
): WorshipRepository {
    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd HH:mm")
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateFormat = LocalDate.Format {
        byUnicodePattern("yyyy-MM-dd")
    }

    override fun getWorship(id: Int): Flow<Worship> {
        TODO("Not yet implemented")
    }

    override suspend fun loadWorship(id: Int): Result<Unit> = coroutineScope {
        // TODO cover with unit tests
        worshipService.getWorship(id).onSuccess { worshipDTO ->
            val oldEntity = eventsDao.getById(id).first()
            val entity = worshipDTO.toDbEntity().copy(
                note = oldEntity?.note,
                audioLocal = oldEntity?.audioLocal,
                isDraft = oldEntity?.isDraft ?: false,
                isArchive = oldEntity?.isArchive ?: false,
                musicGroupId = oldEntity?.musicGroupId,
            )
            eventsDao.insertOrUpdate(entity)

            val unknownDataTasks = mutableListOf<Deferred<*>>()

            val speechesList = mutableListOf<SpeechEntity>()
            speechesList.addAll(worshipDTO.sermons.map { sermonDTO ->
                val oldSermonEntity = speechDao.getById(sermonDTO.id).first()
                sermonDTO.toDbEntity().copy(
                    eventId = id,
                    author = oldSermonEntity?.author,
                    date = oldSermonEntity?.date,
                    audioLocal = oldSermonEntity?.audioLocal,
                    category = SpeechEntity.Category.SERMON,
                    showInMedia = oldSermonEntity?.showInMedia ?: false,
                )
            })
            speechesList.addAll(worshipDTO.witnesses.map { sermonDTO ->
                val oldSermonEntity = speechDao.getById(sermonDTO.id).first()
                sermonDTO.toDbEntity().copy(
                    eventId = id,
                    author = oldSermonEntity?.author,
                    date = oldSermonEntity?.date,
                    audioLocal = oldSermonEntity?.audioLocal,
                    category = SpeechEntity.Category.WITNESS,
                    showInMedia = oldSermonEntity?.showInMedia ?: false,
                )
            })
            speechesList.forEach {
                if (authorsRepository.getAuthor(it.authorId).first() == null) {
                    unknownDataTasks.add(async {
                        authorsRepository.loadAuthor(it.authorId)
                    })
                }
            }
            speechDao.replaceForEvent(id, speechesList)

            val songList = worshipDTO.songs.map { songDTO ->
                val oldSongEntity = songsDao.getById(songDTO.id).first()
                songDTO.toDbEntity().copy(
                    eventId = id,
                    date = oldSongEntity?.date,
                    audioLocal = oldSongEntity?.audioLocal,
                    showInMedia = oldSongEntity?.showInMedia ?: false,
                )
            }
            songList.forEach {
                if (musicGroupsRepository.getMusicGroup(it.musicGroupId).first() == null) {
                    unknownDataTasks.add(async {
                        musicGroupsRepository.loadMusicGroup(it.musicGroupId)
                    })
                }
            }
            songsDao.replaceForEvent(id, songList)

            val photosList = worshipDTO.photos.map { photoDTO ->
                val oldPhotoEntity = photosDao.getById(photoDTO.id).first()
                photoDTO.toDbEntity().copy(
                    eventId = oldPhotoEntity?.eventId,
                    albumId = oldPhotoEntity?.albumId,
                    showInLastPhotos = oldPhotoEntity?.showInLastPhotos ?: false,
                )
            }
            photosDao.replaceForEvent(id, photosList)

            unknownDataTasks.awaitAll()
        }.map { }
    }

    private fun WorshipDTO.toDbEntity() = EventEntity(
        id = this.id,
        categoryId = Event.WORSHIP_CATEGORY_ID,
        title = this.title,
        date = dateTimeFormat.parse(this.date),
        note = null, // missing
        audioRemote = this.audio,
        audioLocal = null, // missing
        shortDesc = this.shortDesc,
        isDraft = false, // missing
        isArchive = false, // missing
        musicGroupId = null, // missing
        video = this.video,
        poster = this.poster
    )

    private fun WorshipDTO.SpeechDTO.toDbEntity() = SpeechEntity(
        id = this.id,
        authorId = this.authorId,
        eventId = null, // missing
        title = this.title,
        author = null, // missing
        date = null, // missing
        audioRemote = this.audio,
        audioLocal = null, // missing
        category = SpeechEntity.Category.SERMON, // missing
        showInMedia = false, // missing
    )

    private fun WorshipDTO.SongDTO.toDbEntity() = SongEntity(
        id = this.id,
        musicGroupId = this.musicGroupId,
        eventId = null, // missing
        title = this.title,
        date = null, // missing
        audioRemote = this.audio,
        audioLocal = null, // missing
        showInMedia = false, // missing
    )

    private fun WorshipDTO.PhotoDTO.toDbEntity() = PhotoEntity(
        id = this.id,
        eventId = null, // missing
        albumId = null, // missing
        title = this.title,
        preview = this.preview,
        photo = this.photo,
        date = dateFormat.parse(this.date),
        showInLastPhotos = false, // missing
    )
}
