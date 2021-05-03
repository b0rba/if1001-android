package br.cin.ufpe.if1001.taskmanager.utils

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//not used on new implementation but i'm proud of this logic, so i'm leaving this in.
class CallbackUtil {
    companion object {
        suspend fun <T> awaitCallback(block: (Callback<T>) -> Unit): T =
            suspendCancellableCoroutine { cont ->
                block(object : Callback<T> {
                    override fun onComplete(result: T) = cont.resume(result)
                    override fun onException(e: Exception?) {
                        e?.let { cont.resumeWithException(it) }
                    }
                })
            }
    }

    interface Callback<T> {
        fun onComplete(result: T)
        fun onException(e: Exception?) {}
    }
}