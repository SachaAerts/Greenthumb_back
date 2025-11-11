package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.controller.UserController;
import com.GreenThumb.api.user.application.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.GreenThumb.api.config.SecurityConfig.class,
                                com.GreenThumb.api.config.JwtAuthenticationFilter.class
                        })
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Doit retourner le nombre d'utilisateurs")
    void shouldReturnUserCount() throws Exception {
        // Given
        when(userService.countUsers()).thenReturn(42L);

        // When & Then
        mockMvc.perform(get("/api/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }
}