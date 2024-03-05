package com.socialmeli2.be_java_hisp_w25_g11.repository.user;

import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImp implements IUserRepository{
    private Map<Integer, IUser> users;

    public UserRepositoryImp() {
        this.users = new HashMap<>();
    }

    @Override
    public List<IUser> getAll() {
        return users
                .values()
                .stream()
                .toList();
    }

    @Override
    public List<IUser> createAll(List<IUser> newUsers) {
        newUsers.forEach(u -> users.put(u.getId(), u));
        return newUsers;
    }

    @Override
    public IUser create(IUser user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<IUser> findById(Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean update(Integer userId, IUser user) {
        if (findById(userId).isEmpty()) {
            return false;
        }

        users.put(userId, user);
        return true;
    }

    @Override
    public boolean delete(Integer userId) {
        return users.remove(userId) != null;
    }

    @Override
    public void clearData() {
        this.users = new HashMap<>();
    }

    @Override
    public void addFollowed(Integer userId, Integer followedId) {
        users.get(userId).getFollowed().add(followedId);
    }

    @Override
    public void addFollower(Integer userId, Integer followerId) {
        ((Seller) users.get(userId)).getFollowers().add(followerId);
    }

    @Override
    public void removeFollowed(Integer userId, Integer followedId) {
        users.get(userId).getFollowed().remove(followedId);
    }

    @Override
    public void removeFollower(Integer userId, Integer followerId) {
        ((Seller) users.get(userId)).getFollowers().remove(followerId);
    }
}
