package com.socialmeli2.be_java_hisp_w25_g11.unitary.service.seller_post;

import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.UserRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.service.seller_post.SellerPostServiceImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerPostServiceTest {

    @Mock
    private UserRepositoryImp userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private SellerPostServiceImp sellerPostService;

    @Test
    @DisplayName("T-0005: HAPPY PATH - Verify that order argument is handled correctly")
    void testFollowedSellersLatestPostsValidOrder(){
        Seller seller = DummyUtils.createSeller();
        Integer sellerId = seller.getId();
        String orderAsc = "DATE_ASC";
        String orderDesc = "DATE_DESC";

        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        assertDoesNotThrow(() -> sellerPostService.getFollowedSellersLatestPosts(sellerId, orderAsc));
        assertDoesNotThrow(() -> sellerPostService.getFollowedSellersLatestPosts(sellerId, orderDesc));
        assertDoesNotThrow(() -> sellerPostService.getFollowedSellersLatestPosts(sellerId, null));
    }
    
    @Test
    @DisplayName("T-0005: BAD REQUEST - Verify that exceptions are raised when invalid order parameter is given")
    void testFollowedSellersLatestPostsInvalidOrder() {
        Seller seller = DummyUtils.createSeller();
        Integer sellerId = seller.getId();
        String failOrderAsc = "ASC";
        String failOrderDesc = "DESC";
        String otherOrder = "EMPANADA";

        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));

        assertThrows(BadRequestException.class, () -> sellerPostService.getFollowedSellersLatestPosts(sellerId, failOrderAsc));
        assertThrows(BadRequestException.class, () -> sellerPostService.getFollowedSellersLatestPosts(sellerId, failOrderDesc));
        assertThrows(BadRequestException.class, () -> sellerPostService.getFollowedSellersLatestPosts(sellerId, otherOrder));
    }
    
    @Test
    @DisplayName("T-0006: HAPPY PATH - Verify that followed users list is sorted correctly")
    void testFollowedSellersLatestPostsOrder() {
        String orderAsc = "DATE_ASC";
        String orderDesc = "DATE_DESC";
        String orderInvalid = "DATE_INVALID";

        List<SellerPostDTO> followedSellersPostsAsc = this.getFollowedSellersLatestPosts(orderAsc).getPosts();
        List<SellerPostDTO> followedSellersPostsDesc = this.getFollowedSellersLatestPosts(orderDesc).getPosts();

        assertTrue(
            IntStream.range(0, followedSellersPostsAsc.size() - 1)
            .allMatch(
                i -> followedSellersPostsAsc.get(i)
                .getDate().compareTo(
                    followedSellersPostsAsc.get(i + 1)
                    .getDate()) <= 0
            )
        );
        assertTrue(
            IntStream.range(0, followedSellersPostsDesc.size() - 1)
            .allMatch(
                i -> followedSellersPostsDesc.get(i)
                .getDate().compareTo(
                    followedSellersPostsDesc.get(i + 1)
                    .getDate()) >= 0
            )
        );
        assertThrows(
            BadRequestException.class,
            () -> this.getFollowedSellersLatestPosts(orderInvalid)
        );
    }
    
    @Test
    @DisplayName("T-0008: HAPPY PATH - Verify that latest posts from followed sellers are correct")
    void testFollowedSellersLatestPostsOK() {
        SellerPostsListDTO followedSellersPosts = this.getFollowedSellersLatestPosts(null);

        assertFalse(followedSellersPosts.getPosts()
                .stream()
                .anyMatch(v -> LocalDate.parse(
                        v.getDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ).isBefore(LocalDate.now().minusWeeks(2))));
    }
    
    @Test
    @DisplayName("T-0008: NOT FOUND - Verifies that provided user ID exists")
    void testFollowedSellersLatestPostsThrowsNotFound() {
        Integer buyerId = 1;

        when(userRepository.findById(buyerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sellerPostService.getFollowedSellersLatestPosts(buyerId, null));
    }

    private SellerPostsListDTO getFollowedSellersLatestPosts(String order) {
        Integer buyerId = 1;
        Seller seller1 = DummyUtils.createSeller();
        Seller seller2 = DummyUtils.createSeller();
        Set<Integer> followedSellers = new HashSet<>(List.of(seller1.getId(), seller2.getId()));

        SellerPost post1 = DummyUtils.createSellerPost(seller1);
        SellerPost post2 = DummyUtils.createSellerPost(seller1);
        SellerPost post3 = DummyUtils.createSellerPost(seller2);
        SellerPost post4 = DummyUtils.createSellerPost(seller2);

        SellerPostDTO postDto1 = DummyUtils.createSellerPostDTO(post1);
        SellerPostDTO postDto2 = DummyUtils.createSellerPostDTO(post2);
        SellerPostDTO postDto3 = DummyUtils.createSellerPostDTO(post3);
        SellerPostDTO postDto4 = DummyUtils.createSellerPostDTO(post4);

        Buyer buyer = new Buyer(1, "Juan", followedSellers);

        when(userRepository.findById(buyerId)).thenReturn(Optional.of(buyer));
        when(userRepository.findById(seller1.getId())).thenReturn(Optional.of(seller1));
        when(userRepository.findById(seller2.getId())).thenReturn(Optional.of(seller2));

        lenient().when(modelMapper.map(post1, SellerPostDTO.class)).thenReturn(postDto1);
        lenient().when(modelMapper.map(post2, SellerPostDTO.class)).thenReturn(postDto2);
        lenient().when(modelMapper.map(post3, SellerPostDTO.class)).thenReturn(postDto3);
        lenient().when(modelMapper.map(post4, SellerPostDTO.class)).thenReturn(postDto4);

        return sellerPostService.getFollowedSellersLatestPosts(buyerId, order);
    }
}