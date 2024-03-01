package com.socialmeli2.be_java_hisp_w25_g11.service.user;

import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowedListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerCountDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SuccessDTO;

public interface IUserService {
    SuccessDTO follow(Integer userId, Integer userIdToFollow);
    FollowerCountDTO followersSellersCount(Integer sellerId);
    SuccessDTO unfollow(Integer userId, Integer sellerIdToUnfollow);
    FollowerListDTO sortFollowers(Integer userId, String order);
    FollowedListDTO sortFollowed(Integer userId, String order);
}