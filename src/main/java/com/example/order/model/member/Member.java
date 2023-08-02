package com.example.order.model.member;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Member {
    private String member_id;
    private String password;
    private String name;
    private GenderType gender;
    private LocalDate birth;
    private String email;
    private RoleType role;
    private String provider;
    
    @Builder
	public Member(String member_id, String password, String name, GenderType gender, LocalDate birth, String email,
			RoleType role, String provider) {
		super();
		this.member_id = member_id;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.birth = birth;
		this.email = email;
		this.role = role;
		this.provider = provider;
	}
    
    

}
