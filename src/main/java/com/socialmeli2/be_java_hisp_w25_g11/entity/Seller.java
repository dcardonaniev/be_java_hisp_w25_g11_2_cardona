package com.socialmeli2.be_java_hisp_w25_g11.entity;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
public class Seller implements ISeller {
    private Integer id;
    private String name;
    private Set<Integer> followed;
    private Set<Integer> followers;
    private Set<SellerPost> posts;

    public Seller (
            Integer id,
            String name
    ) {
        this.id = id;
        this.name = name;
        this.followed = new HashSet<>();
        this.followers = new HashSet<>();
        this.posts = new HashSet<>();
    }

    @Override
    public boolean canBeFollowed() {
        return true;
    }

    @Override
    public boolean canPost() {
        return true;
    }
}