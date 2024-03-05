package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.seller;

import com.socialmeli2.be_java_hisp_w25_g11.entity.ISeller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.IUserRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.UserRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class userRepositoryTest {
    private IUserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository = new UserRepositoryImp();
    }

    @Test
    void testGetAllOK() {
        List<IUser> sellers = List.of(
                DummyUtils.createSeller(),
                DummyUtils.createSeller(),
                DummyUtils.createSeller()
        );

        userRepository.createAll(sellers);
        List<IUser> actualSellers = userRepository.getAll();

        assertEquals(sellers.size(), actualSellers.size());
        assertTrue(actualSellers.containsAll(sellers));
    }

    @Test
    void testCreateAllOK() {
        List<IUser> sellers = List.of(
                DummyUtils.createSeller(),
                DummyUtils.createSeller(),
                DummyUtils.createSeller()
        );

        userRepository.createAll(sellers);
        List<IUser> actualSellers = userRepository.getAll();

        assertEquals(sellers.size(), actualSellers.size());
        assertTrue(actualSellers.containsAll(sellers));
    }

    @Test
    void testCreateSellerOK() {
        IUser seller = DummyUtils.createSeller();
        userRepository.create(seller);

        IUser createdSeller = userRepository.findById(seller.getId()).orElse(null);

        assertEquals(seller, createdSeller);
    }

    @Test
    void testGetSellerOK() {
        IUser seller = new Seller(10, "Danis");
        userRepository.create(seller);

        Optional<IUser> foundSeller = userRepository.findById(seller.getId());

        assertTrue(foundSeller.isPresent());
        assertEquals(seller, foundSeller.get());
    }

    @Test
    void testUpdateSellerOK() {
        IUser seller = DummyUtils.createSeller();
        userRepository.create(seller);

        IUser newSeller = DummyUtils.createSeller();
        userRepository.update(seller.getId(), newSeller);
        Optional<IUser> foundSeller = userRepository.findById(seller.getId());

        assertTrue(foundSeller.isPresent());
        assertEquals(newSeller, foundSeller.get());
    }

    @Test
    void testUpdateSellerReturnsInexistant() {
        IUser seller = userRepository.create(DummyUtils.createSeller());
        Integer inexistantId = 1;

        assertFalse(userRepository.update(inexistantId, seller));
    }

    @Test
    void testDeleteSellerOK() {
        IUser seller = DummyUtils.createSeller();
        userRepository.create(seller);

        assertEquals(1, userRepository.getAll().size());
        userRepository.delete(seller.getId());
        Optional<IUser> foundSeller = userRepository.findById(seller.getId());

        assertTrue(foundSeller.isEmpty());
        assertEquals(0, userRepository.getAll().size());
    }

    @Test
    void testExistingSellerOK() {
        IUser seller = DummyUtils.createSeller();
        Integer nonExistentId = 0;
        userRepository.create(seller);

        assertTrue(userRepository.findById(seller.getId()).isPresent());
        assertFalse(userRepository.findById(nonExistentId).isPresent());
    }

    @Test
    public void testAddFollowedOK() {
        IUser seller = userRepository.create(DummyUtils.createSeller());
        IUser followedSeller = DummyUtils.createSeller();

        userRepository.addFollowed(seller.getId(), followedSeller.getId());

        assertEquals(1, seller.getFollowed().size());
        assertTrue(seller.getFollowed().contains(followedSeller.getId()));
    }

    @Test
    public void testRemoveFollowedOK() {
        IUser seller = userRepository.create(DummyUtils.createSeller());
        IUser followedSeller = DummyUtils.createSeller();

        userRepository.create(seller);
        userRepository.addFollowed(seller.getId(), followedSeller.getId());
        assertEquals(1, seller.getFollowed().size());

        userRepository.removeFollowed(seller.getId(), followedSeller.getId());
        assertEquals(0, ((ISeller) seller).getFollowers().size());
    }
}