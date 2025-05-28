package thesis.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("soil")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowAccessToSwaggerWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());  // Swagger is permitted for all
    }

    @Test
    void shouldDenyUnauthenticatedUserAccessToGetMarker() throws Exception {
        mockMvc.perform(get("/marker/testMarker"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDenyAllUnauthenticatedRequestsOutsideSwagger() throws Exception {
        mockMvc.perform(get("/extractor/some-endpoint"))
                .andExpect(status().isUnauthorized());  // Unauthenticated user denied
    }
}
