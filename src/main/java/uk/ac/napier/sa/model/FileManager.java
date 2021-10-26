package uk.ac.napier.sa.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

final class FileManager implements RemoteFileManager {

    private volatile static FileManager instance;

    private FileManager() {
    }

    /**
     * A thread-safe way of instantiating the {@link FileManager} class.
     *
     * @return An instance of this class.
     */
    public synchronized static FileManager getInstance() {
        if (instance == null) {
            synchronized (FileManager.class) {
                if (instance == null) {
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    /**
     * Asynchronously read in a specified file and have it returned as a list of strings.
     *
     * @param p The path of the file
     * @return The file, expressed as a list of strings.
     * @throws ExecutionException   If the result retrieved from a task is aborted by a thrown exception.
     * @throws InterruptedException When the thread is occupied and interrupted either before or during a certain activity.
     */
    public List<String> read(Path p) throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            List<String> list = null;
            try {
                list = Files.readAllLines(p, Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        });

        return (future.join() != null ? future.get() : new ArrayList<>());
    }
}
