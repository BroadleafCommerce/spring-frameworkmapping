package org.broadleafcommerce.frameworkmapping.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping HTTP {@code DELETE} requests onto specific handler methods.
 *
 * <p>
 * Specifically, {@code @FrameworkDeleteMapping} is a <em>composed annotation</em> that acts as a
 * shortcut for {@code @FrameworkMapping(method = RequestMethod.DELETE)}.
 *
 * @author Samarth Dhruva (samarthd)
 * @see FrameworkGetMapping
 * @see FrameworkPostMapping
 * @see FrameworkPutMapping
 * @see FrameworkPatchMapping
 * @see FrameworkMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@FrameworkMapping(method = RequestMethod.DELETE)
public @interface FrameworkDeleteMapping {

    /**
     * Alias for {@link FrameworkMapping#name}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String name() default "";

    /**
     * Alias for {@link FrameworkMapping#value}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] value() default {};

    /**
     * Alias for {@link FrameworkMapping#path}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] path() default {};

    /**
     * Alias for {@link FrameworkMapping#params}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] params() default {};

    /**
     * Alias for {@link FrameworkMapping#headers}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] headers() default {};

    /**
     * Alias for {@link FrameworkMapping#consumes}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] consumes() default {};

    /**
     * Alias for {@link FrameworkMapping#produces}.
     */
    @AliasFor(annotation = FrameworkMapping.class)
    String[] produces() default {};

}
