package org.se.mac.blorksandbox.jobqueue.job;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnqueableJob {

    String title() default "";
}