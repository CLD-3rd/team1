package basic.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ToString
@Getter
@Setter
public class MemberLoginRequest {

    private String username;
    private String password;
}