package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.buyer;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.IUserRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.UserRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BuyerRepositoryTest {
    private IUserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = new UserRepositoryImp();
    }

    @Test
    void testGetAllOk() {
        List<IUser> buyers = List.of(
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer()
        );

        userRepository.createAll(buyers);
        List<IUser> actualBuyers = userRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertTrue(actualBuyers.containsAll(buyers));
    }

    @Test
    void testCreateAllOK() {
        List<IUser> buyers = List.of(
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer()
        );

        userRepository.createAll(buyers);
        List<IUser> actualBuyers = userRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertTrue(actualBuyers.containsAll(buyers));
    }

    @Test
    void testCreateBuyerOk() {
        IUser buyer = DummyUtils.createBuyer();
        userRepository.create(buyer);

        IUser actualBuyer = userRepository.findById(buyer.getId()).orElse(null);

        assertEquals(buyer, actualBuyer);
    }

    @Test
    void testGetBuyerOk() {
        IUser buyer = DummyUtils.createBuyer();
        userRepository.create(buyer);

        Optional<IUser> foundBuyer = userRepository.findById(buyer.getId());

        assertTrue(foundBuyer.isPresent());
        assertEquals(buyer, foundBuyer.get());
    }

    @Test
    void testUpdateBuyerOK() {
        IUser buyer = DummyUtils.createBuyer();
        userRepository.create(buyer);

        Buyer newSeller = DummyUtils.createBuyer();
        userRepository.update(buyer.getId(), newSeller);
        Optional<IUser> foundBuyer = userRepository.findById(buyer.getId());

        assertTrue(foundBuyer.isPresent());
        assertEquals(newSeller, foundBuyer.get());
    }

    @Test
    void testUpdateBuyerReturnsNonExistent() {
        IUser buyer = userRepository.create(DummyUtils.createBuyer());
        Integer nonExistentId = 1;

        assertFalse(userRepository.update(nonExistentId, buyer));
    }

    @Test
    void testDeleteBuyerOK() {
        Buyer buyer = DummyUtils.createBuyer();
        userRepository.create(buyer);

        assertEquals(1, userRepository.getAll().size());
        userRepository.delete(buyer.getId());
        Optional<IUser> foundBuyer = userRepository.findById(buyer.getId());

        assertTrue(foundBuyer.isEmpty());
        assertEquals(0, userRepository.getAll().size());
    }

    @Test
    void testExistingBuyerOK() {
        Buyer buyer = DummyUtils.createBuyer();
        Integer nonExistentId = 0;
        userRepository.create(buyer);

        assertTrue(userRepository.findById(buyer.getId()).isPresent());
        assertFalse(userRepository.findById(nonExistentId).isPresent());
    }
}