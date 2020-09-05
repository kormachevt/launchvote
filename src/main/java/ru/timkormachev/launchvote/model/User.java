package ru.timkormachev.launchvote.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;


@Entity
@Table(name = "users")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractBaseEntity {

    public User(Integer id) {
        super(id);
    }

    public User() {
    }

    public User(User u) {
        this.id = u.getId();
        this.login = u.getLogin();
        this.password = u.getPassword();
        this.email = u.getEmail();
        this.enabled = u.isEnabled();
        this.roles = u.getRoles();
    }

    @Column(name = "login", nullable = false)
    @NotBlank
    @Size(min = 3, max = 120)
    private String login;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 5, max = 120)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles",
                     joinColumns = @JoinColumn(name = "user_id"),
                     uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"},
                                                            name = "user_roles_unique_idx")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    private Set<Role> roles;
}
