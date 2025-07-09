package basic.dto;


import basic.entity.User;
import basic.enumerate.UserRoleType;

public class UserDTO {

    private String username; 
    private String password;
    private UserRoleType userRoleType;

	public UserDTO(String username, String password, UserRoleType userRoleType) {
		this.username = username;
		this.password = password;
		this.userRoleType = userRoleType;
	}


    // 회원가입 시 사용
    public static UserDTO of(String username, String password) {
        return new UserDTO(username, password, UserRoleType.USER);
    }

    // Entity → DTO
    public static UserDTO from(User user) {
        return new UserDTO(
            user.getUsername(),
            user.getPassword(),
            user.getUserRoleType()
        );
    }


    // DTO → Entity
    public User toEntity() {
        return User.of(username, password, userRoleType);
	    }
    }

