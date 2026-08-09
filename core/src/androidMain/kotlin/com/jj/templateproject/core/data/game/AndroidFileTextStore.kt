package com.jj.templateproject.core.data.game

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/** [FileTextStore] over `Context.filesDir` — private app storage, not shared/external storage. */
class AndroidFileTextStore(
    private val context: Context,
) : FileTextStore {

    override suspend fun writeText(fileName: String, content: String) {
        withContext(Dispatchers.IO) {
            file(fileName).writeText(content)
        }
    }

    // A read failure (a corrupted or half-written file) degrades to "nothing saved" by design —
    // see GameStateStorage's doc comment — so there is nothing further to do with readFailure here.
    @Suppress("SwallowedException")
    override suspend fun readText(fileName: String): String? =
        withContext(Dispatchers.IO) {
            val target = file(fileName)
            if (!target.exists()) return@withContext null
            try {
                target.readText()
            } catch (readFailure: IOException) {
                null
            }
        }

    override suspend fun delete(fileName: String) {
        withContext(Dispatchers.IO) {
            file(fileName).delete()
        }
    }

    private fun file(fileName: String) = File(context.filesDir, fileName)
}
