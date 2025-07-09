package basic.security.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import basic.entity.Member;
import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails{


//재정의해야하것 크개 3가지 1. 유저 이름 2. 유저 비번 3. 유저 권한

private final Member member;

public Member getUser() {
    return this.member;
}

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {


Collection<GrantedAuthority> authorites = new ArrayList<GrantedAuthority>();
authorites.add(()-> member.getUserRoleType().getRole());

return authorites;
}

public String getRole() {
    return member.getUserRoleType().getRole();  // 예: "ROLE_ADMIN"
}


@Override
public String getPassword() {
return member.getPassword();
}

@Override
public String getUsername() {
return member.getUsername();
}


}