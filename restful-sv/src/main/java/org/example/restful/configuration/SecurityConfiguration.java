package org.example.restful.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

  private static final String[] AUTH_WHITELIST = {
    // -- Swagger UI v3 (OpenAPI)
    "/v3/api-docs/**", "/swagger-ui/**"
    // other public endpoints of your API may be appended to this array
  };

  @Bean
  public UserDetailsService userDetailsService() {

    UserDetails user1 =
        User.builder()
            .username("user1")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

    UserDetails admin =
        User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user1, admin);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf((csrf) -> csrf.disable())
        .authorizeRequests()
        .antMatchers(AUTH_WHITELIST)
        .permitAll()
        .antMatchers("/**")
        .authenticated()
        .and()
        .httpBasic();
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
