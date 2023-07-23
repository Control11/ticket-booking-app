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

    @AssertTrue(message = "Name should starts with a capital letter")
    public boolean isNameValid() {
        return Character.isUpperCase(name.charAt(0));
    }

    @AssertTrue(message = "Surname should starts with a capital letter")
    public boolean isSurnameValid() {
        if (Character.isUpperCase(surname.charAt(0))) {
            if (surname.contains("_")) {
                return Character.isUpperCase(surname.split("_")[1].charAt(0));
            }
            return true;
        }
        return false;
    }

}
