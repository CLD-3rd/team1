package basic.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ToString
@Getter
@Setter
public class UserLoginRequestDTO {

    private String username;
    private String password;
}