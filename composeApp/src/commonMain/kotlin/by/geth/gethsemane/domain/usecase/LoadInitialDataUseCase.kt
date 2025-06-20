package by.geth.gethsemane.domain.usecase

import by.geth.gethsemane.domain.repository.AuthorsRepository
import by.geth.gethsemane.domain.repository.MusicGroupsRepository
import by.geth.gethsemane.domain.usecase.base.BaseUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoadInitialDataUseCase(
    private val musicGroupsRepository: MusicGroupsRepository,
    private val authorsRepository: AuthorsRepository,
): BaseUseCase {
    override suspend fun invoke(): Result<Unit> = coroutineScope {
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
