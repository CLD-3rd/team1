package basic.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import basic.entity.Member;
import basic.repository.MemberRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService { 

private final MemberRepository memberRepository;

@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

Member member= memberRepository.findByUsername(username).get();

if (member!= null) {
return new PrincipalDetails(member); 
}

return null;
}
}