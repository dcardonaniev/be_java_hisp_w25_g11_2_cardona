package com.socialmeli2.be_java_hisp_w25_g11.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
public class FollowerCountDTO {
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("followers_count")
    private Integer followersCount;
}
