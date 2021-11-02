package uk.ac.napier.sa.model;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

public sealed interface RemoteFileManager permits FileManager {

    default List<String> read(Path p) throws ExecutionException, InterruptedException {
        return null;
    }
}
