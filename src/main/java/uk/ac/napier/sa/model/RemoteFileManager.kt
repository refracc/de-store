package uk.ac.napier.sa.model

import kotlin.Throws
import java.util.concurrent.ExecutionException
import java.lang.InterruptedException
import java.nio.file.Path

interface RemoteFileManager {
    @Throws(ExecutionException::class, InterruptedException::class)
    fun read(p: Path?): List<String?>? {
        return null
    }
}