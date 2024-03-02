package com.socialmeli2.be_java_hisp_w25_g11.service.user;

import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.*;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.utils.ErrorMessages;
import com.socialmeli2.be_java_hisp_w25_g11.utils.SuccessMessages;
import org.modelmapper.ModelMapper;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.ISellerRepository;
import org.springframework.stereotype.Service;
import java.util.*;

import static com.socialmeli2.be_java_hisp_w25_g11.utils.ErrorMessages.*;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.SuccessMessages.SUCCESFUL_FOLLOW_ACTION;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION;

@Service
public class UserServiceImp implements IUserService {
    private final IBuyerRepository buyerRepository;
    private final ISellerRepository sellerRepository;
    private final ModelMapper modelMapper;

    public UserServiceImp(
        IBuyerRepository buyerRepository,
        ISellerRepository sellerRepository,
        ModelMapper modelMapper
    ) {
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
    }

    private Object getUser(Integer id) {
        if (buyerRepository.existing(id)) {
            return buyerRepository.get(id).get();
        } else if (sellerRepository.existing(id)) {
            return sellerRepository.get(id).get();
        } else {
            throw new NotFoundException(ErrorMessages.build(ErrorMessages.NON_EXISTENT_USER, id));
        }
    }

    @Override
    public SuccessDTO follow(Integer userId, Integer userIdToFollow) {
        Object user = getUser(userId);
        Object userToFollow = getUser(userIdToFollow);

        if (!(userToFollow instanceof Seller)) {
            throw new BadRequestException(ErrorMessages.build(USER_TO_FOLLOW_MUST_BE_SELLER));
        }

        if (userId.equals(userIdToFollow)) {
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_FOLLOW_HIMSELF));
        }

        if (user instanceof Buyer) {
            if (((Buyer) user).getFollowed().contains(userIdToFollow)) {
                throw new BadRequestException(ErrorMessages.build(USER_ALREADY_FOLLOWS_SELLER, userToFollow));
            }

            buyerRepository.addFollowed((Buyer) user,userIdToFollow);
            sellerRepository.addFollower((Seller) userToFollow,userId);
        } else if (user instanceof Seller) {
            if (((Seller) user).getFollowed().contains(userIdToFollow)) {
                throw new BadRequestException(ErrorMessages.build(USER_ALREADY_FOLLOWS_SELLER, userToFollow));
            }

            sellerRepository.addFollowed((Seller) user,userIdToFollow);
            sellerRepository.addFollower((Seller) userToFollow,userId);
        } else {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }
        return new SuccessDTO(SuccessMessages.build(SUCCESFUL_FOLLOW_ACTION));
    }

    @Override
    public FollowerCountDTO followersSellersCount(Integer userId) {
        Optional<Seller> seller = sellerRepository.get(userId);
        if(seller.isEmpty()){
            if(buyerRepository.get(userId).isPresent()){
                throw new BadRequestException(ErrorMessages.build(BUYER_CANNOT_HAVE_FOLLOWERS, userId));
            }

            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_SELLER, userId));
        }

        int followersCount = seller.get().getFollowers().size();
        return new FollowerCountDTO (userId, seller.get().getName(), followersCount);
    }

    private List<UserDTO> getSocialUsersList(Integer userId, String type) {
        Optional<Buyer> buyerOptional = buyerRepository.get(userId);
        Optional<Seller> sellerOptional = sellerRepository.get(userId);

        if (buyerOptional.isPresent()) {
            return getSocialListHelper(buyerOptional.get().getFollowed());
        }

        if (sellerOptional.isPresent()) {
            return type.equalsIgnoreCase("followers")
                    ? getSocialListHelper(sellerOptional.get().getFollowers())
                    : getSocialListHelper(sellerOptional.get().getFollowed());
        }

        throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
    }

    private List<UserDTO> getSocialListHelper(Set<Integer> output) {
        return output
                .stream()
                .map(v -> {
                    Optional<Buyer> buyer = buyerRepository.get(v);
                    Optional<Seller> seller = sellerRepository.get(v);
                    if (buyer.isPresent())
                        return modelMapper.map(buyer.get(), UserDTO.class);
                    else if (seller.isPresent())
                        return modelMapper.map(seller.get(), UserDTO.class);
                    else
                        throw new NotFoundException(ErrorMessages.build(LIST_USER_INFO_NOT_FOUND, v));
                })
                .toList();
    }

    @Override
    public SuccessDTO unfollow(Integer userId, Integer sellerIdToUnfollow) {
        Object user = getUser(userId);
        Object userToUnfollow = getUser(sellerIdToUnfollow);

        if (!(userToUnfollow instanceof Seller)) {
            throw new BadRequestException(ErrorMessages.build(USER_TO_UNFOLLOW_MUST_BE_SELLER));
        }

        if (user instanceof Buyer) {
            if (!((Buyer) user).getFollowed().contains(sellerIdToUnfollow)) {
                throw new BadRequestException(ErrorMessages.build(BUYER_DOES_NOT_FOLLOW_SELLER, userId, sellerIdToUnfollow));
            }

            buyerRepository.removeFollowed((Buyer) user,sellerIdToUnfollow);
            sellerRepository.removeFollower((Seller) userToUnfollow,userId);
        } else if (user instanceof Seller) {
            if (!((Seller) user).getFollowed().contains(sellerIdToUnfollow)) {
                throw new BadRequestException(ErrorMessages.build(SELLER_DOES_NOT_FOLLOW_SELLER, userId, sellerIdToUnfollow));
            }

            sellerRepository.removeFollowed((Seller) user,sellerIdToUnfollow);
            sellerRepository.removeFollower((Seller) userToUnfollow,userId);
        }

        return new SuccessDTO(SuccessMessages.build(SUCCESFUL_UNFOLLOW_ACTION));
    }

    @Override
    public FollowerListDTO sortFollowers(Integer userId, String order) {
        Optional<Seller> seller = sellerRepository.get(userId);
        Optional<Buyer> buyer = buyerRepository.get(userId);
        String name;
        if (buyer.isPresent())
            throw new BadRequestException(ErrorMessages.build(BUYER_CANNOT_HAVE_FOLLOWERS, userId));
        else if (seller.isPresent())
            name = seller.get().getName();
        else
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_SELLER, userId));

        List<UserDTO> users = this.getSocialUsersList(userId, "followers");

        if (order == null) {
            return new FollowerListDTO(
                    userId,
                    name,
                    users
            );
        }

        Comparator<UserDTO> comparator = switch (order.toLowerCase()) {
            case "name_asc" -> Comparator.comparing(UserDTO::getName);
            case "name_desc" -> Comparator.comparing(UserDTO::getName).reversed();
            default -> throw new BadRequestException(ErrorMessages.build(INVALID_NAME_ORDER_ARGUMENT));
        };

        return new FollowerListDTO(
                userId,
                name,
                users
                        .stream()
                        .sorted(comparator)
                        .toList()
        );
    }

    @Override
    public FollowedListDTO sortFollowed(Integer userId, String order) {
        Optional<Buyer> buyer = buyerRepository.get(userId);
        Optional<Seller> seller = sellerRepository.get(userId);
        String name;
        if (buyer.isPresent())
            name = buyer.get().getName();
        else if (seller.isPresent())
            name = seller.get().getName();
        else
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));

        List<UserDTO> users = this.getSocialUsersList(userId, "followed");

        if (order == null) {
            return new FollowedListDTO(
                    userId,
                    name,
                    users
            );
        }

        Comparator<UserDTO> comparator = switch (order.toLowerCase()) {
            case "name_asc" -> Comparator.comparing(UserDTO::getName);
            case "name_desc" -> Comparator.comparing(UserDTO::getName).reversed();
            default -> throw new BadRequestException(ErrorMessages.build(INVALID_NAME_ORDER_ARGUMENT));
        };

        return new FollowedListDTO(
                userId,
                name,
                users
                        .stream()
                        .sorted(comparator)
                        .toList()
        );
    }
}
