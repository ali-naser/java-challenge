package jp.co.axa.apidemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    @NotNull(message = "error.validation.notNull")
    @Size(min = 6, max = 50, message = "error.validation.size.min.6.max.50")
    private String userName;

    @NotBlank
    @Size(min = 4, max = 50, message = "error.validation.size.min.4.max.50")
    private String password;

    @Size(max = 60, message = "error.validation.size.max.60")
    private String email;
}
