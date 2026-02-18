package Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component  // Principle 4: Component - Spring creates and manages this
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            // Step 1: Extract token from header
            String jwt = extractJwtFromRequest(request);
            
            // Step 2: Validate token
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                
                // Step 3: Load user from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                
                // Step 4: Create authentication object
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Step 5: CRITICAL - Store in SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Principle 5: SecurityContextHolder - current user storage
            }
        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
        }
        
        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}


//OncePerRequestFilter guarantees filter runs once per requestdoFilterInternal is called for EVERY request
// SecurityContextHolder is like a pocket where you put the current user
//The authentication object contains WHO the user is and WHAT they can do
//Always call filterChain.doFilter() to continue processing