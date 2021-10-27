package uk.ac.napier.sa.model

import java.nio.file.Path
import java.util.concurrent.ExecutionException

interface RemoteFileManager {

    @Throws(ExecutionException::class, InterruptedException::class)
    fun read(p: Path?): List<String?>? {
        return null
    }
}