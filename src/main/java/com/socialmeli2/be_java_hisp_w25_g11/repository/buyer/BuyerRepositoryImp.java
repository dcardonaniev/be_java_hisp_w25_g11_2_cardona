package com.socialmeli2.be_java_hisp_w25_g11.repository.buyer;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BuyerRepositoryImp implements IBuyerRepository {
    private Map<Integer, Buyer> buyers;

    public BuyerRepositoryImp() {
        this.buyers = new HashMap<>();
    }

    @Override
    public List<Buyer> getAll() {
        return buyers
                .values()
                .stream().toList();
    }

    @Override
    public List<Buyer> createAll(List<Buyer> entities) {
        entities.forEach(b -> buyers.put(b.getId(), b));
        return entities;
    }

    @Override
    public Buyer create(Buyer buyer) {
        this.buyers.put(buyer.getId(), buyer);

        return buyer;
    }

    @Override
    public Optional<Buyer> get(Integer id) {
        return Optional.ofNullable(buyers.get(id));
    }

    @Override
    public boolean update(Integer id, Buyer buyer) {
        if (get(id).isEmpty()) {
            return false;
        }

        buyers.put(id,buyer);
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        return buyers.remove(id) != null;
    }

    @Override
    public boolean exists(Integer id) {
        return buyers.containsKey(id);
    }

    @Override
    public Boolean addFollowed(Buyer user, Integer userId) {
        user.getFollowed().add(userId);
        return update(user.getId(),user);
    }

    @Override
    public Boolean removeFollowed(Buyer user, Integer userIdToRemove) {
        user.getFollowed().remove(userIdToRemove);
        return update(user.getId(),user);
    }

    @Override
    public void clearData() {
        buyers = new HashMap<>();
    }
}
