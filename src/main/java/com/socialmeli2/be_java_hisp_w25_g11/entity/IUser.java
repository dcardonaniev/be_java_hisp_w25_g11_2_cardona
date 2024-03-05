package com.socialmeli2.be_java_hisp_w25_g11.entity;

import java.util.Set;

public interface IUser {
    Integer getId();
    String getName();
    Set<Integer> getFollowed();
    void setId(Integer id);
    void setName(String name);
    void setFollowed(Set<Integer> followed);
    default boolean canBeFollowed() { return false; }
    default boolean canPost() { return false; }
}
