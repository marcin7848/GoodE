package com.goode.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.jdbcAuthentication().dataSource(dataSource)
        .usersByUsernameQuery("select username, password, enabled"
            + " from accounts where username=?")
        .authoritiesByUsernameQuery("select username, role"
            + " from accounts where username=?");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests().antMatchers("/resources/**").permitAll()
        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/login/success",true)
        .failureUrl("/login/error")
        .permitAll()
        .and()
        .logout()
        .permitAll()
        .and()
        .csrf().disable();

  }
}
