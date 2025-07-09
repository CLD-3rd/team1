package basic.dto;

import basic.entity.Member;
import basic.enumerate.UserRoleType;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;

    private String username;

    private String password;
    private UserRoleType userRoleType;
    public MemberDto() {
        // 기본 생성자
    }
	public MemberDto(String username, String password, UserRoleType userRoleType) {
		this.username = username;
		this.password = password;
		this.userRoleType = userRoleType;
	}
    // 회원가입 시 사용
    public static MemberDto of(String username, String password) {
        return new MemberDto(username, password, UserRoleType.USER);
    }

    // Entity → DTO
    public static MemberDto from(Member member) {
        return new MemberDto(
        		member.getUsername(),
        		member.getPassword(),
            member.getUserRoleType()
        );
    }
    // DTO → Entity
    public Member toEntity() {
        return Member.of(username, password, userRoleType);
	    }

}