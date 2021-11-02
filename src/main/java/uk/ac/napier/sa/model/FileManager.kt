package uk.ac.napier.sa.model

import kotlin.Throws
import java.util.concurrent.ExecutionException
import java.lang.InterruptedException
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.nio.file.Files
import java.nio.charset.Charset
import java.io.IOException
import java.util.ArrayList
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized

internal class FileManager private constructor() : RemoteFileManager {
    /**
     * Asynchronously read in a specified file and have it returned as a list of strings.
     *
     * @param p The path of the file
     * @return The file, expressed as a list of strings.
     * @throws ExecutionException   If the result retrieved from a task is aborted by a thrown exception.
     * @throws InterruptedException When the thread is occupied and interrupted either before or during a certain activity.
     */
    @Throws(ExecutionException::class, InterruptedException::class)
    override fun read(p: Path?): List<String?>? {
        val future = CompletableFuture.supplyAsync {
            var list: List<String?>? = null
            try {
                list = Files.readAllLines(p, Charset.defaultCharset())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            list
        }
        return if (future.join() != null) future.get() else ArrayList()
    }

    companion object {
        /**
         * A thread-safe way of instantiating the [FileManager] class.
         *
         * @return An instance of this class.
         */
        @JvmStatic
        @get:Synchronized
        @Volatile
        var instance: FileManager? = null
            get() {
                if (field == null) {
                    synchronized(FileManager::class.java) {
                        if (field == null) {
                            field = FileManager()
                        }
                    }
                }
                return field
            }
            private set
    }
}