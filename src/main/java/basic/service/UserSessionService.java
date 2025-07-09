package basic.service;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserSession {
		private String userId;
		private String username;
		private List<String> roles;
		private LocalDateTime loginTime;
		private String ipAddress;
		private String userAgent;
	}

	public void login(HttpSession session, UserSession userSession) {
		session.setAttribute("userSession", userSession);
		session.setAttribute("userId", userSession.getUserId());

		// 추가 정보를 Redis에 직접 저장
		String sessionKey = "spring:session:sessions:" + session.getId();
		redisTemplate.opsForHash().put(sessionKey, "loginTime", userSession.getLoginTime());
		redisTemplate.opsForHash().put(sessionKey, "ipAddress", userSession.getIpAddress());

		String customKey = "user:session:meta:" + session.getId();
		Map<String, Object> meta = Map.of("loginTime", userSession.getLoginTime().toString(), "ipAddress",
				userSession.getIpAddress());
		redisTemplate.opsForHash().putAll(customKey, meta);
		redisTemplate.expire(customKey, Duration.ofSeconds(3600));

		limitConcurrentSessions(userSession.getUserId(), session.getId());
	}

	private void limitConcurrentSessions(String userId, String currentSessionId) {
		String userSessionsKey = "user:sessions:" + userId;

		redisTemplate.opsForZSet().add(userSessionsKey, currentSessionId, System.currentTimeMillis());

		Set<Object> sessions = redisTemplate.opsForZSet().range(userSessionsKey, 0, -1);
		if (sessions.size() > 2) {
			Set<Object> toRemove = redisTemplate.opsForZSet().range(userSessionsKey, 0, sessions.size() - 3);
			for (Object sessionId : toRemove) {
				// 세션 무효화
				redisTemplate.delete("spring:session:sessions:" + sessionId);
				redisTemplate.opsForZSet().remove(userSessionsKey, sessionId);
				log.info("Removed old session for user {}: {}", userId, sessionId);
			}
		}
	}

	public long getActiveUserCount() {
		Set<String> sessionKeys = redisTemplate.keys("spring:session:sessions:*");
		return sessionKeys != null ? sessionKeys.size() : 0;
	}

	public List<Map<String, Object>> getUserSessions(String userId) {

		String userSessionsKey = "user:sessions:" + userId;
		Set<ZSetOperations.TypedTuple<Object>> sessions = redisTemplate.opsForZSet().rangeWithScores(userSessionsKey, 0,
				-1);

		List<Map<String, Object>> result = new ArrayList<>();
		for (ZSetOperations.TypedTuple<Object> session : sessions) {
			String sessionId = session.getValue().toString();
			Map<String, Object> sessionInfo = new HashMap<>();
			sessionInfo.put("sessionId", sessionId);
			sessionInfo.put("loginTime", new Date(session.getScore().longValue()));

			String sessionKey = "spring:session:sessions:" + sessionId;
			Map<Object, Object> sessionData = redisTemplate.opsForHash().entries(sessionKey);
			sessionInfo.put("lastAccess", sessionData.get("lastAccessedTime"));
			sessionInfo.put("ipAddress", sessionData.get("ipAddress"));

			result.add(sessionInfo);
		}

		return result;
	}
}