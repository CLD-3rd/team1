package basic.controller;

import basic.dto.MemberDto;
import basic.dto.MemberSignupRequest;
import basic.entity.Member;
import basic.repository.MemberRepository;
import basic.security.auth.PrincipalDetails;
import basic.service.MemberService;
import basic.service.UserSessionService;
import basic.service.UserSessionService.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MemberController {

    private final MemberService memberService;
	private final UserSessionService userSessionService;
	private final ObjectMapper mapper;
	private final MemberRepository memberRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;


    public MemberController(MemberService memberService, UserSessionService userSessionService, PasswordEncoder passwordEncoder, MemberRepository memberRepository, ObjectMapper mapper, AuthenticationManager authenticationManager) {
        this.memberService = memberService;
		this.userSessionService = userSessionService;
		this.mapper = mapper;
		this.memberRepository = memberRepository;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    // 회원가입 처리
    @GetMapping("/member/signup")
    public String signUp(Model model) {
    	model.addAttribute("memberDto", new MemberDto());
        return "createMemberDto";
    }

    @PostMapping("/member/signup")
    public String signUp(@ModelAttribute MemberDto memberDto, Model model) {
    	try {
            String rawPassword = memberDto.getPassword();
            String encryptedPassword = passwordEncoder.encode(rawPassword);
            memberDto.setPassword(encryptedPassword); // 암호화된 비밀번호로 덮어쓰기
            Member member = memberDto.toEntity();
            // 서비스 레이어로 가입 처리 위임
            memberService.join(memberDto);
            return "redirect:/"; // 가입 성공 시 홈으로 리다이렉트
        } catch (IllegalStateException e) {
            // 예외 메시지를 모델에 담아서 회원가입 폼으로 다시 보여줌
            model.addAttribute("signupError", e.getMessage());
            model.addAttribute("memberDto", memberDto); // 기존 입력값 유지
            return "createMemberDto";
        }
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session, HttpServletRequest servletRequest) {
     
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

			long startTime = System.nanoTime();

			UserSession userSession = UserSession.builder().userId(String.valueOf(principal.getUser().getId()))
					.username(principal.getUsername()).roles(List.of(principal.getUser().getUserRoleType().name()))
					.loginTime(LocalDateTime.now()).ipAddress(servletRequest.getRemoteAddr())
					.userAgent(servletRequest.getHeader("User-Agent")).build();

			userSessionService.login(session, userSession);

			long duration = System.nanoTime() - startTime;

			return memberService.login(username, password, session, model);

		} catch (AuthenticationException e) {

			 model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
			 return "loginForm"; // 실패 시 로그인 폼으로 리턴	
		}
    	
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("loginMember");
        if (member == null) {
            return "redirect:/";
        }
        model.addAttribute("member", member);
        return "home";
    }
}
