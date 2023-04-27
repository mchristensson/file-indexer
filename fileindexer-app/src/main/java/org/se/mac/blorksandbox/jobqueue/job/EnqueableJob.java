package org.se.mac.blorksandbox.jobqueue.job;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class implementation description for a task type.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnqueableJob {

    /**
     * Represents the static name of the task type.
     *
     * @return Title of the task type
     */
    String title() default "";
}