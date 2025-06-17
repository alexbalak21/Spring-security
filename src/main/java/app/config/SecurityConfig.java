package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

//Configuring Spring Security
@Configuration
//Enabling Spring Security
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //Diable csrf
        //http.csrf(customizer -> customizer.disable());


        //Authentication needed for any request
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

        //Enabling login with default page
        http.formLogin(Customizer.withDefaults());

        //Enabling basic authentication
        http.httpBasic(Customizer.withDefaults());

        // Disable session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        // Example of Lambda method expanded from the above code
        Customizer<CsrfConfigurer<HttpSecurity>> customCsrf = new Customizer<CsrfConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CsrfConfigurer<HttpSecurity> customizer) {
                customizer.disable();
            }
        };
        http.csrf(customCsrf);





        return http.build();

    }
}
