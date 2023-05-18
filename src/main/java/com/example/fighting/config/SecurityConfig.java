package com.example.fighting.config;

import com.example.fighting.filter.JwtFilter;
import com.example.fighting.service.LoginService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {
    private final LoginService loginService;
    private final MessageSource messageSource;
    private final JwtFilter jwtFilter;
    public SecurityConfig(LoginService loginService, MessageSource messageSource, JwtFilter jwtFilter) {
        this.loginService = loginService;
        this.messageSource = messageSource;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                /**
                 * 僅測試用所以關掉
                 * */
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable()
                /**
                 * 關掉session
                 * */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                /**
                 * 設定那些url不經由security監控
                 * 其餘請求在登入後都可造訪
                 * */
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated();

        httpSecurity.authenticationProvider(daoAuthenticationProvider());//配置provider
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//配置JwtFilter

        return httpSecurity.build();
    }
    /**
     * 配置provider
     * detailService : 傳入實作 detailService
     * passwordEncoder : 密碼加密
     * messageSource : 訊息配置
     */
    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        Assert.notNull(this.loginService, "請實作UserDetailsService");
        DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
        impl.setUserDetailsService(this.loginService);
        impl.setPasswordEncoder(this.passwordEncoder());
        impl.setMessageSource(messageSource);
        return impl;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 啟用預設 authenticationManager
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 若想做SuccessHandler的話
     * */
    public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler(){
        return new SimpleUrlAuthenticationSuccessHandler(){
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

                //todo

                setDefaultTargetUrl("/");
                super.onAuthenticationSuccess(request, response, authentication);
            }
        };

    }
}
