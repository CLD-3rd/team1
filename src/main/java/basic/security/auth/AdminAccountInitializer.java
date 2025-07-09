package basic.security.auth;

import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import basic.dto.UserDTO;
import basic.entity.User;
import basic.enumerate.UserRoleType;
import basic.repository.UserRepository;

@Component
public class AdminAccountInitializer implements ApplicationListener<ApplicationReadyEvent> {

	  private final UserRepository userRepository;
	    private final PasswordEncoder passwordEncoder;

	    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	        this.userRepository = userRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

	    @Override
	    public void onApplicationEvent(ApplicationReadyEvent event) {
	        if (userRepository.findByUsername("admin") == null) {
	        	
	            // 관리자 DTO 생성, 비밀번호는 암호화 적용
	            UserDTO adminDto = new UserDTO("admin", passwordEncoder.encode("adminpassword"), UserRoleType.ADMIN);

	            // DTO → Entity 변환
	            User admin = adminDto.toEntity();

	            // 저장
	            userRepository.save(admin);
	        }
	        	UserDTO userDto = new UserDTO("test123", passwordEncoder.encode("test123!"), UserRoleType.USER);
	        	User admin = userDto.toEntity();
	        	userRepository.save(admin);
	    }
}
