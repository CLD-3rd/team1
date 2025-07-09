package basic.security.auth;

import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import basic.enumerate.UserRoleType;
import basic.dto.MemberDto;
import basic.entity.Member;
import basic.repository.MemberRepository;

@Component
public class AdminAccountInitializer implements ApplicationListener<ApplicationReadyEvent> {

		private final MemberRepository memberRepository;
	    private final PasswordEncoder passwordEncoder;

	    public AdminAccountInitializer(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
	        this.memberRepository = memberRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

	    @Override
	    public void onApplicationEvent(ApplicationReadyEvent event) {
	        if (!memberRepository.findByUsername("admin").isPresent()) {
	        	
	            // 관리자 DTO 생성, 비밀번호는 암호화 적용
	        	MemberDto adminDto = new MemberDto("admin", passwordEncoder.encode("admin"), UserRoleType.ADMIN);

	            // DTO → Entity 변환
	            Member admin = adminDto.toEntity();

	            // 저장
	            memberRepository.save(admin);
	        }
	        	MemberDto userDto = new MemberDto("test123", passwordEncoder.encode("test123"), UserRoleType.USER);
	        	Member admin = userDto.toEntity();
	        	memberRepository.save(admin);
	    }
}
