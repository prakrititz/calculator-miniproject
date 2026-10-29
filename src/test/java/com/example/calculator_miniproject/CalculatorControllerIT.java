package com.example.calculator_miniproject;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

// Integration tests: start the whole Spring app and send fake HTTP requests through it.
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CalculatorController - MockMvc integration tests")
class CalculatorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/add returns 200 and the correct JSON")
    void addEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2.0))
                .andExpect(jsonPath("$.b").value(3.0))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    @DisplayName("GET /api/v1/multiply returns 200 and the correct product")
    void multiplyEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "2.5").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", closeTo(10.0, 1e-9)));
    }

    @Test
    @DisplayName("GET /api/v1/divide with b=0 returns 400 and the error JSON")
    void divideByZeroReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "10").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cannot divide by zero"))
                .andExpect(jsonPath("$.path").value("/api/v1/divide"));
    }

    @Test
    @DisplayName("GET /api/v1/add with a missing param returns 400")
    void missingParamReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /actuator/health returns 200 and status UP")
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
