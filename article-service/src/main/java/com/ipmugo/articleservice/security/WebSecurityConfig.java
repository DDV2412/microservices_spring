package com.ipmugo.articleservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(HttpMethod.GET,"/api/article", "/api/article/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/article").hasAuthority("Administrator")
                .antMatchers(HttpMethod.PUT,  "/api/article/**").hasAuthority("Administrator")
                .antMatchers(HttpMethod.DELETE,  "/api/article/**").hasAuthority("Administrator")
                .antMatchers(HttpMethod.GET,  "/api/article/current/issue/**", "/api/article/oai-pmh/**").hasAuthority("Administrator")
                .anyRequest().authenticated();

        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}