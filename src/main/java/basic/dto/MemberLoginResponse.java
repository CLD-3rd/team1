package basic.dto;

public class MemberLoginResponse {

    private String username;
    private String message;

    public MemberLoginResponse(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public static MemberLoginResponse of(String username) {
        return new MemberLoginResponse(username, "로그인 되었습니다");
    }
    
    }
