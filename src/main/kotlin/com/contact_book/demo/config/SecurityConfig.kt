package com.contact_book.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
class SecurityConfig(private val dataSource: DataSource): WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.jdbcAuthentication().dataSource(dataSource)
            .authoritiesByUsernameQuery("select username, password, enabled from users where username = ?")
            .authoritiesByUsernameQuery("select username, authority from authorities where username = ?")
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/resources/**")
    }

    override fun configure(http: HttpSecurity?) {
        http!!
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/app/{id}/delete", "/api/{id}/delete").hasRole("ADMIN")
            .antMatchers("/app/**").authenticated()
            .antMatchers("/api/**").hasAnyRole("ADMIN", "MODERATOR")
            .antMatchers("/login").anonymous()
            .and().formLogin().loginPage("/login")
            .loginProcessingUrl("/perform_login")
            .usernameParameter("login")
            .passwordParameter("password")
            .defaultSuccessUrl("/app/add")

    }

    @Bean
    protected fun passwordEncoder(): PasswordEncoder? {
        return NoOpPasswordEncoder.getInstance()
    }

}