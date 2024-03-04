package com.socialmeli2.be_java_hisp_w25_g11.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ValidationMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerPostDTO {
    @NotNull(message = ValidationMessages.USER_ID_CANNOT_BE_EMPTY)
    @Positive(message = ValidationMessages.USER_ID_MUST_BE_GREATER_THAN_ZERO)
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = ValidationMessages.POST_ID_CANNOT_BE_EMPTY)
    @Positive(message = ValidationMessages.POST_ID_MUST_BE_GREATER_THAN_ZERO)
    @JsonProperty("post_id")
    private Integer postId;

    @NotBlank(message = ValidationMessages.DATE_CANNOT_BE_EMPTY)
    @JsonProperty("date")
    private String date;

    @Valid
    @JsonProperty("product")
    private ProductDTO product;

    @NotNull(message = ValidationMessages.CATEGORY_CANNOT_BE_EMPTY)
    @JsonProperty("category")
    private Integer category;

    @NotNull(message = ValidationMessages.PRICE_CANNOT_BE_EMPTY)
    @DecimalMax(value = "10000000.00" ,message = ValidationMessages.PRICE_LIMIT)
    @JsonProperty("price")
    private Double price;
}
