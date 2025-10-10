package com.arthurrouelle.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  void hello_returns_message() throws Exception {
    mvc.perform(get("/hello"))
       .andExpect(status().isOk())
       .andExpect(content().string(Matchers.containsString("Hello, World!")));
  }
}
