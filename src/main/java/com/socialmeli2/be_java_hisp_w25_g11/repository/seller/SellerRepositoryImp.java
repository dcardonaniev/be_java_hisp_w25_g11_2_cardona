package com.socialmeli2.be_java_hisp_w25_g11.repository.seller;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SellerRepositoryImp implements ISellerRepository {
    private Map<Integer, Seller> sellers;

    public SellerRepositoryImp() {
        this.sellers = new HashMap<>();
    }

    @Override
    public List<Seller> getAll() {
        return sellers
                .values()
                .stream().toList();
    }

    @Override
    public List<Seller> createAll(List<Seller> entities) {
        entities.forEach(s -> sellers.put(s.getId(), s));
        return entities;
    }

    @Override
    public Seller create(Seller user) {
        this.sellers.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<Seller> get(Integer id) {
        return Optional.ofNullable(sellers.get(id));
    }

    @Override
    public boolean update(Integer id, Seller seller) {
        if (get(id).isEmpty()) {
            return false;
        }
        sellers.put(id,seller);
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        return sellers.remove(id) != null;
    }

    @Override
    public boolean exists(Integer id) {
        return sellers.containsKey(id);
    }

    @Override
    public Boolean addFollowed(Seller user, Integer userId) {
        user.getFollowed().add(userId);
        return update(user.getId(),user);
    }

    @Override
    public Boolean addFollower(Seller user, Integer userId) {
         user.getFollowers().add(userId);
         return update(user.getId(),user);
    }

    @Override
    public Boolean removeFollower(Seller user, Integer userIdToRemove) {
        user.getFollowers().remove(userIdToRemove);
        return update(user.getId(),user);
    }

    @Override
    public Boolean removeFollowed(Seller user, Integer userIdToRemove) {
        user.getFollowed().remove(userIdToRemove);
        return update(user.getId(),user);
    }

    @Override
    public void clearData() {
        sellers = new HashMap<>();
    }
}
