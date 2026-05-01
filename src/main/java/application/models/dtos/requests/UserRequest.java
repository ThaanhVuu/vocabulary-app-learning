package application.models.dtos.requests;

import core.base.BaseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest extends BaseDTO {
    @Email
    @NotBlank
    String email;
    @Size(min = 8, message = "Password must be at least 8 characters")
    @NotBlank
    String password;
    boolean enabled = false;
}
