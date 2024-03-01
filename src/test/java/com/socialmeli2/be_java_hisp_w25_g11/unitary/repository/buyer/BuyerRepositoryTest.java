package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.buyer;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.BuyerRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            new Buyer(1, "Buyer 1"),
            new Buyer(2, "Buyer 2"),
            new Buyer(3, "Buyer 3")
        );

        buyerRepository.createAll(buyers);
        List<Buyer> actualBuyers = buyerRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertIterableEquals(buyers, actualBuyers);
    }

    @Test
    void testCreateAllOK() {
        List<Buyer> buyers = List.of(
                new Buyer(1, "Buyer 1"),
                new Buyer(2, "Buyer 2"),
                new Buyer(3, "Buyer 3")
        );

        buyerRepository.createAll(buyers);
        List<Buyer> actualBuyers = buyerRepository.getAll();

        assertEquals(buyers.size(), actualBuyers.size());
        assertIterableEquals(buyers, actualBuyers);
    }

    @Test
    void testCreateOk() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "Buyer 1");

        buyerRepository.create(buyer);
        Buyer actualBuyer = buyerRepository.get(buyerId).orElse(null);

        assertEquals(buyer, actualBuyer);
    }

    @Test
    void testGetOk() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "Buyer 1");
        buyerRepository.create(buyer);

        Buyer actualBuyer = buyerRepository.get(buyerId).orElse(null);

        assertEquals(buyer, actualBuyer);
    }

    @Test
    void testUpdateOK() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "Buyer 1");
        Buyer updatedBuyer = new Buyer(buyerId, "Buyer 1 updated");
        buyerRepository.create(buyer);

        buyerRepository.update(buyerId, updatedBuyer);
        Buyer actualBuyer = buyerRepository.get(buyerId).orElse(null);

        assertEquals(updatedBuyer, actualBuyer);
    }

    @Test
    void testUpdateSellerReturnsInexistant() {
        Buyer buyer = buyerRepository.create(DummyUtils.createBuyer());
        Integer inexistantId = 100;

        assertFalse(buyerRepository.update(inexistantId, buyer));
    }

    @Test
    void testDeleteOK() {
        Integer buyerId = 1;
        List<Buyer> buyers = List.of(
                new Buyer(1, "Buyer 1"),
                new Buyer(2, "Buyer 2"),
                new Buyer(3, "Buyer 3")
        );
        buyerRepository.createAll(buyers);
        Integer expectedSize = buyers.size() - 1;

        buyerRepository.delete(buyerId);
        Buyer actualBuyer = buyerRepository.get(buyerId).orElse(null);

        assertNull(actualBuyer);
        assertEquals(expectedSize, buyerRepository.getAll().size());
    }

    @Test
    void testExistingOK() {
        Integer buyerId = 1;
        Integer nonExistantId = 100;
        Buyer buyer = new Buyer(buyerId, "Buyer 1");
        buyerRepository.create(buyer);

        boolean existing = buyerRepository.existing(buyerId);
        boolean falseExisting = buyerRepository.existing(nonExistantId);

        assertTrue(existing);
        assertFalse(falseExisting);
    }
}