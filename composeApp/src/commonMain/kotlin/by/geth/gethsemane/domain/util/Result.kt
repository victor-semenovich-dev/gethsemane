package by.geth.gethsemane.domain.util

inline fun <R, T> Result<T>.chain(transform: (value: T) -> Result<R>): Result<R> {
    if (this.isSuccess) {
        val value = this.getOrThrow()
        return transform(value)
    } else {
        val throwable = this.exceptionOrNull()!!
        return Result.failure(throwable)
    }
}
