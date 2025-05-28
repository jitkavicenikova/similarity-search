package thesis;

import io.lettuce.core.ClientOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public abstract class RedisContainerTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisContainerTestBase.class);

    protected static GenericContainer<?> redisContainer;
    protected static StringRedisTemplate redisTemplate;

    @BeforeAll
    public static void setUpContainer() {
        redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .waitingFor(Wait.forListeningPort());
        redisContainer.start();

        var redisHost = redisContainer.getHost();
        var redisPort = redisContainer.getMappedPort(6379);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);

        ClientOptions clientOptions = ClientOptions.builder()
                .autoReconnect(false)
                .build();
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(config, lettuceClientConfiguration);
        lettuceConnectionFactory.setValidateConnection(true);
        lettuceConnectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.afterPropertiesSet();
    }

    @AfterAll
    public static void tearDownContainer() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            redisTemplate.getConnectionFactory().getConnection().close();
        }
        if (redisContainer != null) {
            redisContainer.stop();
        }
    }

    @AfterEach
    public void cleanUpAfterTest() {
        if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
            var connection = redisTemplate.getConnectionFactory().getConnection();
            if (connection != null) {
                connection.serverCommands().flushAll();
                connection.close();
                LOGGER.info("Redis database flushed and connection closed.");
            } else {
                LOGGER.error("Failed to get Redis connection.");
            }
        } else {
            LOGGER.error("RedisTemplate or ConnectionFactory is null.");
        }
    }
}
