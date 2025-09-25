package com.femcoders.sitme.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
public class ApiSuccessResponseTestHelper {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public <T> ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder,
                                            T requestBody,
                                            String expectedMessage,
                                            HttpStatus expectedStatus) throws Exception {
        if (requestBody != null){
            String json = objectMapper.writeValueAsString(requestBody);
            requestBuilder.contentType(MediaType.APPLICATION_JSON).content(json);
        }

        return mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.data").exists());
    }

    public <T> ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder,
                                            T requestBody,
                                            String expectedMessage) throws Exception {

        return performRequest(requestBuilder, requestBody, expectedMessage, HttpStatus.OK);
    }

    public <T> void performErrorRequest(MockHttpServletRequestBuilder requestBuilder,
                                        T requestBody,
                                        String expectedErrorCode,
                                        int expectedStatus,
                                        String expectedMessageContains) throws Exception {

        String json = requestBody != null ? objectMapper.writeValueAsString(requestBody) : null;

        if (json != null) {
            requestBuilder.contentType(MediaType.APPLICATION_JSON).content(json);
        }

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(expectedErrorCode))
                .andExpect(jsonPath("$.message").value(containsString(expectedMessageContains)))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists());
    }
}
