package com.socialmeli2.be_java_hisp_w25_g11.unitary.service.user;
import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowedListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SuccessDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.service.user.IUserService;
import com.socialmeli2.be_java_hisp_w25_g11.service.user.UserServiceImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.MapperUtil;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private ISellerRepository sellerRepository;
    private IBuyerRepository buyerRepository;
    private ModelMapper modelMapper;
    private IUserService userService;

    @BeforeAll
    public void setupBeforeAll(){
        this.buyerRepository = mock(IBuyerRepository.class);
        this.sellerRepository = mock(ISellerRepository.class);
        this.modelMapper = spy(new MapperUtil().modelMapper());
        this.userService = new UserServiceImp(buyerRepository,sellerRepository,modelMapper);
    }

    @BeforeEach
    public void setupReset(){
        reset(buyerRepository);
        reset(sellerRepository);
    }

    @Test
    @DisplayName("T-0001: HAPPY PATH - Verify that following functionality works correctly for buyers")
    void testBuyerFollowOk() {
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        when(buyerRepository.addFollowed(buyer,seller.getId())).thenReturn(true);
        when(sellerRepository.addFollower(seller,buyer.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.exists(buyer.getId())).thenReturn(true);
        when(sellerRepository.exists(seller.getId())).thenReturn(true);

        SuccessDTO result = userService.follow(buyer.getId(),seller.getId());

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_FOLLOW_ACTION, seller.getId()), result.getMessage());
    }


    @Test
    @DisplayName("T-0001: HAPPY PATH - Verify that following functionality works correctly for sellers")
    void testSellerFollowOk() {
        Seller seller = new Seller(2,"pepitoTest");
        Seller sellerToFollow = new Seller(6,"sellerTest");

        when(sellerRepository.addFollowed(seller,sellerToFollow.getId())).thenReturn(true);
        when(sellerRepository.addFollower(sellerToFollow,seller.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(sellerRepository.get(sellerToFollow.getId())).thenReturn(Optional.of(sellerToFollow));
        when(sellerRepository.exists(seller.getId())).thenReturn(true);
        when(sellerRepository.exists(sellerToFollow.getId())).thenReturn(true);

        SuccessDTO result = userService.follow(seller.getId(),sellerToFollow.getId());

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_FOLLOW_ACTION, sellerToFollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("T-0001: NOT FOUND - Verify that user with provided ID exists")
    void testFollowNotFound() {
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        assertThrows(NotFoundException.class,()-> userService.follow(buyer.getId(),seller.getId()));
    }

    @Test
    @DisplayName("T-0001: BAD REQUEST - Verify that user does not try to follow himself")
    void testFollowBadRequest() {
        Buyer buyer = new Buyer(5,"pepitoTest");

        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.exists(buyer.getId())).thenReturn(true);

        assertThrows(BadRequestException.class,()-> userService.follow(buyer.getId(), buyer.getId()));
    }

    @Test
    @DisplayName("T-0002: HAPPY PATH - Verify that unfollow functionality works correctly for sellers")
    void testSellerUnfollowOK() {
        Integer userId = 1;
        Integer sellerIdToUnfollow = 6;
        Seller fakeSeller = new Seller(userId, "Carolina", Set.of(2, 3), Set.of(sellerIdToUnfollow), Set.of());
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 2, 4, 5),Set.of(4, 5),Set.of());

        when(sellerRepository.get(userId)).thenReturn(Optional.of(fakeSeller));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(sellerRepository.exists(userId)).thenReturn(true);
        when(sellerRepository.exists(sellerIdToUnfollow)).thenReturn(true);

        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION, fakeSellerToUnfollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("T-0002: HAPPY PATH - Verify that unfollow functionality works correctly for buyers")
    void testBuyerUnfollowOK() {
        Integer userId = 2;
        Integer sellerIdToUnfollow = 5;
        Buyer fakeBuyer = new Buyer(userId, "Martin", Set.of(sellerIdToUnfollow));
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 4, 5),Set.of(4, 5),Set.of());

        when(buyerRepository.get(userId)).thenReturn(Optional.of(fakeBuyer));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(buyerRepository.exists(userId)).thenReturn(true);
        when(sellerRepository.exists(sellerIdToUnfollow)).thenReturn(true);

        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION, fakeSellerToUnfollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("T-0002: NOT FOUND - Verify that users with provided IDs exists")
    void testUnfollowThrowsNotFoundOnInexistantID() {
        Integer inexistentUserId = 1;
        Integer inexistentSellerId = 2;

        assertThrows(NotFoundException.class, () -> userService.unfollow(inexistentUserId, inexistentSellerId));
    }

    @Test
    @DisplayName("T-0002: BAD REQUEST - Verify that user does not try to unfollow himself")
    void testUnfollowThrowsBadRequestOnSameIDs() {
        Buyer buyer = new Buyer(5, "pepitoTest");

        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.exists(buyer.getId())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.unfollow(buyer.getId(), buyer.getId()));
    }

    @Test
    @DisplayName("T-0003: HAPPY PATH - Verify that follower list name sorting handles order parameter correctly")
    void testSortFollowersValidOrderOK() {
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String orderAsc = "NAME_ASC";
        String orderDesc = "NAME_DESC";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, null));
    }

    @Test
    @DisplayName("T-0003: BAD REQUEST - Verify that exceptions are raised when invalid order parameter is given")
    void testSortFollowersInvalidOrder() {
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String failOrderAsc = "ASC";
        String failOrderDesc = "DESC";
        String otherOrder = "EMPANADA";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, otherOrder));
    }

    @Test
    @DisplayName("T-0003: HAPPY PATH - Verify that followed list name sorting handles order parameter correctly")
    void testSortFollowedValidOrderOK() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String orderAsc = "NAME_ASC";
        String orderDesc = "NAME_DESC";

        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, null));
    }

    @Test
    @DisplayName("T-0003: BAD REQUEST - Verify that exceptions are raised when invalid order parameter is given")
    void testSortFollowedInvalidOrder() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String failOrderAsc = "ASC";
        String failOrderDesc = "DESC";
        String otherOrder = "EMPANADA";

        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, otherOrder));
    }

    @Test
    @DisplayName("T-0004: HAPPY PATH - Verify that followers list sorting works correctly")
    void testSortFollowersOK() {
        Integer sellerId = 1;
        Integer fakeUserId1 = 5, fakeUserId2 = 6, fakeUserId3 = 7;
        String order = "NAME_ASC";

        Buyer fakeUser1 = new Buyer(fakeUserId1, "Benito");
        Buyer fakeUser2 = new Buyer(fakeUserId2, "Armando");
        Buyer fakeUser3 = new Buyer(fakeUserId3, "Carlos");
        Seller seller = new Seller(
                1,
                "Vendedor #1",
                new HashSet<>(List.of(fakeUserId1, fakeUserId2, fakeUserId3)),
                new HashSet<>(),
                new HashSet<>()
        );

        UserDTO fakeUserDto1 = new UserDTO(fakeUser1.getId(), fakeUser1.getName());
        UserDTO fakeUserDto2 = new UserDTO(fakeUser2.getId(), fakeUser2.getName());
        UserDTO fakeUserDto3 = new UserDTO(fakeUser3.getId(), fakeUser3.getName());

        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(fakeUserId1)).thenReturn(Optional.of(fakeUser1));
        when(buyerRepository.get(fakeUserId2)).thenReturn(Optional.of(fakeUser2));
        when(buyerRepository.get(fakeUserId3)).thenReturn(Optional.of(fakeUser3));

        when(modelMapper.map(fakeUser1, UserDTO.class)).thenReturn(fakeUserDto1);
        when(modelMapper.map(fakeUser2, UserDTO.class)).thenReturn(fakeUserDto2);
        when(modelMapper.map(fakeUser3, UserDTO.class)).thenReturn(fakeUserDto3);

        FollowerListDTO followersInfo = userService.sortFollowers(sellerId, order);

        assertEquals("Armando", followersInfo.getFollowers().get(0).getName());
        assertEquals("Benito", followersInfo.getFollowers().get(1).getName());
        assertEquals("Carlos", followersInfo.getFollowers().get(2).getName());
    }

    @DisplayName("T-0004: HAPPY PATH - Verify that followed list sorting works correctly")
    @Test
    void testSortFollowedOK() {
        Integer userId = 1;
        Integer sellerId1 = 5, sellerId2 = 6, sellerId3 = 7;
        String order = "NAME_DESC";

        Buyer buyer = new Buyer(userId, "Batman", new HashSet<>(Set.of(sellerId1, sellerId2, sellerId3)));
        Seller seller1 = new Seller(sellerId1, "Benito");
        Seller seller2 = new Seller(sellerId2, "Armando");
        Seller seller3 = new Seller(sellerId3, "Carlos");

        UserDTO fakeUserDto1 = new UserDTO(seller1.getId(), seller1.getName());
        UserDTO fakeUserDto2 = new UserDTO(seller2.getId(), seller2.getName());
        UserDTO fakeUserDto3 = new UserDTO(seller3.getId(), seller3.getName());

        when(sellerRepository.get(userId)).thenReturn(Optional.empty());
        when(buyerRepository.get(userId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(sellerId1)).thenReturn(Optional.of(seller1));
        when(sellerRepository.get(sellerId2)).thenReturn(Optional.of(seller2));
        when(sellerRepository.get(sellerId3)).thenReturn(Optional.of(seller3));

        when(modelMapper.map(seller1, UserDTO.class)).thenReturn(fakeUserDto1);
        when(modelMapper.map(seller2, UserDTO.class)).thenReturn(fakeUserDto2);
        when(modelMapper.map(seller3, UserDTO.class)).thenReturn(fakeUserDto3);

        FollowedListDTO followersInfo = userService.sortFollowed(userId, order);

        assertEquals("Carlos", followersInfo.getFollowed().get(0).getName());
        assertEquals("Benito", followersInfo.getFollowed().get(1).getName());
        assertEquals("Armando", followersInfo.getFollowed().get(2).getName());
    }

    @DisplayName("T-0004: NOT FOUND - Verify that user with provided ID exists")
    @Test
    void testSortFollowedThrowsNotFound() {
        Integer nonExistentBuyerId = 100;

        when(buyerRepository.get(nonExistentBuyerId)).thenReturn(Optional.empty());
        when(sellerRepository.get(nonExistentBuyerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sortFollowed(nonExistentBuyerId, null));
    }

    @Test
    @DisplayName("T-0004: NOT FOUND - Verify that user with provided ID exists")
    void testSortFollowersThrowsNotFound() {
        Integer nonExistentSellerId = 1;

        when(sellerRepository.get(nonExistentSellerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sortFollowers(nonExistentSellerId, null));
    }

    @Test
    @DisplayName("T-0007: HAPPY PATH - Verify the amount of followers an user has is correct")
    void testFollowersSellersCountOk() {
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId,"SellerTest");
        seller.setFollowers(Set.of(5,4,3,2,8));

        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));

        Integer followersAmountExpected = seller.getFollowers().size();
        Integer followersAmountResult = userService.followersSellersCount(sellerId).getFollowersCount();

        assertEquals(followersAmountExpected,followersAmountResult,
                () -> String.format("El Seller con id %d tiene: %d y se esperan %d seguidores",sellerId,followersAmountResult,followersAmountExpected));
    }

    @Test
    @DisplayName("T-0007: BAD REQUEST - Verify that user with provided ID is a seller")
    void testFollowersSellersCountFailWithAnNoSeller() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId,"BuyerTest");

        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());
        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));

        assertThrows(BadRequestException.class,() -> userService.followersSellersCount(buyerId));
    }

    @Test
    @DisplayName("T-0007: NOT FOUND - Verify that user with provided ID exists")
    void testFollowersSellersCountFailWithNotFoundSeller() {
        Integer sellerId = 1;

        when(sellerRepository.get(sellerId)).thenReturn(Optional.empty());
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Class<NotFoundException> exceptionExpected = NotFoundException.class;

        assertThrows(exceptionExpected,() -> userService.followersSellersCount(sellerId));
    }
}