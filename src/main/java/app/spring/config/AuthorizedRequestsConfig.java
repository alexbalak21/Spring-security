package app.spring.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Configuration class that defines authorized request matchers and public endpoints.
 * This helps keep the security configuration clean and maintainable.
 */
public class AuthorizedRequestsConfig {

    private static final PathPatternParser pathPatternParser = PathPatternParser.defaultInstance;

    /**
     * List of public endpoints that don't require authentication
     */
    public static final List<RequestMatcher> PUBLIC_ENDPOINTS = createPublicEndpoints();

    private static List<RequestMatcher> createPublicEndpoints() {
        return List.of(
            // Authentication
            createRequestMatcher("/api/login", HttpMethod.POST),
            createRequestMatcher("/api/users", HttpMethod.POST),
            
            // API Documentation
            createRequestMatcher("/v3/api-docs/**"),
            createRequestMatcher("/swagger-ui/**"),
            createRequestMatcher("/swagger-ui.html"),
            createRequestMatcher("/swagger-resources/**"),
            createRequestMatcher("/webjars/**"),
            
            // Health check
            createRequestMatcher("/actuator/health")
        );
    }

    private static RequestMatcher createRequestMatcher(String pattern, HttpMethod method) {
        PathPattern pathPattern = pathPatternParser.parse(pattern);
        return request -> {
            if (method != null && !method.matches(request.getMethod())) {
                return false;
            }
            String path = request.getRequestURI();
            return pathPattern.matches(org.springframework.http.server.PathContainer.parsePath(path));
        };
    }

    private static RequestMatcher createRequestMatcher(String pattern) {
        return createRequestMatcher(pattern, null);
    }

    /**
     * Checks if a request should be permitted without authentication
     *
     * @param request the HTTP request
     * @return true if the request is public, false otherwise
     */
    public static boolean isPublicRequest(HttpServletRequest request) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }
}
