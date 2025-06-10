package gov.iti.jet.ewd.ecom.config;

import gov.iti.jet.ewd.ecom.repository.UserRepository;
import gov.iti.jet.ewd.ecom.service.impl.UserServiceImpl;
import gov.iti.jet.ewd.ecom.security.AuthRole;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.rememberme.key}")
    private String rememberMeKey;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .logout(logout -> logout
                        .logoutUrl("/api/v1/users/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .cors(
                        cors ->
                                cors.configurationSource(corsConfigurationSource()))

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                //  Use session-based authentication instead of stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register"
                                , "/api/v1/users/login"
                                , "/api/v1/users/forgot-password"
                                , "/api/v1/users/verify-otp"
                                ,"/api/v1/users/reset-password")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
	                    .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
                        .requestMatchers("/api/v1/users/check-session").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/{orderId}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/user/**").permitAll()



                        // Admin-only endpoints
                        .requestMatchers("/api/v1/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/user/**").authenticated()

                        .anyRequest().authenticated()
                )
                .rememberMe(remember -> remember
                        .key(rememberMeKey) // Use a unique secret key
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
                        .rememberMeParameter("remember-me") // Name of the checkbox parameter
                        .userDetailsService(jpaUserDetailsService()) // Required for remember-me functionality
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3200")); // Your Angular app URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true); // Important for cookies/session

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "inMemoryUserDetailsService")
    public UserDetailsService inMemoryUserDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .roles(AuthRole.ADMIN.name()) // ROLE_ADMIN
                        .build()
        );
    }

    @Bean(name = "jpaUserDetailsService")
    @Primary
    public UserDetailsService jpaUserDetailsService() {
        return new UserServiceImpl(userRepository);
    }

    @Bean(name = "inMemoryAuthProvider")
    public DaoAuthenticationProvider inMemoryAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(inMemoryUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean(name = "jpaAuthProvider")
    public DaoAuthenticationProvider jpaAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(jpaUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(
                inMemoryAuthProvider(),
                jpaAuthProvider()
        ));
    }


}