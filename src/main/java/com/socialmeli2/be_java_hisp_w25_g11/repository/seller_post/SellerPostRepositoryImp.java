package com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post;

import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SellerPostRepositoryImp implements ISellerPostRepository {
    private Map<Integer,SellerPost> sellerPosts;
    private Integer index = 1;

    public SellerPostRepositoryImp() {
        this.sellerPosts = new HashMap<>();
    }

    @Override
    public List<SellerPost> getAll() {
        return sellerPosts
                .values()
                .stream()
                .toList();
    }

    @Override
    public List<SellerPost> createAll(List<SellerPost> entities) {
        entities.forEach(p -> sellerPosts.put(p.getPostId(), p));

        return entities;
    }

    @Override
    public SellerPost create(SellerPost sellerPost) {
        sellerPost.setPostId(index++);
        sellerPosts.put(sellerPost.getPostId(), sellerPost);

        return sellerPost;
    }

    @Override
    public Optional<SellerPost> findById(Integer id) {
        return Optional.ofNullable(sellerPosts.get(id));
    }

    @Override
    public boolean update(Integer id, SellerPost sellerPost) {
        if (findById(id).isEmpty()) {
            return false;
        }

        sellerPosts.put(id, sellerPost);
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        return sellerPosts.remove(id) != null;
    }

    @Override
    public void clearData() {
        sellerPosts = new HashMap<>();
        index = 0;
    }
}
