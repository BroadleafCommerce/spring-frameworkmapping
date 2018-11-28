# Spring Frameworkmappings

This library is a support library for frameworks that want to ship a set of out-of-the-box Spring request mappings but still allow their users to turn endpoints off, override them, extend them, etc with their own `@RequestMapping` annotation within their library.

## Usage

Use `@FrameworkController` and the corresponding `@FrameworkMapping` just like you would an `@Controller` and `@RequestMapping`. Example:

```java
@FrameworkController
public void DefaultedController {

    @RequestMapping("/test")
    public String getAString() {
        return "path/to/template";
    }
}
```

Then activate the controllers just like you would an `@ComponentScan`, but with `@FrameworkControllerScan`

```java
@Configuration
@FrameworkControllerScan(basePackageClasses = DefaultedController.class)
public void ControllerConfig {
}
```

This scan cannot be in your `@SpringBootApplication` class, otherwise it will overwrite your global `@ComponentScan`. If you are using this in your main `@SpringBootApplication`, then use it like so:

```java
@SpringBootApplication
public class MyApplication {

    @Configuration
    @FrameworkControllerScan(basePackageClasses = MyApplication.class)
    public void FrameworkControllerConfiguration {
    }
}
```

## Convenience Annotations

Convenience annotations also exist for specific request methods:

```java
@FrameworkController
@RequestMapping("/test")
public void DefaultedController {

    @GetMapping("/get")
    public @ResponseBody String getAString() {
        return "Success!";
    }
}
```

Or as a correlary to `@RestController`:

```java
@FrameworkRestController
@RequestMapping("/test")
public void DefaultedController {

    @GetMapping("/get")
    public @ResponseBody String getAString() {
        return "Success!";
    }
}
```

## Test Support

Since the use case for this library is for distributing other libraries, make sure that you have a `spring.factories` entry that corresponds to the `@AutoConfigureWebMvc` test slice:

```ini
org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc=\
    com.mycompany.mylibrary.package.MyControllerConfig
```

However, if you use `@WebMvcTest`, since it is not a default Spring filter, the controllers will not be available in the applicationContext.

If you want to enable a single controller, use the `controllers` attribute of `@WebMvcTest`:

```java
@WebMvcTest(controllers = TestController.class)
@ExtendWith(SpringExtension.class)
public class ControllerTest {

    @Configuration
    class Config {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @FrameworkController
    public class TestController {

        @GetMapping("/test")
        public String test() {
            return "Success!";
        }
    }
        
    @Autowired
    MockMvc mockMvc;

    @Test
    public void controllersWork() throws Exception {
        mockMvc.perform(get("/test"))
            .andExpect(status().isOk());
    }

}
```

If you want to enable a group of `@FrameworkMapping`-annotated controllers use `includeFilters`:

```java
@WebMvcTest(includeFilters = @Filter(FrameworkController.class))
@FrameworkControllerScan
@ExtendWith(SpringExtension.class)
public class ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void controllersWork() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }

}

@FrameworkController
public class TestController {

  @GetMapping("/test")
  public String test() {
  return "Success!";
  }
}
```

> Note the at this time, `@FrameworkController`s that are _scanned_ using `@FrameworkControllerScan` within a test class will not be picked up. This is because of the exclusions within `TestTypeExcludeFilter`. Remediations are to either move your `@FrameworkController` to a class outside of a test class, or manually create it with `@Bean`.

## Framework Controllers

### Overview

We have introduced the concept of framework controllers that are default implementations of endpoints. This is a replacement for our current pattern where "default controllers" are included in the framework but aren't annotated as such, requiring that the client application extent them and add the `@RequestMapping` annotations.

The idea is that you can have our default endpoints out of the box without any code requirements in the client application.

The thing that makes framework controllers unique is that if you specify your own custom endpoint that has the same URL mapping as a framework endpoint, you will not get an ambiguous mapping exception.

### Enabling Framework Controllers

By default, the framework controllers will not be registered unless a `@FrameworkControllerScan` is specified in a `@Configuration` class to scan the package(s) the controllers are in. 

#### Important Caveat

**These annotations all leverage annotation composition of `@ComponentScan` and as such cannot be specified alongside a class that is annotated with another `@ComponentScan` or an annotation that is composed of `@ComponentScan` such as `@SpringBootApplication`.** Instead, created a nested class inside of that class:

```java
@SpringBootApplication
public class MyApplication {
    
    @FrameworkControllerScan(basePackages = "com.mypackage.packagewithcontrollers")
    public static class EnableFrameworkControllers {}
    
    public static void main(String[] args) { SpringApplication.run(MyApplication.class, args); }
}
```

#### Excluding Controllers

In the event you want to enable framework controllers, but want to exclude particular framework controllers, you can leverage the `excludeFilters` property of the `@FrameworkControllerScan`. For example:

```java
@FrameworkControllerScan(basePackages = "com.mypackage.packagewithcontrollers",
                         excludeFilters = {
                             @Filter(value = DefaultCustomerController.class, type = FilterType.ASSIGNABLE_TYPE),
                             @Filter(value = DefaultOrderController.class, type = FilterType.ASSIGNABLE_TYPE)
                         })
```

#### Including a Single Controller

If you only want a small number of framework controllers enabled, it would be easier to declare the ones you want as beans instead of listing a large number of controllers using `excludeFilters`.

For example, you can activate a single framework controller in an `@Configuration` class like so:

```java
@Bean
public DefaultCartController defaultCartController() {
    return new DefaultCartController();
}
```

Alternatively, you may utilize `includeFilters` of `@FrameworkControllerScan` and override its value to include just a few controllers:

```java
@FrameworkControllerScan(basePackages = "com.mypackage.packagewithcontrollers",
                         includeFilters = {
                             @Filter(value = DefaultCustomerController.class, type = FilterType.ASSIGNABLE_TYPE),
                             @Filter(value = DefaultOrderController.class, type = FilterType.ASSIGNABLE_TYPE)
                         })
```




### Framework Controllers

Default framework controllers are defined by their `@FrameworkController` or `@FrameworkRestController` annotation. There are used exactly like their counterparts `@Controller` and `@RestController`. It is important to use the proper annotation for the situation, `@FrameworkController` for MVC controllers and `@FrameworkRestController` for RESTful controllers, since the above annotations that enable them leverage this distinction. For example, you would not want to create a RESTful controller by annotating it with `@FrameworkController` and `@ResponseBody`. `@FrameworkRestController` already has a composition with `@ResponseBody` so all mappings defined within will infer `@ResponseBody`.

### Framework Mappings

In order to avoid conflict with the way Spring reads `@RequestMapping` annotations, we have also created an `@FrameworkMapping` annotation. Without it, framework controllers with a parent mapping would get included as regular controllers and classes that extend default framework controllers but don't want their mappings would get them anyway.

Otherwise, `@FrameworkMapping` behaves exactly like `@RequestMapping`, including all of the same properties. You can put `@FrameworkMapping` at the class level to declare the parent mapping and at the method level to declare the specific mapping.

### Use Cases

#### Using Framework Controller Annotations

When you use `@FrameworkControllerScan`, you are declaring that for the most part, you want and are satisfied with the default mappings/API in those particular packages/classes. That being said, there is still room for some customization on top of the defaults.

##### Overriding Default Functionality

To override the functionality for a particular mapping, you can simply create your own regular controller class with the same mapping as what is in the framework controller and define your functionality there. This class could be stand-alone or could extend the default framework controller if you would like to call `super` at some point.

###### Important Note

**As of right now, you may only extend framework controllers that are not registered - meaning any controller which has been picked up in an `@FrameworkControllerScan` should *not* be extended. If you would like to extend a framework controller, ensure it is excluded from `@FrameworkControllerScan` to avoid ambiguous mapping issues.**

Since regular controllers take precedence over framework controller mappings, your mapping will get used when a request comes in.

For example, given the framework controller:

```java
@FrameworkRestController
@FrameworkMapping("/cart")
public class DefaultCartController {
    @FrameworkMapping(path = "/get", method = RequestMethod.GET)
    public Cart getActiveCart() {
        return cartService.getActiveCart();
    }
}
```

You can hijack the `/cart/get` mapping with:

```java
@RestController
@RequestMapping("/cart")
public class MyCartController {
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public MyCart getActiveCart() {
        return myCartService.getActiveCart();
    }
}
```

Or if you want to call super, you could extend the default framework controller as well like so:

```java
@RestController
@RequestMapping("/cart")
public class MyCartController extends DefaultCartController {
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public MyCart getActiveCart() {
        Cart cart = super.getActiveCart();
        return doCustomThingsToCart(cart);
    }
}
```

##### Changing a Mapping

If you want to alter the URL for some mapping, you can do so by defining your own mapping and calling super.

For example, given the framework controller:

```java
@FrameworkRestController
@FrameworkMapping("/cart")
public class DefaultCartController {
    @FrameworkMapping(path = "/get", method = RequestMethod.GET)
    public Cart getActiveCart() {
        return cartService.getActiveCart();
    }
}
```

You can change the mapping by extending the framework controller, and calling super with a new mapping:

```java
@RestController
@RequestMapping("/cart")
public class MyCartController extends DefaultCartController {
    @RequestMapping(path = "/retrieve", method = RequestMethod.GET)
    public Cart getActiveCart() {
        return super.getActiveCart();
    }
}
```

Now we've created a new mapping `/cart/retrieve`, but note that `/cart/get` will still be registered.

##### Changing a Mapping and Functionality

This is achieved by simply applying both patterns above.

##### Removing a Mapping

If you want to remove all of the mappings for a given framework controller, see the section `Excluding Controllers`.

If you want to remove (disable) particular a `@FrameworkMapping` then you'll need to create a `@RequestMapping` method with the same URL that returns a 404 error.

For example, to disable `/cart/get`:

```java
@RestController
@RequestMapping("/cart")
public class MyCartController extends DefaultCartController {
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public ResponseEntity getActiveCart() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
```

Please note that if you find yourself using this pattern a lot, then maybe you should consider excluding that framework controller (see `Excluding Controllers`) or not using enable framework controllers at all (see `Not Using Framework Controller Annotations`). The primary purpose of these default framework controllers is to provide an out of the box experience that can be tweaked. If your application has heavily customized mappings/API, then it would probably be best to simply write your own mappings/API, extending the default framework controllers where desired.

#### *Not* Using Framework Controller Annotations

This is the same pattern as before framework controllers were introduced. You basically do not specify any `@FrameworkControllerScan`, and provide your own `@RequestMappings` for the application. When no `@FrameworkControllerScan` is specified, the `@FrameworkController`, `@FrameworkRestController`, and `@FrameworkMapping` annotations are completely ignored and those classes aren't even registered as beans.

##### Use Existing Functionality

You can still leverage the functionality of the default framework controllers by extending them and calling `super`.

For example:

```java
@RestController
@RequestMapping("/cart")
public class MyCartController extends DefaultCartController {
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public Cart getActiveCart() {
        return super.getActiveCart();
    }
}
```

Of course you can add some additional code to customize this further, or not even call `super` at all.

## Handler Mapping Classes

These are the relevant handler mapping implementations listed in order of precedence for site applications.

`RequestMappingHandlerMapping`

> This is the default handler mapping implementation that comes from spring. Controllers are registered in this handler mapping when the class includes a `@Controller` or `@RequestMapping` annotation.

`FrameworkControllerHandlerMapping`

> This is the handler mapping that registers controllers annotated with `@FrameworkController` and `@FrameworkRestController`.

## URI Builder
This class has been copied from `MvcUriComponentsBuilder` in `spring-webmvc:5.0.8-RELEASE` in order to provide URI building functionality for `@FrameworkMapping` annotations. It replicates the functionality of `MvcUriComponentsBuilder`.
