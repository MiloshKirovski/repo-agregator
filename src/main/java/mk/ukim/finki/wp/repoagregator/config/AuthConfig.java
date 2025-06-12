package mk.ukim.finki.wp.repoagregator.config;

import mk.ukim.finki.wp.repoagregator.model.enumerations.AppRole;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;


public class AuthConfig {

    public HttpSecurity authorize(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers("/Consultations/GetTermsByTeacherCode").permitAll()
                        .requestMatchers("/display/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/consultations").hasAnyAuthority(
                                AppRole.STUDENT.name(), AppRole.ADMIN.name(), AppRole.PROFESSOR.name())
                        .requestMatchers("/auth/*", "/", "", "/display/*", "/css/*", "/js/*", "/images/*", "/login", "/logout").permitAll()
                        .requestMatchers("/studentAttendances/**").hasRole(AppRole.STUDENT.name())
                        .requestMatchers("/professorAttendances/**").hasAnyRole(AppRole.PROFESSOR.name(), AppRole.ADMIN.name())
                        .requestMatchers("/adminAttendances/**").hasRole(AppRole.ADMIN.name())
                        .requestMatchers("/manage-consultations/import").hasRole(AppRole.ADMIN.name())
                        .requestMatchers("/manage-consultations/sample-tsv").hasRole(AppRole.ADMIN.name())
                        .requestMatchers("/manage-consultations/{professorId}/**").access(
                                new WebExpressionAuthorizationManager("#professorId == authentication.name or hasRole('ROLE_ADMIN')")
                        )
                        .requestMatchers("/consultations/schedule").hasRole(AppRole.STUDENT.name())
                        .requestMatchers("/consultations/cancel").hasRole(AppRole.STUDENT.name())
                        .requestMatchers("/consultations/student/**").hasRole(AppRole.STUDENT.name())
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
    }

}
