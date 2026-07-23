# Project knowledge

Book Social Network API — a Spring Boot REST backend for a book-sharing / social network application. Handles users, roles, JWT-based authentication, and email (account activation) flows.

## Quickstart
- **Prerequisites:** JDK 25, Docker (for Postgres + MailDev). Uses the bundled Maven Wrapper (`./mvnw`).
- **Env vars:** `DB_USER` and `DB_PASSWORD` must be set (consumed by both `compose.yaml` and `application-dev.yaml`).
- **Start dependencies:** `docker compose up -d` — launches:
  - Postgres on `5432` (db `book_social_network`)
  - MailDev on `1080` (web UI) / `1025` (SMTP)
  - Note: `spring-boot-docker-compose` is on the runtime classpath, so running the app locally can auto-start Compose too.
- **Run app:** `./mvnw spring-boot:run` (defaults to the `dev` profile).
- **Build:** `./mvnw clean package`
- **Test:** `./mvnw test`
- **API base path:** all endpoints are served under `/api/v1/` (see `server.servlet.context-path`).
- **API docs:** Swagger UI via springdoc at `/api/v1/swagger-ui.html`.

## Architecture
- **Stack:** Spring Boot 4.1.0, Spring Web MVC, Spring Data JPA (Hibernate), Spring Security, Spring Mail, PostgreSQL, Lombok, JJWT 0.12.6, springdoc-openapi 2.5.0.
- **Base package:** `com.nabgha.book`
- **Key directories** (`src/main/java/com/nabgha/book`):
  - `BookNetworkApiApplication.java` — entry point; `@SpringBootApplication` + `@EnableJpaAuditing`.
  - `user/` — `User` (implements `UserDetails` + `Principal`, table `_users`), `Token` (table `tokens`, activation tokens), `UserRepository`, and `TokenRepository` (note: this misnamed class actually extends `JpaRepository<Token, ...>`).
  - `role/` — `Role` entity (table `roles`), `TokenRepository` (`findByName`).
  - `security/` — `SecurityConfig` (stateless, JWT filter chain, `/auth/**` + swagger paths permitted) and `JwtFilter`.
  - `config/` — `BeansConfig` (declares the `AuthenticationProvider` bean).
- **Config:** `application.yaml` selects the `dev` profile; `application-dev.yaml` holds datasource, JPA (`ddl-auto: update`), and mail settings.
- **Auth model:** JWT bearer tokens, stateless sessions, role-based access via `@EnableMethodSecurity(securedEnabled = true)`. Many-to-many User↔Role (eager-loaded on User).

## Conventions
- **Entities:** Lombok `@Getter/@Setter/@Builder/@NoArgsConstructor/@AllArgsConstructor`; auditing via `@CreatedDate`/`@LastModifiedDate` + `AuditingEntityListener` (enabled by `@EnableJpaAuditing`).
- **Lombok** is excluded from the final boot jar (see `spring-boot-maven-plugin` config) and wired as an annotation processor.
- **Java 25** language level — modern syntax is expected.

## Gotchas / known issues (incomplete scaffold)
- `BeansConfig.authenticationProvider()` has an **empty body** — must return a real provider (e.g. `DaoAuthenticationProvider`) or the app will fail to compile/start.
- `JwtFilter` is an empty `@Service` and is **not** a `OncePerRequestFilter` yet, but `SecurityConfig` adds it via `addFilterBefore(...)` — needs a proper filter implementation.
- `user/RoleRepository.java` is a misnomer: it manages `Token` entities, not roles. The real role repo lives in `role/RoleRepository.java`.
- `BookNetworkApiApplication.main` is package-private (`static void main`) rather than `public static void main`.
- No controllers/services exist yet — this is an early scaffold; auth endpoints under `/auth/**` are permitted in security config but not yet implemented.
