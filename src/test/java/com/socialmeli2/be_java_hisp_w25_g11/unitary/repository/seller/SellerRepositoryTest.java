package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.seller;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.SellerRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SellerRepositoryTest {
    private ISellerRepository sellerRepository;

    @BeforeEach
    public void setup() {
        sellerRepository = new SellerRepositoryImp();
    }

    @Test
    void testGetAllOK() {
        List<Seller> sellers = List.of(
                DummyUtils.createSeller(),
                DummyUtils.createSeller(),
                DummyUtils.createSeller()
        );

        sellerRepository.createAll(sellers);
        List<Seller> actualSellers = sellerRepository.getAll();

        assertEquals(sellers.size(), actualSellers.size());
        assertTrue(actualSellers.containsAll(sellers));
    }

    @Test
    void testCreateAllOK() {
        List<Seller> sellers = List.of(
                DummyUtils.createSeller(),
                DummyUtils.createSeller(),
                DummyUtils.createSeller()
        );

        sellerRepository.createAll(sellers);
        List<Seller> actualSellers = sellerRepository.getAll();

        assertEquals(sellers.size(), actualSellers.size());
        assertTrue(actualSellers.containsAll(sellers));
    }

    @Test
    void testCreateSellerOK() {
        Seller seller = DummyUtils.createSeller();
        sellerRepository.create(seller);

        Seller createdSeller = sellerRepository.get(seller.getId()).orElse(null);
        int expectedMapSize = 1;

        assertEquals(seller, createdSeller);
    }

    @Test
    void testGetSellerOK() {
        Seller seller = new Seller(10, "Danis");
        sellerRepository.create(seller);

        Optional<Seller> foundSeller = sellerRepository.get(seller.getId());

        assertTrue(foundSeller.isPresent());
        assertEquals(seller, foundSeller.get());
    }

    @Test
    void testUpdateSellerOK() {
        Seller seller = DummyUtils.createSeller();
        sellerRepository.create(seller);

        Seller newSeller = DummyUtils.createSeller();
        sellerRepository.update(seller.getId(), newSeller);
        Optional<Seller> foundSeller = sellerRepository.get(seller.getId());

        assertTrue(foundSeller.isPresent());
        assertEquals(newSeller, foundSeller.get());
    }

    @Test
    void testUpdateSellerReturnsInexistant() {
        Seller seller = sellerRepository.create(DummyUtils.createSeller());
        Integer inexistantId = 1;

        assertFalse(sellerRepository.update(inexistantId, seller));
    }

    @Test
    void testDeleteSellerOK() {
        Seller seller = DummyUtils.createSeller();
        sellerRepository.create(seller);

        assertEquals(1, sellerRepository.getAll().size());
        sellerRepository.delete(seller.getId());
        Optional<Seller> foundSeller = sellerRepository.get(seller.getId());

        assertTrue(foundSeller.isEmpty());
        assertEquals(0, sellerRepository.getAll().size());
    }

    @Test
    void testExistingSellerOK() {
        Seller seller = DummyUtils.createSeller();
        Integer nonExistentId = 0;
        sellerRepository.create(seller);

        assertTrue(sellerRepository.existing(seller.getId()));
        assertFalse(sellerRepository.existing(nonExistentId));
    }

    @Test
    public void testAddFollowedOK() {
        Seller seller = sellerRepository.create(DummyUtils.createSeller());
        Seller followedSeller = DummyUtils.createSeller();

        sellerRepository.addFollowed(seller, followedSeller.getId());

        assertEquals(1, seller.getFollowed().size());
        assertTrue(seller.getFollowed().contains(followedSeller.getId()));
    }

    @Test
    public void testRemoveFollowedOK() {
        Seller seller = sellerRepository.create(DummyUtils.createSeller());
        Seller followedSeller = DummyUtils.createSeller();

        sellerRepository.create(seller);
        sellerRepository.addFollowed(seller, followedSeller.getId());
        assertEquals(1, seller.getFollowed().size());

        sellerRepository.removeFollowed(seller, followedSeller.getId());
        assertEquals(0, seller.getFollowers().size());
    }
}