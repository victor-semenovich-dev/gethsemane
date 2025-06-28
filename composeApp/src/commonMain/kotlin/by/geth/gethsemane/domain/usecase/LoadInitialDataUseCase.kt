package by.geth.gethsemane.domain.usecase

import by.geth.gethsemane.domain.repository.AuthorsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoadInitialDataUseCase(
    private val musicGroupsRepository: MusicGroupsRepository,
    private val authorsRepository: AuthorsRepository,
) {
    suspend operator fun invoke(): Result<Unit> = coroutineScope {
        val loadMusicGroups = async {
            musicGroupsRepository.loadAllMusicGroups()
        }
        val loadAuthors = async {
            authorsRepository.loadAllAuthors()
        }
        loadMusicGroups.await()
        loadAuthors.await()
        Result.success(Unit)
    }
}
