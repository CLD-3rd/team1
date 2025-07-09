package basic.request;

import basic.entity.User;
import basic.enumerate.UserRoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ToString
@Getter
@Setter

public class UserSignupRequestDTO {

    private String username;
    private String password;

    public User toEntity() {
        return User.of(
            username,
            password,
            UserRoleType.USER
        );
    }
}