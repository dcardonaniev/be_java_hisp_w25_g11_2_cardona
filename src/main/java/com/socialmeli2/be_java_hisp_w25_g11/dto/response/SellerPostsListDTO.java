package com.socialmeli2.be_java_hisp_w25_g11.dto.response;

import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class SellerPostsListDTO {
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("posts")
    private List<SellerPostDTO> posts;
}
