package com.example.order.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.order.config.PrincipalDetails;
import com.example.order.model.member.Member;
import com.example.order.model.member.RoleType;
import com.example.order.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOAuthUserService extends DefaultOAuth2UserService{
	
	private final MemberRepository memberRepository;
	
	
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("userReauest.getRegistration: {}", userRequest.getClientRegistration());
		log.info("getAccessToken: {}", userRequest.getAccessToken());
		
		OAuth2User oAuthUser = super.loadUser(userRequest);
		log.info("oAuthUser.getAttributes: {}", oAuthUser.getAttributes());
		
		
		
		String provider=userRequest.getClientRegistration().getRegistrationId();
		
		String email =null;
		String name = null;
		
		switch (provider) {
		case "google":
			 email = oAuthUser.getAttribute("email");
			 name = oAuthUser.getAttribute("name");
			 break;
		case "kakao":
			Map<String,Object> kakaoAccount = oAuthUser.getAttribute("kakao_account");
			name = ((Map<String,String>) kakaoAccount.get("profile")).get("nickname");
			email = (String)kakaoAccount.get("email");
			break;	
		}
		
//		Member member = new Member();
//		member.setMember_id(email);
//		member.setName(name);
//		member.setPassword("1234");
//		member.setEmail(email);
//		member.setRole(RoleType.ROLE_USER);
		
		
		Member member = Member.builder()
				.member_id(email)
				.password("1234")
				.name(name)
				.role(RoleType.ROLE_USER)
				.email(email)
				.provider(provider)
				.build();
		
		
		Member findMemberById = memberRepository.findMemberById(member.getMember_id());
		if(findMemberById==null) {
			//자동 회원가입
			memberRepository.saveMember(member);
		}
		
		return new PrincipalDetails(member, oAuthUser.getAttributes());
	}
}
