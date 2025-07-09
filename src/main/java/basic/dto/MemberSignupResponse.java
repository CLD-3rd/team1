package basic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;


public class MemberSignupResponse {

    private String username;
    private String message;

    public MemberSignupResponse(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public static MemberSignupResponse of(String username) {
        return new MemberSignupResponse(username, "회원가입이 완료되었습니다.");
    }
}