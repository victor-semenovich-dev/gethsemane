package by.geth.gethsemane.domain.usecase.base

interface BaseUseCase {
    suspend operator fun invoke(): Result<Unit>
}
