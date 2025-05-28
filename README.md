# Similarity Search for Laboratory Data

This application enables efficient processing and querying of laboratory measurement data.
It supports the full data pipeline—from extraction and 
normalization of input files to storage and similarity-based search over results.
The system is designed to handle both quantitative and qualitative measurements,
offering flexible querying that tolerates natural variation in laboratory data.
Separate transformation profiles (e.g., for soil or diabetes) allow domain-specific processing while sharing a common 
backend for validation and search.

## Deployment

### Docker Setup And Configuration

1. **Dockerfile**  
   The Dockerfile for building the application uses a multi-stage build process:
   - First, it uses `maven:3.9.5-eclipse-temurin-17` to build the Spring Boot app.
   - Then, it uses `eclipse-temurin:17-jdk` to run the app.

2. **Docker Compose Setup**  
   Each application (`soil-app` and `diabetes-app`) is configured with its own Spring profile and Redis instance.

3. **Setting Spring Profile**  
   The `SPRING_PROFILES_ACTIVE` environment variable selects which extractor/profile to use:
   ```yaml
   environment:
     - SPRING_PROFILES_ACTIVE=soil       # For soil-app

   environment:
     - SPRING_PROFILES_ACTIVE=diabetes   # For diabetes-app
  
### Running the Application
  Start the services with:
  ```bash
  docker-compose up --build
  ```

### Accessing the Application
- **Soil App**: http://localhost:8081/api/swagger-ui.html
- **Diabetes App**: http://localhost:8082/api/swagger-ui.html
- **Redis Insight**: for both Redis instances available at http://localhost:5540

## Data

Datasets are available in the `src/main/resources/data` directory.
- **Soil App**: `soil_data.csv`
- **Diabetes App**: `diabetes1_data.csv`, `diabetes2_data.csv`
