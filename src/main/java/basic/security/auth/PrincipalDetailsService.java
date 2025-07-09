package basic.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import basic.entity.User;
import basic.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService { 

private final UserRepository userRepository;

@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

User user= userRepository.findByUsername(username);

if (user!= null) {
return new PrincipalDetails(user); 
}

return null;
}
}