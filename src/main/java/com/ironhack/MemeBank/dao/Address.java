package com.ironhack.MemeBank.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Embeddable
public class Address {
    private Long id;
    private String country;
    private String town;
    private String street;
    private String homeNumber;
    private String zipCode;
}
