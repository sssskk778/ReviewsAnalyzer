package com.aska.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Size(max = 20, message = "Password must be at most 20 characters long")
    private String password;

    @Size(min = 5, message = "Name must be at least 5 characters long")
    @Size(max = 50, message = "Name must be at most 50 characters long")
    private String name;

    private enum tier {
        FREE,
        PRO,
        VIP
    }

    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY, targetEntity = Project.class)
    private Set<Project> projects;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Authority> authorities;

}
