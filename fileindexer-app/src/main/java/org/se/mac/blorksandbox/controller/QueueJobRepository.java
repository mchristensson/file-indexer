package org.se.mac.blorksandbox.controller;

import org.se.mac.blorksandbox.jobqueue.job.EnqueableJob;
import org.se.mac.blorksandbox.spi.QueuedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Library of all available jobs.
 * The library is pre-loaded at initialization of this {@link Component} at startup.
 *
 * <p>
 * The job implementation must fulfill the following in our to be available:
 * <ul>
 * <li>Listed in {@code /resources/META-INF/services/org.se.mac.blorksandbox.spi.QueuedJob}</li>
 * <li>Implementing {@link QueuedJob}</li>
 * <li>Annotated with {@link EnqueableJob}</li>
 * </ul>
 */
@Component
public class QueueJobRepository {

    private static final Logger logger = LoggerFactory.getLogger(QueueJobRepository.class);

    private ServiceLoader<QueuedJob> loader;

    public QueueJobRepository() {
        this.loader = ServiceLoader.load(QueuedJob.class);
    }

    /**
     * Looks up an implementation class from its annotated ({@code EnqueableJob.class}) title-value.
     *
     * @param title Title of job-definition to locate
     * @return Class corresponding to the job-definition's title
     */
    @Cacheable(value = "jobqueueimpl")
    public Optional<? extends Class<? extends QueuedJob>> lookupByTitle(@Validated String title) {
        logger.trace("LookupByTitle... [title={}]", title);
        if (Objects.isNull(title)) {
            return Optional.empty();
        }
        return this.loader.stream()
                .filter(queuedJob -> {
                    EnqueableJob an = queuedJob.type().getAnnotation(EnqueableJob.class);
                    return (an != null && an.title().equalsIgnoreCase(title));
                })
                .map(ServiceLoader.Provider::type)
                .findFirst();
    }

    /**
     * Finds the title of each defined classes implementing {@code QueuedJob}.
     *
     * @return The {@code title} of All defined classes implementing {@code QueuedJob}.
     */
    @Cacheable(value = "jobqueueimpl")
    public List<String> findAllTitles() {
        logger.trace("findAll...");
        return this.loader.stream()
                .map(queuedJob -> {
                    EnqueableJob an = queuedJob.type().getAnnotation(EnqueableJob.class);
                    return (an != null && !"".equals(an.title())) ? an.title() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}


