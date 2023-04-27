package org.se.mac.blorksandbox.analyzer.task;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Task for analysizing/processing a file.
 *
 * @param <R> Output type
 */
public interface FileAnalyzerTask<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(Path t) throws Exception;

    void setDoAfter(Consumer<String> filePathConsumer);
}
