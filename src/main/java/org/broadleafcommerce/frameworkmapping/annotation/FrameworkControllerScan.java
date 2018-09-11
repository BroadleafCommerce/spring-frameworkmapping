package org.broadleafcommerce.frameworkmapping.annotation;

import org.broadleafcommerce.frameworkmapping.FrameworkControllerHandlerMapping;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * By default, scan the locations specified in {@link #value()} or {@link #basePackages()} or
 * {@link #basePackageClasses()} for {@link FrameworkRestController} and {@link FrameworkController}
 * so that their {@link FrameworkMapping FrameworkMappings} will get included in
 * {@link FrameworkControllerHandlerMapping} to provide default implementations of web endpoints.
 * <p>
 * If there is no need to scan for both {@link FrameworkController} and
 * {@link FrameworkRestController}, supply only one of them to {@link #includeFilters()}.
 * <p>
 * If only some specific controllers are desired, then use {@link #excludeFilters()} to disable
 * undesired default controllers.
 * <p>
 * <b>DO NOT place this annotation on the same class as another {@link ComponentScan} or other
 * annotations that compose {@link ComponentScan} such as {@link SpringBootApplication} as they will
 * conflict when Spring performs annotation composition.</b> Instead, you can create a nested class
 * in your {@link SpringBootApplication} class like this:
 *
 * <pre>
 * {@code
 * @SpringBootApplication public class MyApplication {
 *
 *     @FrameworkControllerScan(basePackages = "com.mypackage.packagewithcontrollers") public static class EnableBroadleafControllers {}
 *
 *     public static void main(String[] args) { SpringApplication.run(MyApplication.class, args); }
 *
 * }}
 * 
 * @author Samarth Dhruva (samarthd)
 * @author Philip Baggett (pbaggett)
 *
 * @see FrameworkRestController
 * @see FrameworkController
 * @see org.broadleafcommerce.frameworkmapping.FrameworkControllerHandlerMapping
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FrameworkControllerScanRegistrar.class)
public @interface FrameworkControllerScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    boolean useDefaultFilters() default true;

    /**
     * A set of {@link ComponentScan.Filter ComponentScan.Filters} that describe which type of framework controllers to
     * look for.
     *
     * By default, scans for both {@link FrameworkRestController FrameworkRestControllers} and
     * {@link FrameworkController FrameworkControllers}.
     *
     * @see ComponentScan#includeFilters()
     * @see ComponentScan.Filter
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * A set of {@link ComponentScan.Filter ComponentScan.Filters} that describe classes to exclude from component
     * scanning.
     * <p>
     * This is most useful when you want to enable some framework controllers but exclude others.
     * You can exclude classes annotated with {@link FrameworkController} or
     * {@link FrameworkRestController} by providing a filter like
     * {@code @FrameworkControllerScan(excludeFilters = @Filter(value = DefaultCustomerController.class, type =
     * FilterType.ASSIGNABLE_TYPE))}
     *
     * @see ComponentScan#excludeFilters()
     * @see ComponentScan.Filter
     */
    ComponentScan.Filter[] excludeFilters() default {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class)};
}
