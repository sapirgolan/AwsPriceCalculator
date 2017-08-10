package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by i062070 on 27/07/2017.
 * here Spring Boot is only instantiating the web layer, not the whole context.
 * Other beans are not loaded and should be mocked using @MockBean
 */

@RunWith(SpringRunner.class)
//@WebMvcTest(HelloWorldController.class)
@WebMvcTest()
public class HelloWorldControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GreeterService greeterService;

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        mockMvc.perform(get("/hellow-world")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello, Jhon Doe!")));
    }

}