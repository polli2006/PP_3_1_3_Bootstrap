package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;

    private final UserService userService;

    @Lazy
    public WebSecurityConfig(SuccessUserHandler successUserHandler, UserService userService) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index").permitAll()  // Страница доступна для всех
                .antMatchers("/admin/**").hasRole("ADMIN")  // Страницы admin только для пользователей с ролью ADMIN
                .anyRequest().authenticated()  // Все остальные страницы требуют аутентификации
                .and()
                .formLogin()
                .loginPage("/index")  // Указываем страницу для логина
                .loginProcessingUrl("/process_login")
                .successHandler(successUserHandler)
                .permitAll()  // Страница логина доступна для всех
                .and()
                .logout()
                .logoutSuccessUrl("/index")  // После логаута редирект на главную страницу
                .permitAll();  // Разрешить всем выходить
    }


}