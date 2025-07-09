package basic.dto;

import basic.entity.Member;
import basic.enumerate.UserRoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ToString
@Getter
@Setter

public class MemberSignupRequest {

    private String username;
    private String password;

    public Member toEntity() {
        return Member.of(
            username,
            password,
            UserRoleType.USER
        );
    }
}