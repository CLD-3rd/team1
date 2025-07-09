package basic.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable());

//		http.authorizeHttpRequests(authorize -> authorize
//				.requestMatchers("/", "/api/auth/").permitAll()
//				.requestMatchers("/api/auth/user/**").hasAnyRole("USER", "MANAGER", "ADMIN") // 유저, 매니져, 어드민 접근가능한 user
//				.requestMatchers("/api/auth/manager/**").hasAnyRole("MANAGER", "ADMIN")// admin,매니져만 접근가능한
//				.requestMatchers("/api/auth/admin/**").hasAnyRole("ADMIN") // admin만 접근가능한 페이지
//				.anyRequest().permitAll())
//				.exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler()) // 403 처리
//				);

		http.authorizeHttpRequests(
				authorize -> authorize.requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup")
						.permitAll().requestMatchers("/", "/api/auth/").permitAll().anyRequest().authenticated());

//		http.formLogin(form -> form.loginPage("/api/auth/login") 														
//				.loginProcessingUrl("/api/auth/login") 														
//				.defaultSuccessUrl("/api/questions?page=1&size=10", true)); 

		http.formLogin(login -> login.disable());

		http.logout(logout -> logout.logoutUrl("/api/auth/logout") // 로그아웃 요청을 보낼 URL (프론트에서 이 경로로 POST 요청)
				.logoutSuccessUrl("/api/auth/login") // 로그아웃 성공 후 이동할 URL
				.invalidateHttpSession(true) // 세션 무효화
				.deleteCookies("JSESSIONID") // 쿠키 삭제
		);
		return http.build();

	}

	@Bean
	public AccessDeniedHandler customAccessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			response.sendRedirect("/api/auth/");
		};
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}