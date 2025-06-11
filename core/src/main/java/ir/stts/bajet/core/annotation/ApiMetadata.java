package ir.stts.bajet.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiMetadata {
    boolean authenticated() default true;

    boolean gatewayBypass() default false;

    boolean encrypted() default false;
}
