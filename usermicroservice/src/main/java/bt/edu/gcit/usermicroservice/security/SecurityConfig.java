
// package bt.edu.gcit.usermicroservice.security;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtUtil jwtUtil;

//     public SecurityConfig(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     @Bean
//     public BCryptPasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable()) // Disable CSRF for testing (enable it in production)
//             .authorizeRequests(auth -> auth
//                 .requestMatchers("/api/users/approve/**").hasRole("Admin") 
//                 .requestMatchers("/api/login", "/api/signup","/api/forgot-password", "/api/verify-otp", "/api/reset-password").permitAll() // Allow open access to these endpoints
//                 .anyRequest().authenticated() // Require authentication for all other endpoints
              
//             )
//             .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // Add JWT authentication filter
//             .formLogin(form -> form.disable()) // Disable default login form
//             .httpBasic(basic -> basic.disable()); // Disable basic authentication

//         return http.build();
//     }
// }
package bt.edu.gcit.usermicroservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Bean for BCryptPasswordEncoder
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure security filter chain (JWT, permissions, etc.)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for testing (enable it in production)
            .authorizeRequests(auth -> auth
                .requestMatchers("/api/users/approve/**").hasRole("Admin")
                .requestMatchers("/api/login", "/api/signup", "/api/forgot-password", "/api/verify-otp", "/api/reset-password").permitAll() // Open access for specific endpoints
                .anyRequest().authenticated() // Require authentication for all other endpoints
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class) // Add JWT authentication filter
            .formLogin(form -> form.disable()) // Disable default login form
            .httpBasic(basic -> basic.disable()); // Disable basic authentication

        return http.build();
    }

    // Configure resource handlers to serve static images (from local directory)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This will serve files stored in the "static/images" folder under "src/main/resources"
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./src/main/resources/static/images/");
    }
}
