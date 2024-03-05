package com.socialmeli2.be_java_hisp_w25_g11.repository.user;

import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.repository.ICrudRepository;

public interface IUserRepository extends ICrudRepository <IUser, Integer> {
    void addFollowed(Integer userId, Integer followedId);
    void addFollower(Integer userId, Integer followerId);
    void removeFollowed(Integer userId, Integer followedId);
    void removeFollower(Integer userId, Integer followerId);
}
