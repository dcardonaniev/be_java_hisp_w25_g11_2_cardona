package com.socialmeli2.be_java_hisp_w25_g11.entity;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class SellerPost {
    private Integer userId;
    private Integer postId;
    private LocalDate date;
    private Product product;
    private Integer Category;
    private Double price;
    private ISeller seller;
}
