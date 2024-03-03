package com.socialmeli2.be_java_hisp_w25_g11.unitary.repository.seller_post;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Product;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.SellerPostRepositoryImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class SellerPostRepositoryTest {

        private ISellerPostRepository sellerPostRepository;

        @BeforeEach
        void setUp() {
            sellerPostRepository = new SellerPostRepositoryImp();
            sellerPostRepository.create(new SellerPost(1,2, LocalDate.now(),
                    new Product(2,"Test product","Type test",
                            "Brand test","Color test","notas test")
            ,2,5.0,
                    new Seller(1,"Pepito")));
        }

        @Test
        void testGetAllOk() {
            List<SellerPost> sellerPosts = sellerPostRepository.getAll();

            assertEquals(1,sellerPosts.size());
        }

        @Test
        void testCreateAllOk() {
            List<SellerPost> sellerPosts = List.of(
                    new SellerPost(),
                    new SellerPost(),
                    new SellerPost()
            );

            sellerPosts.get(0).setPostId(1);
            sellerPosts.get(1).setPostId(2);
            sellerPosts.get(2).setPostId(3);

            List<SellerPost> result = sellerPostRepository.createAll(sellerPosts);

            assertEquals(sellerPosts.size(), result.size());
            assertEquals(sellerPosts, result);
        }

        @Test
        void testCreateOk() {
            SellerPost sellerPost = new SellerPost();
            sellerPost.setPostId(1);

            sellerPostRepository.create(sellerPost);
            Optional<SellerPost> result = sellerPostRepository.get(sellerPost.getPostId());
            assertTrue(result.isPresent());

            assertNotNull(result.get().getPostId());
            assertEquals(sellerPost.getPostId(), result.get().getPostId());
            assertEquals(sellerPost.getCategory(), result.get().getCategory());
        }

        @Test
        void testGetOk() {
            SellerPost sellerPost = new SellerPost();
            sellerPost.setPostId(1);
            sellerPostRepository.create(sellerPost);

            Optional<SellerPost> retrievedPost = sellerPostRepository.get(sellerPost.getPostId());

            assertTrue(retrievedPost.isPresent());
            assertEquals(sellerPost, retrievedPost.get());
        }

        @Test
        void testUpdateOk() {
            SellerPost sellerPost = new SellerPost();
            sellerPost.setPostId(1);
            sellerPostRepository.create(sellerPost);

            SellerPost updatedPost = new SellerPost();
            updatedPost.setPostId(2);
            boolean result = sellerPostRepository.update(sellerPost.getPostId(), updatedPost);

            assertTrue(result);
            Optional<SellerPost> retrievedPost = sellerPostRepository.get(sellerPost.getPostId());
            assertTrue(retrievedPost.isPresent());
            assertEquals(updatedPost, retrievedPost.get());
        }

        @Test
        void testDeleteOK() {
            SellerPost sellerPost = new SellerPost();
            sellerPost.setPostId(1);
            sellerPostRepository.create(sellerPost);

            boolean result = sellerPostRepository.delete(sellerPost.getPostId());

            assertTrue(result);
            Optional<SellerPost> retrievedPost = sellerPostRepository.get(sellerPost.getPostId());
            assertTrue(retrievedPost.isEmpty());
        }

        @Test
        void testExistingOK() {
            SellerPost sellerPost = new SellerPost();
            sellerPost.setPostId(1);
            sellerPostRepository.create(sellerPost);

            Assertions.assertTrue(sellerPostRepository.existing(sellerPost.getPostId()));
            Assertions.assertFalse(sellerPostRepository.existing(sellerPost.getPostId() + 1));
        }
}
