package com.br.sestaro.lowerbid.bid.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class Bid {

    @Id
    @GeneratedValue
    private Long id;

    @Positive(message = "Lance deve ser maior do que zero.")
    @Digits(integer = 99999999, fraction = 2, message = "Lance deve conter o máximo de duas casas decimais.")
    private double value;

    @NotBlank(message = "Nome é obrigatório.")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
