package ru.javaprojects.projector.users.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "password_reset_tokens", uniqueConstraints = {@UniqueConstraint(columnNames = "user_id", name = "password_reset_tokens_unique_user_idx")})
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken extends UserToken {

    public PasswordResetToken(Long id, String token, Date expiryDate, User user) {
        super(id, token, expiryDate, user);
    }
}