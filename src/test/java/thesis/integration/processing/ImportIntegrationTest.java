package thesis.integration.processing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("soil")
@AutoConfigureMockMvc
public class ImportIntegrationTest {
    private final static GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("redis:latest"))
                    .withExposedPorts(6379)
                    .waitingFor(Wait.forListeningPort());

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Test
    @WithMockUser(username = "user")
    void testImportFullSoilData() throws Exception {
        performImportTest("/import/full", "test-data/soil-data-full.json");
    }

    @Test
    @WithMockUser(username = "user")
    void testImportIncrementSoilData() throws Exception {
        performImportTest("/import/full", "test-data/soil-data-full.json");
        performImportTest("/import/increment", "test-data/soil-data-increment.json");
    }

    @Test
    @WithMockUser(username = "user")
    void testImportFullWithTransformSoilData() throws Exception {
        performImportTest("/import/full-with-transform", "test-data/soil-data.csv");
    }

    @Test
    @WithMockUser(username = "user")
    void testImportIncrementWithTransformSoilData() throws Exception {
        performImportTest("/import/full-with-transform", "test-data/soil-data.csv");
        performImportTest("/import/increment-with-transform", "test-data/soil-data.csv");
    }

    private void performImportTest(String endpoint, String fileResource) throws Exception {
        ClassPathResource resource = new ClassPathResource(fileResource);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "application/json",
                resource.getInputStream()
        );

        mockMvc.perform(multipart(endpoint)
                        .file(file))
                .andExpect(status().isOk());
    }
}
