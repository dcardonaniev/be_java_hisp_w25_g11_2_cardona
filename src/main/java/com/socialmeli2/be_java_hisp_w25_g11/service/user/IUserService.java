package com.socialmeli2.be_java_hisp_w25_g11.service.user;

import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowedListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerCountDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SuccessDTO;

public interface IUserService {
    SuccessDTO follow(Integer userId, Integer userIdToFollow);
    FollowerCountDTO getSellerFollowersCount(Integer sellerId);
    SuccessDTO unfollow(Integer userId, Integer sellerIdToUnfollow);
    FollowedListDTO getFollowedInfo(Integer userId, String order);
    FollowerListDTO getFollowersInfo(Integer userId, String order);
}