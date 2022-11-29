package uet.ktmt.myproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import uet.ktmt.myproject.common.myEnum.RoleEnum;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableJpaAuditing
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements AuditorAware<String> {

    @Autowired
    private UserDetailServiceConfig userDetailServiceConfig;
    @Autowired
    private FilterConfig filterConfig;

    // mã hóa thay cho MD5 spring security cung cấp (BCryptPasswordEncoder)
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Password encoder, để Spring Security sử dụng mã hóa mật khẩu người dùng
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
//        auth.setUserDetailsService(userDetailServiceConfig); //set the custom user details service
//        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
//        return auth;
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // Get AuthenticationManager bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailServiceConfig) // Cung cáp userservice cho spring security
                .passwordEncoder(passwordEncoder()); // cung cấp password encoder
        //auth.authenticationProvider(authenticationProvider());
    }

    // config các request được truy cập theo các role
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/", "/login", "/home", "/generate-otp", "/verify-otp"
                        , "/check-username-exist", "/check-email-exist", "/check-cellphone-exist"
                        , "/favicon.ico", "/register", "/get-menu", "/forget-password", "/send-new_password-by-email"
                        , "/product/detail/*", "/random-list-product", "/product/**", "/category/**", "/slide/**"
                        , "/blog/**", "/blog-detail/**"
                        , "/blog-detail", "/blog", "/faq", "/search", "/term-and-condition"
                        , "/assets/**", "/assets2/**")
                .permitAll() // cho phép tất cả người dùng truy cập các api /basic
                .antMatchers("/user/**").hasAnyAuthority(RoleEnum.ROLE_USER.toString()) // cho phép người dùng có role là user truy cập các api /user
                .antMatchers("/admin/**").hasAnyAuthority(RoleEnum.ROLE_ADMIN.toString()) // cho phép người dùng có role là admin truy cập các api /admin
                .anyRequest().authenticated(); // các api khác cần xác thực mới được truy cập
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .deleteCookies("accessToken")
                .invalidateHttpSession(true);
        // Thêm một lớp Filter kiểm tra jwt
        http.addFilterBefore(filterConfig, UsernamePasswordAuthenticationFilter.class);
    }

    //
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception ignored) {
            return Optional.of("anonymousUser");
        }
    }
    // @Bean
    // public JavaMailSender getJavaMailSender() {
    //     JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    //     mailSender.setHost("smtp.gmail.com");
    //     mailSender.setPort(587);

    //     mailSender.setUsername(EMAIL_NAME);
    //     mailSender.setPassword(EMAIL_PASSWORD);

    //     Properties props = mailSender.getJavaMailProperties();
    //     props.put("mail.transport.protocol", "smtp");
    //     props.put("mail.smtp.auth", "true");
    //     props.put("mail.smtp.starttls.enable", "true");
    //     props.put("mail.debug", "true");

    //     return mailSender;
    // }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        List<String> listDomain = new ArrayList<>();
//        listDomain.add("https://sandbox.vnpayment.vn");
//        config.setAllowedOrigins(listDomain);
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}
