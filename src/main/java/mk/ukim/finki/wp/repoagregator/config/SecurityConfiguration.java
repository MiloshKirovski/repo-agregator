//package mk.ukim.finki.wp.repoagregator.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//
//@Profile(("!cas"))
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration extends AuthConfig {
//
//    @Bean
//    @Order(1100)
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        this.authorize(http);
//        http.httpBasic((basic) -> basic.realmName("wp.finki.ukim.mk"));
//        return http.build();
//    }
//
//}
package mk.ukim.finki.wp.repoagregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Profile(("!cas"))
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AuthConfig {

    @Bean
    @Order(1100)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        this.authorize(http)
                .formLogin(form -> form
                        .loginPage("/login")              // Custom login page
                        .loginProcessingUrl("/login")     // Form submission URL
                        .defaultSuccessUrl("/projects", true)     // Where to go after login
                        .failureUrl("/login?error=true")  // Where to go if login fails
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")             // Logout URL
                        .logoutSuccessUrl("/login?logout=true") // Where to go after logout
                        .invalidateHttpSession(true)     // Clear session
                        .deleteCookies("JSESSIONID")      // Delete session cookie
                        .permitAll()
                );

        // REMOVE THIS LINE - this is what's causing the browser popup:
        // http.httpBasic((basic) -> basic.realmName("wp.finki.ukim.mk"));

        return http.build();
    }
}