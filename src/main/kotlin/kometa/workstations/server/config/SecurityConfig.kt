package kometa.workstations.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
//                    .requestMatchers("/api/engineer/**").hasRole("ENGINEER")
//                    .requestMatchers("/api/manager/**").hasRole("MANAGER")
                    .requestMatchers(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/",
                        "/index"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            }
            .sessionManagement { session ->
                session
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
            }
            .sessionManagement { session ->
                session
                    .sessionFixation().migrateSession()
                    .invalidSessionUrl("/login?expired")
            }
            .csrf { csrf -> csrf.disable() }
        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}