

package Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import Security.AuthTokenFilter;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthTokenFilter authTokenFilter ;  //Authentication Filter is the last filter the requested data passes before heading into the controller and checking into the database 
    
    public SecurityConfig(AuthTokenFilter authTokenFilter){ 
        this.authTokenFilter = authTokenFilter ; 
    }

@Bean //inversion of controll
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder() ; //inversion of controll Spring calls this not me 
    }

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();  // Spring gives you the manager
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Principle 2: DI - http is injected by Spring
            .csrf(csrf -> csrf.disable()) //disabl
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //stateless can handle crashes over webservers , relaiable on scalability and simple 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()  
                .anyRequest().authenticated()
            )
            // Principle 3: Filter Chain - your filter runs before Spring's
            .addFilterBefore(authTokenFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();  // Returns configured object
    }
}


// -------------------------------------- FILE UNDERSTANDING ----------------------------------------------------------

//@Configuration tells Spring this class defines beans 
// @Bean methods create objects that Spring manages 
// SecurityFilterChain is the rulebook for all requests 
// addFilterBefore inserts your filter into Spring's chain
//  Stateless means no sessions - each request is independent
// This graph shows the security process that the project uses to take the user's data in safer hands 
//┌─────────────┐
//│   Request   │
//└──────┬──────┘
//       ↓
//┌─────────────┐
//│  JWT Filter │ ← Extracts token from "Authorization: Bearer ..."
//└──────┬──────┘
//       ↓
//┌─────────────┐
//│JwtUtils     │ ← Validates token signature & expiration
//└──────┬──────┘
//       ↓
//┌─────────────┐
//│UserDetails- │ ← Loads user from DB by email
//│Service      │
//└──────┬──────┘
//       ↓
//┌─────────────┐
//│Security-    │ ← Sets authentication for this request
//│ContextHolder│
//└──────┬──────┘
//       ↓
//┌─────────────┐
//│ Controller  │ ← You can now use @AuthenticationPrincipal
//└─────────────┘

