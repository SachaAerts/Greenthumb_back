package com.GreenThumb.api.user.domain.objectValue;

import com.GreenThumb.api.user.domain.exception.FormatException;

import java.util.Objects;
import java.util.regex.Pattern;

public class PhoneNumber {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^0\\d{1,14}$"
    );

    private String number;

    public PhoneNumber(String number) throws FormatException {
        checkNumber(number);

        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) throws FormatException {
        checkNumber(number);

        this.number = number;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof PhoneNumber that
                && this.number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }

    private void checkNumber(String number) throws FormatException {
        if (!PHONE_PATTERN.matcher(number).matches() || number.isEmpty()) {
            throw new FormatException("Format de l'phone invalide");
        }
    }
}
