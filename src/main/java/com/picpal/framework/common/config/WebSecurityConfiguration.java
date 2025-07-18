package com.picpal.framework.common.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration  {
    private static final String[] WHITE_LIST = {"/h2-console/**"};
    private static final String[] PERMIT_ALL_PATHS = {
        "/monitoring/**",  // 모니터링 페이지
        "/actuator/**",    // Spring Boot Actuator
        "/swagger-ui/**",  // Swagger UI
        "/v3/api-docs/**" // OpenAPI 문서
    };

    @Bean
    protected SecurityFilterChain filterChainConfigure(HttpSecurity http) throws Exception{
        /* #######################################
         * H2 Database console 사용을 위해 사용된 옵션
         * H2 Database 사용을 하지 않는 경우 반드시 제거.
         #########################################*/
        http.headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()
            )
        );

        http

            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/**")
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(
                    sessionManagementConfigure -> sessionManagementConfigure
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ) // 세션 유지X ( rest API는 stateless )
            .authorizeHttpRequests(
                    request -> request
                            .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                            .requestMatchers(PERMIT_ALL_PATHS).permitAll() // 인증 없이 접근 가능한 경로들
                            .requestMatchers(WHITE_LIST).permitAll() // H2 콘솔 등 특별한 경로
//                            .requestMatchers("/v1/..").hasRole("admin") // admin 권한만 접근 가능한 endpoint
//                            .requestMatchers("/v1/..").hasRole("user") // user 권한만 접근 가능한 endpoint
                            .anyRequest()
                            .authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        String user = "bwc";
        String pw = "bwc123";

        manager.createUser(User.withUsername(user).password(passwordEncoder().encode(pw)).roles("USER").build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // 단방향 암호화 , 같은 값이라도 매번 다른 값 반환
        return new BCryptPasswordEncoder();

        // 단방향 SHA512 암호화
        // return new EncSHA512Password();
    }
}