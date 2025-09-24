package com.ecom.config;

import com.ecom.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  private final JpaUserDetailsService userDetailsService;
  public SecurityConfig(JpaUserDetailsService uds){ this.userDetailsService = uds; }

  @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(userDetailsService);
    p.setPasswordEncoder(passwordEncoder());
    return p;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // REST ไม่ใช้ CSRF

      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/login", "/register",
                         "/css/**", "/js/**", "/images/**", "/favicon.ico","/uploads/**", "/error")
        .permitAll()
        .requestMatchers("/api/**",
                         "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
        .permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )

      .formLogin(f -> f
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/", true)
        .permitAll()
      )

      .logout(l -> l.logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .permitAll());

    http.authenticationProvider(authenticationProvider());
    return http.build();
  }
}
