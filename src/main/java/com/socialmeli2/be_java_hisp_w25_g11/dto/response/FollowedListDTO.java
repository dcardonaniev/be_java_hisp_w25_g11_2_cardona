package com.socialmeli2.be_java_hisp_w25_g11.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class FollowedListDTO {
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("followed")
    private List<UserDTO> followed;
}