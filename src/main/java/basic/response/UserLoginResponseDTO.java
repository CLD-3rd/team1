package basic.response;

public class UserLoginResponseDTO {

    private String username;
    private String message;

    public UserLoginResponseDTO(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public static UserLoginResponseDTO of(String username) {
        return new UserLoginResponseDTO(username, "로그인 되었습니다");
    }
    
    }
