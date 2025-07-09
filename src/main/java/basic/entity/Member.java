package basic.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import basic.enumerate.UserRoleType;
@Entity
@Data
@Table(name = "member")
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;

    private String password;
	private LocalDateTime lastLogin; 
	@Enumerated(EnumType.STRING)
	private UserRoleType userRoleType;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    protected Member() { }

    private Member(String username, String password, UserRoleType userRoleType) {
        this.username = username;
        this.password = password;
        this.userRoleType = userRoleType;
    }

    public static Member of(String username, String password, UserRoleType userRoleType) {
        return new Member(username, password, userRoleType);
    }

}