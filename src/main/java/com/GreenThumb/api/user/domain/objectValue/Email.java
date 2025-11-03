package com.GreenThumb.api.user.domain.objectValue;

import com.GreenThumb.api.user.domain.exception.FormatException;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private String email;
    public Email(String email) throws FormatException {
        checkMail(email);
        this.email = email;
    }

    public void setEmail(String email) throws FormatException {
        checkMail(email);
        this.email = email;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Email that
                && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    private void checkMail(String email) throws FormatException {
        if (!EMAIL_PATTERN.matcher(email).matches() || email.isEmpty()) {
            throw new FormatException("Format de l'email invalide");
        }
    }
}
