package com.jj.templateproject.core.data.game

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.stringWithContentsOfFile

/** [FileTextStore] over the app's `Documents` directory (`NSFileManager` + `NSSearchPathForDirectoriesInDomains`). */
@OptIn(ExperimentalForeignApi::class)
class IosFileTextStore : FileTextStore {

    private val fileManager = NSFileManager.defaultManager

    private val documentsPath: String by lazy {
        NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .first() as String
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override suspend fun writeText(fileName: String, content: String) {
        withContext(Dispatchers.Default) {
            // The compiler warns this cast can never succeed because kotlin.String and NSString are
            // not related by Kotlin's own class hierarchy, but Kotlin/Native bridges them as the same
            // runtime object — the cast is how framework-only NSString extensions (dataUsingEncoding)
            // become callable, and it always succeeds in practice.
            val data = (content as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            fileManager.createFileAtPath(pathFor(fileName), data, null)
        }
    }

    override suspend fun readText(fileName: String): String? =
        withContext(Dispatchers.Default) {
            NSString.stringWithContentsOfFile(pathFor(fileName), NSUTF8StringEncoding, null)
        }

    override suspend fun delete(fileName: String) {
        withContext(Dispatchers.Default) {
            fileManager.removeItemAtPath(pathFor(fileName), null)
        }
    }

    private fun pathFor(fileName: String) = "$documentsPath/$fileName"
}
