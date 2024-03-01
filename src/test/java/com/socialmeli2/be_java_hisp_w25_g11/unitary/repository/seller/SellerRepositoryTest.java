package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.seller;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.seller.SellerRepositoryImp;
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
                new Seller(0, "David"),
                new Seller(1, "Mateo"),
                new Seller(2, "Juan Pablo")
        );

        sellerRepository.createAll(sellers);
        List<Seller> repoSellers = sellerRepository.getAll();

        assertEquals(sellers.size(), repoSellers.size());
        assertIterableEquals(sellers, repoSellers);
    }

    @Test
    void testCreateAllOK() {
        List<Seller> sellers = List.of(
                new Seller(0, "David"),
                new Seller(1, "Mateo"),
                new Seller(2, "Juan Pablo")
        );

        sellerRepository.createAll(sellers);
        List<Seller> repoSellers = sellerRepository.getAll();

        assertEquals(sellers.size(), repoSellers.size());
        assertIterableEquals(sellers, repoSellers);
    }

    @Test
    void testCreateSellerOK() {
        Seller seller = new Seller(0, "Juan Camilo");
        sellerRepository.create(seller);

        Seller createdSeller = sellerRepository.get(seller.getId()).orElse(null);
        int expectedMapSize = 1;

        assertEquals(expectedMapSize, sellerRepository.getAll().size());
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
        Seller seller = new Seller(0, "Emmanuel");
        sellerRepository.create(seller);

        Seller newSeller = new Seller(0, "Juan David");
        sellerRepository.update(seller.getId(), newSeller);
        Seller foundSeller = sellerRepository.get(seller.getId()).orElse(null);

        assertEquals(newSeller, foundSeller);
    }

    @Test
    void testUpdateSellerReturnsInexistant() {
        Seller seller = sellerRepository.create(DummyUtils.createSeller());
        Integer inexistantId = 100;

        assertFalse(sellerRepository.update(inexistantId, seller));
    }

    @Test
    void testDeleteSellerOK() {
        Seller seller = new Seller(0, "Emmanuel");
        sellerRepository.create(seller);

        assertEquals(1, sellerRepository.getAll().size());
        sellerRepository.delete(seller.getId());
        Optional<Seller> foundSeller = sellerRepository.get(seller.getId());

        assertTrue(foundSeller.isEmpty());
        assertEquals(0, sellerRepository.getAll().size());
    }

    @Test
    void testExistingSellerOK() {
        Seller seller = new Seller(0, "Raúl");
        sellerRepository.create(seller);

        assertTrue(sellerRepository.existing(0));
        assertFalse(sellerRepository.existing(1));
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