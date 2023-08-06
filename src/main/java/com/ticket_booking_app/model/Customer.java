package com.ticket_booking_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Length(min = 3, message = "Name should be at least 3 characters long")
    private String name;

    @NotBlank
    @Length(min = 3, message = "Surname should be at least 3 characters long")
    private String surname;

    @AssertTrue(message = "Name should start with a capital letter and rest of the letters should be lower case type")
    public boolean isNameValid() {
        char[] nameLetters = name.toCharArray();

        for (int i = 0; i < nameLetters.length; i++) {
            if (i != 0) {
                if (!Character.isLowerCase(nameLetters[i])) {
                    return false;
                }
            } else {
                if (!Character.isUpperCase(nameLetters[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    @AssertTrue(message = "Surname should start with a capital letter and rest of the letters should be lower case type")
    public boolean isSurnameValid() {
        char[] surnameLetters = surname.toCharArray();
        int underscore_counter = 0;

        for (int i = 0; i < surnameLetters.length; i++) {
            if (surnameLetters[i] == '_' && i != 0) {
                underscore_counter += 1;
                if (!Character.isUpperCase(surnameLetters[++i]) || underscore_counter > 1) {
                    return false;
                }
            } else if (i != 0) {
                if (!Character.isLowerCase(surnameLetters[i])) {
                    return false;
                }
            } else {
                if (!Character.isUpperCase(surnameLetters[i])) {
                    return false;
                }
            }
        }
        return true;
    }

}
