package com.socialmeli2.be_java_hisp_w25_g11.unitary.service.user;
import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
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
import com.socialmeli2.be_java_hisp_w25_g11.utils.SuccessMessages;
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
    @DisplayName("HAPPY PATH - Verify that following functionality works correctly")
    void testBuyerFollowOk() {
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        when(buyerRepository.addFollowed(buyer,seller.getId())).thenReturn(true);
        when(sellerRepository.addFollower(seller,buyer.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);
        when(sellerRepository.existing(seller.getId())).thenReturn(true);

        SuccessDTO result = userService.follow(buyer.getId(),seller.getId());

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_FOLLOW_ACTION, seller.getId()), result.getMessage());
    }


    @Test
    @DisplayName("HAPPY PATH - Verify that following functionality works correctly")
    void testSellerFollowOk() {
        Seller seller = new Seller(2,"pepitoTest");
        Seller sellerToFollow = new Seller(6,"sellerTest");

        when(sellerRepository.addFollowed(seller,sellerToFollow.getId())).thenReturn(true);
        when(sellerRepository.addFollower(sellerToFollow,seller.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(sellerRepository.get(sellerToFollow.getId())).thenReturn(Optional.of(sellerToFollow));
        when(sellerRepository.existing(seller.getId())).thenReturn(true);
        when(sellerRepository.existing(sellerToFollow.getId())).thenReturn(true);

        SuccessDTO result = userService.follow(seller.getId(),sellerToFollow.getId());

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_FOLLOW_ACTION, sellerToFollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("THROWS NOT FOUND - Verify that following functionality works correctly")
    void testFollowNotFound() {
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        assertThrows(NotFoundException.class,()-> userService.follow(buyer.getId(),seller.getId()));
    }

    @Test
    @DisplayName("THROWS BAD REQUEST - Verify that following functionality works correctly")
    void testFollowBadRequest() {
        Buyer buyer = new Buyer(5,"pepitoTest");

        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);

        assertThrows(BadRequestException.class,()-> userService.follow(buyer.getId(), buyer.getId()));
    }

    @Test
    @DisplayName("HAPPY PATH - Verify the amount of followers an user has is correct")
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
    @DisplayName("THROW BAD REQUEST - Verify the amount of followers an user has is correct")
    void testFollowersSellersCountFailWithAnNoSeller() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId,"BuyerTest");

        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());
        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));

        assertThrows(BadRequestException.class,() -> userService.followersSellersCount(buyerId));
    }

    @Test
    @DisplayName("THROW NOT FOUND - Verify the amount of followers an user has is correct")
    void testFollowersSellersCountFailWithNotFoundSeller() {
        Integer sellerId = 1;

        when(sellerRepository.get(sellerId)).thenReturn(Optional.empty());
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Class<NotFoundException> exceptionExpected = NotFoundException.class;

        assertThrows(exceptionExpected,() -> userService.followersSellersCount(sellerId));
    }

    @Test
    @DisplayName("HAPPY PATH - Verify that unfollow function works correctly")
    void testSellerUnfollowTrue() {
        Integer userId = 1;
        Integer sellerIdToUnfollow = 6;
        Seller fakeSeller = new Seller(userId, "Carolina", Set.of(2, 3), Set.of(sellerIdToUnfollow), Set.of());
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 2, 4, 5),Set.of(4, 5),Set.of());

        when(sellerRepository.get(userId)).thenReturn(Optional.of(fakeSeller));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(sellerRepository.existing(userId)).thenReturn(true);
        when(sellerRepository.existing(sellerIdToUnfollow)).thenReturn(true);

        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION, fakeSellerToUnfollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("THROW NOT FOUND - Verify that unfollow function works correctly")
    void testUnfollowThrowsNotFoundOnInexistantID() {
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        assertThrows(NotFoundException.class, () -> userService.unfollow(buyer.getId(),seller.getId()));
    }

    @Test
    @DisplayName("THROW BAD REQUEST - Verify that unfollow function works correctly")
    void testUnfollowThrowsBadRequestOnSameIDs() {
        Buyer buyer = new Buyer(5, "pepitoTest");

        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.unfollow(buyer.getId(), buyer.getId()));
    }

    @Test
    @DisplayName("HAPPY PATH - Verify that unfollow function works correctly")
    void testUnfollowOK() {
        Integer userId = 2;
        Integer sellerIdToUnfollow = 5;
        Buyer fakeBuyer = new Buyer(userId, "Martin", Set.of(sellerIdToUnfollow));
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 4, 5),Set.of(4, 5),Set.of());

        when(buyerRepository.get(userId)).thenReturn(Optional.of(fakeBuyer));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(buyerRepository.existing(userId)).thenReturn(true);
        when(sellerRepository.existing(sellerIdToUnfollow)).thenReturn(true);

        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        assertEquals(SuccessMessages.build(SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION, fakeSellerToUnfollow.getId()), result.getMessage());
    }

    @Test
    @DisplayName("HAPPY PATH - Verify that follower list sorting verifies order parameter")
    void testSortFollowersValidOrderOK() {
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String orderAsc = "name_asc";
        String orderDesc = "name_desc";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, null));
    }

    @Test
    @DisplayName("THROWS BAD REQUEST - Verify that follower list sorting verifies order parameter")
    void testSortFollowersInvalidOrder() {
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String failOrderAsc = "asc";
        String failOrderDesc = "desc";
        String otherOrder = "empanada";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, otherOrder));
    }

    @Test
    @DisplayName("HAPPY PATH - Verify that followed list sorting verifies order parameter")
    void testSortFollowedValidOrder() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String orderAsc = "name_asc";
        String orderDesc = "name_desc";

        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, null));
    }

    @Test
    @DisplayName("THROWS BAD REQUEST - Verify that followed list sorting works correctly")
    void testSortFollowedInvalidOrder() {
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String failOrderAsc = "asc";
        String failOrderDesc = "desc";
        String otherOrder = "empanada";

        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, otherOrder));
    }

    @Test
    @DisplayName("HAPPY PATH - Verify that followers list sorting works correctly")
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

    @DisplayName("HAPPY PATH - Verify that followed list sorting works correctly")
    @Test
    void testSortFollowedOK() {
        Integer sellerId = 1;
        Integer fakeUserId1 = 5, fakeUserId2 = 6, fakeUserId3 = 7;
        String order = "NAME_DESC";

        Buyer fakeUser1 = new Buyer(fakeUserId1, "Armando");
        Buyer fakeUser2 = new Buyer(fakeUserId2, "Benito");
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

        assertEquals("Carlos", followersInfo.getFollowers().get(0).getName());
        assertEquals("Benito", followersInfo.getFollowers().get(1).getName());
        assertEquals("Armando", followersInfo.getFollowers().get(2).getName());
    }

    @Test
    @DisplayName("THROWS NOT FOUND - Verify that followers list sorting works correctly")
    void testSortFollowersThrowsNotFound() {
        Integer sellerId = 1;
        String order = "NAME_ASC";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sortFollowers(sellerId, order));
    }

    @Test
    @DisplayName("THROWS NOT FOUND - Verify that followed list sorting works correctly")
    void testSortFollowedThrowsNotFound() {
        Integer sellerId = 1;
        String order = "NAME_ASC";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sortFollowed(sellerId, order));
    }
}