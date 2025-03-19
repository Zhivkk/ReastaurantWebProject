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

    // SecurityFilterChain - начин, по който Spring Security разбира как да се прилага за нашето приложение
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // authorizeHttpRequests - конфиг. за група от ендпойнти
        // requestMatchers - достъп до даден ендпойнт
        // .permitAll() - всеки може да достъпи този ендпойнт
        // .anyRequest() - всички заявки, които не съм изброил
        // .authenticated() - за да имаш достъп, трябва да си аутентикиран
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
                                response.sendRedirect("/users/admin");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHEF"))) {
                                response.sendRedirect("/cart/chef");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_BARTENDER"))) {
                                    response.sendRedirect("/cart/bartender");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPPLIER"))) {
                                response.sendRedirect("/errand/delivery");
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
