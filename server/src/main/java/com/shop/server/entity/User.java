package com.shop.server.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity						
@Table(name="users")
public class User extends BaseEntity{


	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY) 	// project에 연결된 db의 넘버링 전략을 따라간다.
	@Column(name="user_id")
	private Long id;	
	
	@Column(nullable = false, length = 100, unique = true)
	private String username;	

	@Column(nullable = false, length = 100) 
	private String password;	
	
	private String name;
	
	private String addr;
	
	@Column(length = 50) 
	private String email;		
	
	
	@Column(nullable = false, length = 100) 
	private String roles;	
	
	
	public List<String> getRoleList(){
		if(this.roles.length() > 0) {
			return Arrays.asList(this.roles.split(","));
		}
		return new ArrayList<>();
	}
	

}