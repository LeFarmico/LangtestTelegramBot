package state

sealed class DataState<out T> {

    /**
     * When nothing found or answer is No.
     */
    object Empty : DataState<Nothing>()

    /**
     * Success data object.
     */
    data class Success<out T>(val data: T) : DataState<T>()

    /**
     * Only if exception necessary.
     */
    data class Failure(val exception: Exception) : DataState<Nothing>()
}
