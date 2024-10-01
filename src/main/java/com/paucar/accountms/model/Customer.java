package com.paucar.accountms.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
    private long id;
    private String name;
    private String lastname;
    private String dni;
    private String email;
}
