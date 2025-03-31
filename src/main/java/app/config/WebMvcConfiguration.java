package app.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;

@Configuration
@EnableMethodSecurity
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(matchers -> matchers
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/register", "/home").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            // Вземане на ролите на потребителя
                            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                            // Пренасочване въз основа на ролята
                            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHEF"))) {
                                response.sendRedirect("/chef");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_BARTENDER"))) {
                                    response.sendRedirect("/bartender");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPPLIER"))) {
                                response.sendRedirect("/delivery");
                            } else {
                                response.sendRedirect("/home");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}
