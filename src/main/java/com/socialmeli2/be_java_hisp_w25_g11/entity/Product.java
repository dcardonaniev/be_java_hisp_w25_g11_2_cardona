package com.socialmeli2.be_java_hisp_w25_g11.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {
    private Integer id;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String notes;
}
