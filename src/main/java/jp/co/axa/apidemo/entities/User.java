package jp.co.axa.apidemo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.co.axa.apidemo.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public User(UserDto userDto) {
        this.userName = userDto.getUserName();
        this.email = userDto.getEmail();
    }
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "error.validation.notNull")
    @Size(min = 6, max = 50, message = "error.validation.size.min.6.max.50")
    @Column(name = "USER_NAME", length = 50, unique = true, nullable = false)
    private String userName;

    @JsonIgnore
    @NotNull(message = "error.validation.notNull")
    @Size(min = 60, max = 60, message = "error.validation.size.min.60.max.60")
    @Column(name = "PASSWORD_HASH", length = 60)
    private String password;

    @Size(max = 60, message = "error.validation.size.max.60")
    @Column(name = "USER_EMAIL", length = 100, unique = true)
    private String email;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
