package com.socialmeli2.be_java_hisp_w25_g11.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull(message = ValidationMessages.USER_ID_CANNOT_BE_EMPTY)
    @Positive(message = ValidationMessages.USER_ID_MUST_BE_GREATER_THAN_ZERO)
    @JsonProperty("user_id")
    private Integer id;

    @NotBlank(message = ValidationMessages.USER_NAME_CANNOT_BE_EMPTY)
    @Size(message = ValidationMessages.USER_NAME_SIZE_LIMIT, max = 15)
    @JsonProperty("user_name")
    private String name;
}
