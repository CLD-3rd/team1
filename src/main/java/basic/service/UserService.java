package basic.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import basic.entity.User;
import basic.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

//    @Transactional
//    @CacheEvict(value = "userCache", allEntries = true)
//    public void initUsers() {
//        if (userRepository.count() == 0) {
//            IntStream.rangeClosed(1, 1000).forEach(i -> {
//                userRepository.save(User.builder()
//                        .name("User" + i)
//                        .email("user" + i + "@test.com")
//                        .age(20 + (i % 30))
//                        .build());
//            });
//        }
//    }

	@Transactional(readOnly = true)
	@Cacheable(value = "userCache", key = "#id")
	public User findById(Long id) {
		simulateDbDelay(); // DB hit 테스트 체감용 (선택)
		return userRepository.findById(id).orElse(null);
	}

	@Transactional
	@CacheEvict(value = "userCache", allEntries = true)
	public List<User> createBulk(List<User> users) {
		return userRepository.saveAll(users);
	}

	private void simulateDbDelay() {
		try {
			Thread.sleep(50); // DB hit 차이를 체감하기 위한 인위적 지연
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
