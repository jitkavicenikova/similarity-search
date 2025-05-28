package thesis.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final StringRedisTemplate redisTemplate;

    public AdminController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/flushAll")
    public void flushAll() {
        LOGGER.info("Flushing all Redis data");
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}