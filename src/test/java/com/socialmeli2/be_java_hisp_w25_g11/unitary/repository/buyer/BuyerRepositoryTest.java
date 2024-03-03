package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.buyer;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.BuyerRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BuyerRepositoryTest {
    private IBuyerRepository buyerRepository;

    @BeforeEach
    void setup() {
        buyerRepository = new BuyerRepositoryImp();
    }

    @Test
    void testGetAllOk() {
        List<Buyer> buyers = List.of(
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer()
        );

        buyerRepository.createAll(buyers);
        List<Buyer> actualBuyers = buyerRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertTrue(actualBuyers.containsAll(buyers));
    }

    @Test
    void testCreateAllOK() {
        List<Buyer> buyers = List.of(
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer(),
                DummyUtils.createBuyer()
        );

        buyerRepository.createAll(buyers);
        List<Buyer> actualBuyers = buyerRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertTrue(actualBuyers.containsAll(buyers));
    }

    @Test
    void testCreateBuyerOk() {
        Buyer buyer = DummyUtils.createBuyer();
        buyerRepository.create(buyer);

        Buyer actualBuyer = buyerRepository.get(buyer.getId()).orElse(null);

        assertEquals(buyer, actualBuyer);
    }

    @Test
    void testGetBuyerOk() {
        Buyer buyer = DummyUtils.createBuyer();
        buyerRepository.create(buyer);

        Optional<Buyer> foundBuyer = buyerRepository.get(buyer.getId());

        assertTrue(foundBuyer.isPresent());
        assertEquals(buyer, foundBuyer.get());
    }

    @Test
    void testUpdateBuyerOK() {
        Buyer buyer = DummyUtils.createBuyer();
        buyerRepository.create(buyer);

        Buyer newSeller = DummyUtils.createBuyer();
        buyerRepository.update(buyer.getId(), newSeller);
        Optional<Buyer> foundBuyer = buyerRepository.get(buyer.getId());

        assertTrue(foundBuyer.isPresent());
        assertEquals(newSeller, foundBuyer.get());
    }

    @Test
    void testUpdateBuyerReturnsNonExistent() {
        Buyer buyer = buyerRepository.create(DummyUtils.createBuyer());
        Integer nonExistentId = 1;

        assertFalse(buyerRepository.update(nonExistentId, buyer));
    }

    @Test
    void testDeleteBuyerOK() {
        Buyer buyer = DummyUtils.createBuyer();
        buyerRepository.create(buyer);

        assertEquals(1, buyerRepository.getAll().size());
        buyerRepository.delete(buyer.getId());
        Optional<Buyer> foundBuyer = buyerRepository.get(buyer.getId());

        assertTrue(foundBuyer.isEmpty());
        assertEquals(0, buyerRepository.getAll().size());
    }

    @Test
    void testExistingBuyerOK() {
        Buyer buyer = DummyUtils.createBuyer();
        Integer nonExistentId = 0;
        buyerRepository.create(buyer);

        assertTrue(buyerRepository.existing(buyer.getId()));
        assertFalse(buyerRepository.existing(nonExistentId));
    }
}