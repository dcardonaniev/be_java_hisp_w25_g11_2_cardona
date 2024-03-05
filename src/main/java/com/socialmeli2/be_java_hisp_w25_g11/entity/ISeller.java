package com.socialmeli2.be_java_hisp_w25_g11.entity;

import java.util.Set;

public interface ISeller extends IUser {
    Set<Integer> getFollowers();
    Set<SellerPost> getPosts();

    void setFollowers(Set<Integer> followers);
    void setPosts(Set<SellerPost> posts);
}
