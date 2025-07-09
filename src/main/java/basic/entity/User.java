package basic.entity;

import java.time.LocalDateTime;

import basic.common.entity.BaseEntity;
import basic.enumerate.UserRoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class User extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	
	private String username;
	private String password;
	private LocalDateTime lastLogin; 


	@Enumerated(EnumType.STRING)
	private UserRoleType userRoleType;
	
	protected User() {} 

    private User(String username, String password, UserRoleType userRoleType) {
        this.username = username;
        this.password = password;
        this.userRoleType = userRoleType;
    }

    public static User of(String username, String password, UserRoleType userRoleType) {
        return new User(username, password, userRoleType);
    }
}
