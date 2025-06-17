package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//Configuring Spring Security
@Configuration
//Enabling Spring Security
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //Diable csrf
        http.csrf(AbstractHttpConfigurer::disable);


        //Authentication needed for any request
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

        //Enabling login with default page
        http.formLogin(Customizer.withDefaults());


        return http.build();

    }
}
