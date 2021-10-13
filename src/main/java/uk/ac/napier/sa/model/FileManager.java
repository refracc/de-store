package uk.ac.napier.sa.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class FileManager {

    private volatile static FileManager instance;

    private FileManager() {
    }

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

        return (future.get() != null ? future.get() : new ArrayList<>());
    }
}
