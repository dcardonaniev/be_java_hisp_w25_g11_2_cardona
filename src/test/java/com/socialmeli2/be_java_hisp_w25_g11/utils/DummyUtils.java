package com.socialmeli2.be_java_hisp_w25_g11.utils;

import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.ProductDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Product;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DummyUtils {
    public static Faker faker = new Faker();

    public static SellerPost createNewSellerPost(Seller seller) {
        return new SellerPost(
                seller.getId(),
                faker.number().positive(),
                LocalDate.now(),
                new Product(
                        1,
                        faker.name().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.pokemon().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.brand().car().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.color().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.text().text(0, 15).replaceAll("[^a-zA-Z0-9 ]", "")
                ),
                faker.number().positive(),
                faker.number().randomDouble(1, 0, 100),
                seller
        );
    }

    public static CreatePostRequestDTO createCreatePostRequestDTO(Seller seller) {
        return new CreatePostRequestDTO(
                seller.getId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                new ProductDTO(
                        1,
                        faker.name().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.pokemon().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.brand().car().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.color().name().replaceAll("[^a-zA-Z0-9 ]", ""),
                        faker.text().text(0, 15).replaceAll("[^a-zA-Z0-9 ]", "")
                ),
                faker.number().positive(),
                faker.number().randomDouble(1, 0, 100)
        );
    }
}
