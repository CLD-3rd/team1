package basic.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import basic.entity.User;
import basic.repository.UserRepository;
import basic.request.UserLoginRequestDTO;
import basic.request.UserSignupRequestDTO;
import basic.service.*;
import basic.service.UserSessionService.UserSession;
import basic.security.auth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginLogoutController {

	private final UserSessionService userSessionService;
	private final ObjectMapper mapper;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(HttpSession session, @RequestBody UserLoginRequestDTO request,
			HttpServletRequest servletRequest) {

		// 시큐리티 유저 검증

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// 인증 성공 후 유저 정보 가져오기
			PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

			// 세션 생성 시간 측정
			long startTime = System.nanoTime();

			UserSession userSession = UserSession.builder().userId(String.valueOf(principal.getUser().getId()))
					.username(principal.getUsername()).roles(List.of(principal.getUser().getUserRoleType().name()))
					.loginTime(LocalDateTime.now()).ipAddress(servletRequest.getRemoteAddr())
					.userAgent(servletRequest.getHeader("User-Agent")).build();

			userSessionService.login(session, userSession);

			long duration = System.nanoTime() - startTime;

			return ResponseEntity.ok(Map.of("sessionId", session.getId(), "creationTime", duration / 1_000, // μs
					"message", "Login successful"));

		} catch (AuthenticationException e) {

			return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
		}
	}

//	@PostMapping("/signup") 
//	public String signUp(@ModelAttribute UserSignupRequestDTO requestDTO, Model model) {
//		String rawPassword = requestDTO.getPassword();
//		String encryptedPassword = passwordEncoder.encode(rawPassword);
//
//		User user = requestDTO.toEntity();
//		user.setPassword(encryptedPassword);
//		userRepository.save(user);
//
//		model.addAttribute("username", user.getUsername());
//		return "redirect:/api/auth/login"; 
//	}
//	

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserSignupRequestDTO requestDTO) {
		try {
			String rawPassword = requestDTO.getPassword();
			String encryptedPassword = passwordEncoder.encode(rawPassword);

			User user = requestDTO.toEntity();
			user.setPassword(encryptedPassword);
			userRepository.save(user);

			return ResponseEntity.ok(Map.of("username", user.getUsername(), "message", "Signup successful"));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(Map.of("error", "Signup failed", "details", e.getMessage()));
		}
	}

	@GetMapping("/validate")
	public ResponseEntity<Map<String, Object>> validateSession(HttpSession session) {
		long startTime = System.nanoTime();

		Object obj = session.getAttribute("userSession");
		UserSession userSession = mapper.convertValue(obj, UserSession.class);
		boolean valid = userSession != null;

		long duration = System.nanoTime() - startTime;

		return ResponseEntity.ok(Map.of("valid", valid, "validationTime", duration / 1_000, // μs
				"sessionId", session.getId()));
	}

	@GetMapping("/active-users")
	public ResponseEntity<Map<String, Object>> getActiveUsers() {
		return ResponseEntity
				.ok(Map.of("activeUsers", userSessionService.getActiveUserCount(), "timestamp", LocalDateTime.now()));
	}

	// 초기 데이터 세팅
//  @GetMapping("/users/init-data")
//  public ResponseEntity<String> initUsers() {
//
//  	userService.initUsers();
//      
//  	return ResponseEntity.ok("complete set user data");
//  }

	// 단일 사용자 조회 API
	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		User user = userService.findById(id);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}

	// CPU 과부화 API
	@GetMapping("/calculate")
	public ResponseEntity<Map<String, Object>> calculate(@RequestParam int complexity) {
		long start = System.currentTimeMillis();
		double result = 0;
		for (int i = 0; i < complexity * 1_000_000; i++) {
			result += Math.sqrt(i) * Math.sin(i);
		}
		long duration = System.currentTimeMillis() - start;

		Map<String, Object> response = new HashMap<>();
		response.put("result", result);
		response.put("processingTimeMs", duration);
		return ResponseEntity.ok(response);
	}

	// 대량 사용자 생성 API
	@PostMapping("/users/bulk")
	public ResponseEntity<List<User>> createBulkUsers(@RequestBody List<User> users) {
		List<User> created = userService.createBulk(users);
		return ResponseEntity.ok(created);
	}

	// 헬스 체크
	@GetMapping("/health")
	public ResponseEntity<Map<String, String>> health() {
		return ResponseEntity.ok(Map.of("status", "UP"));
	}

}
