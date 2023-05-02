package com.example.rest_web_services_coding_assig;

import com.example.rest_web_services_coding_assig.filter.JwtRequestFilter;
import com.example.rest_web_services_coding_assig.service.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigurer  {

    private final MyUserDetailsService myUserDetailService;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfigurer(MyUserDetailsService myUserDetailService, JwtRequestFilter jwtRequestFilter) {
        this.myUserDetailService = myUserDetailService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/authenticate").permitAll()  // for this endpoint -> no login page
                .antMatchers("/swagger-ui/**").permitAll()
                .anyRequest().authenticated()                          // for any other endpoint require login
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session creation
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // add the filter
       return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(myUserDetailService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

