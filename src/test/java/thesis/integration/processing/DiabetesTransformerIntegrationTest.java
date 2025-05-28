package thesis.integration.processing;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("diabetes")
@AutoConfigureMockMvc
public class DiabetesTransformerIntegrationTest {
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
    void testFullTransformDataSet1() throws Exception {
        performTransformationTest("/transformer/full", "test-data/diabetes1.csv", "test-data/diabetes1-full.json");
    }

    @Test
    @WithMockUser(username = "user")
    void testIncrementTransformDataSet1() throws Exception {
        performTransformationTest("/transformer/increment", "test-data/diabetes1.csv", "test-data/diabetes1-increment.json");
    }

    @Test
    @WithMockUser(username = "user")
    void testFullTransformDataSet2() throws Exception {
        performTransformationTest("/transformer/full", "test-data/diabetes2.csv", "test-data/diabetes2-full.json");
    }

    @Test
    @WithMockUser(username = "user")
    void testIncrementTransformDataSet2() throws Exception {
        performTransformationTest("/transformer/increment", "test-data/diabetes2.csv", "test-data/diabetes2-increment.json");
    }

    private void performTransformationTest(String endpoint, String csvResource, String expectedJsonResource) throws Exception {
        String expectedJson = new String(Files.readAllBytes(Paths.get(
                getClass().getClassLoader().getResource(expectedJsonResource).toURI())));

        ClassPathResource resource = new ClassPathResource(csvResource);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                resource.getFilename(),
                "application/json",
                resource.getInputStream()
        );

        var result = mockMvc.perform(multipart(endpoint)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        var actualJson = result.getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
