package com.socialmeli2.be_java_hisp_w25_g11.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ValidationMessages;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotNull(message = ValidationMessages.PRODUCT_ID_CANNOT_BE_EMPTY)
    @Positive(message = ValidationMessages.PRODUCT_ID_MUST_BE_GREATER_THAN_ZERO)
    @JsonProperty("product_id")
    private Integer id;

    @NotBlank(message = ValidationMessages.PRODUCT_NAME_CANNOT_BE_EMPTY)
    @Size(message = ValidationMessages.PRODUCT_NAME_SIZE_LIMIT, max = 40)
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = ValidationMessages.PRODUCT_NAME_CONTAINS_SPECIAL_CHARACTERS)
    @JsonProperty("product_name")
    private String name;


    @NotBlank(message = ValidationMessages.TYPE_CANNOT_BE_EMPTY)
    @Size(message = ValidationMessages.TYPE_SIZE_LIMIT, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",message = ValidationMessages.TYPE_CONTAINS_SPECIAL_CHARACTERS)
    @JsonProperty("type")
    private String type;

    @NotBlank(message = ValidationMessages.BRAND_CANNOT_BE_EMPTY)
    @Size(message = ValidationMessages.BRAND_SIZE_LIMIT, max = 25)
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",message = ValidationMessages.BRAND_CONTAINS_SPECIAL_CHARACTERS)
    @JsonProperty("brand")
    private String brand;

    @NotBlank(message = ValidationMessages.COLOR_CANNOT_BE_EMPTY)
    @Size(message = ValidationMessages.COLOR_SIZE_LIMIT, max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = ValidationMessages.COLOR_CONTAINS_SPECIAL_CHARACTERS)
    @JsonProperty("color")
    private String color;

    @Size(message = ValidationMessages.NOTES_SIZE_LIMIT, max = 80)
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$",message = ValidationMessages.NOTES_CONTAIN_SPECIAL_CHARACTERS)
    @JsonProperty("notes")
    private String notes;
}
