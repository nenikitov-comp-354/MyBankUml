package bank.db;

import java.time.LocalDate;

import lombok.*;

public class Customer {
    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    private LocalDate dateOfBirth;

    @Getter
    private String socialInsuranceNumber;

    @Getter
    private String phone;

    @Getter
    private String email;

    @Getter
    private Branch branch;
}
