package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HelloWorldConfiguration extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(HelloWorldConfiguration.class, args);
    }
}
