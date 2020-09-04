package ru.timkormachev.launchvote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.timkormachev.launchvote.services.DbUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/profile/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/votes/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.GET, "/restaurants/**").hasAnyRole("ADMIN", "USER")
                .antMatchers(HttpMethod.POST, "/restaurants/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/restaurants/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/restaurants/**").hasRole("ADMIN").and()
                .csrf().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(encoder);
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new DbUserDetailsService();
    }
}