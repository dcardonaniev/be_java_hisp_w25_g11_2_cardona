package com.socialmeli2.be_java_hisp_w25_g11.dto.response;

import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class FollowerListDTO {
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("followers")
    private List<UserDTO> followers;
}
