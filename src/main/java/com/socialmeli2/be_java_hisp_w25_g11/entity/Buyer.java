package com.socialmeli2.be_java_hisp_w25_g11.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Buyer {
    private Integer id;
    private String name;
    private Set<Integer> followed;

    public Buyer(
            Integer id,
            String name
    ) {
        this.id = id;
        this.name = name;
        this.followed = new HashSet<>();
    }
}
