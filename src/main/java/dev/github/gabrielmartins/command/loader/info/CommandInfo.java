package dev.github.gabrielmartins.command.loader.info;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String[] names();
    String[] permission() default {};
    String method() default "execute";
}
