package ru.timkormachev.launchvote.to;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.timkormachev.launchvote.model.HasIdAndEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserTo implements HasIdAndEmail, Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank
    @Size(min = 3, max = 120)
    private String login;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 5, max = 32)
    private String password;

    public void setId(Integer id) {
        this.id = id;
    }
}
