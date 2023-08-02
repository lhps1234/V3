package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.example.order.service.PrincipalOAuthUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final PrincipalOAuthUserService principalOAuthUserService;
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http
			// Cross-Site Request Forgery 보호 기능 비활성화 
			.csrf().disable()
			// iframe으로 접근이 안되도록 하는 설정을 비활성화
			.headers().frameOptions().disable()
			.and()
			// URL 별 권한접근 제어
			.authorizeRequests()
			.antMatchers("/", "/member/join", "/member/login", "/member/logout", "/member/login-failed").permitAll()
			.antMatchers("/css/*", "/js/*", "/favicon.ico", "/error").permitAll()
			// "/admin" 하위의 모든 요청은 인증 후 ADMIN 권한을 가진 사용자만 접근 가능
			.antMatchers("/admin/**").hasAnyRole("ADMIN")
			// 이외의 모든 경로는 인증을 받아야 접근 가능
			.anyRequest().authenticated()
			.and()
			// 폼 로그인 방식을 사용
			.formLogin()
			// 아이디 필드의 기본값은 username이고 다른 이름을 사용할 시 이름을 지정함
			.usernameParameter("member_id")
			// 사용자가 만든 로그인 페이지를 사용
			// 해당 설정을 하지 않으면, 기본 값인 /login 이기 때문에 스프링이 사용하는 기본 로그인 페이지가 호출된다.
			.loginPage("/member/login")	//get 방식
			// 로그인 인증 처리를 하는 URL
			.loginProcessingUrl("/member/login")	//post 방식
			// 로그인에 성공 했을 때 이동할 URL
			.defaultSuccessUrl("/member/login-success")
			// 로그인에 실패 했을 때 이동할 URL
			//.failureUrl("/member/login-failed")
			.failureHandler(authenticationFailureHandler)
			.and()
			.logout()
			// 로그아웃 URL 지정
			.logoutUrl("/member/logout")
			// 로그아웃 성공 후 리다이렉트 될 주소
			.logoutSuccessUrl("/")
			// 세션 삭제
			.invalidateHttpSession(true)
			// 쿠키 삭제
			.deleteCookies("JSESSIONID")
			.and()
			.oauth2Login()
			// oauth2 로그인에 성공하면 principalOAuthUserService에서 인증을 진행함
			.userInfoEndpoint()
			.userService(principalOAuthUserService);
		
		return http.build();
	}
}
