package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by i062070 on 27/07/2017.
 */

@RestController
@RequestMapping("/hellow-world")
public class HelloWorldController {

    @Autowired
    private GreeterService greeterService;
    private final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Greeting greeting(
            @RequestParam(value = "name", required = false, defaultValue = "Jhon Doe") String name) {
        logger.info("Asked to greet user");
        return greeterService.greet(name);
    }
}
