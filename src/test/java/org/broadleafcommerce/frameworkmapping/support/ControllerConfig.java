/**
 *
 */
package org.broadleafcommerce.frameworkmapping.support;

import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

/**
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@TestConfiguration
@ComponentScan(excludeFilters = {
        @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class)})
public class ControllerConfig { }
