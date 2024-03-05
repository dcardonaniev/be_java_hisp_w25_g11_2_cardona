package com.socialmeli2.be_java_hisp_w25_g11.service.user;

import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.*;
import com.socialmeli2.be_java_hisp_w25_g11.entity.ISeller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.IUserRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.UserRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;

import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages.*;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages.SUCCESFUL_FOLLOW_ACTION;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION;

@Service
public class UserServiceImp implements IUserService {
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImp(
            UserRepositoryImp userRepository,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SuccessDTO follow(Integer userId, Integer userIdToFollow) {
        if (userId.equals(userIdToFollow)) {
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_FOLLOW_HIMSELF));
        }

        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        Optional<IUser> userToFollow = userRepository.findById(userIdToFollow);
        if (userToFollow.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        if (!userToFollow.get().canBeFollowed()) {
            throw new BadRequestException(ErrorMessages.build(USER_TO_FOLLOW_MUST_BE_SELLER));
        }

        if (user.get().getFollowed().contains(userIdToFollow)) {
            throw new BadRequestException(ErrorMessages.build(USER_ALREADY_FOLLOWS_SELLER, userToFollow));
        }

        return new SuccessDTO(SuccessMessages.build(SUCCESFUL_FOLLOW_ACTION));
    }

    @Override
    public SuccessDTO unfollow(Integer userId, Integer sellerIdToUnfollow) {
        if (userId.equals(sellerIdToUnfollow)) {
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_UNFOLLOW_HIMSELF));
        }

        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        Optional<IUser> userToUnfollow = userRepository.findById(sellerIdToUnfollow);
        if (userToUnfollow.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        if (!userToUnfollow.get().canBeFollowed()) {
            throw new BadRequestException(ErrorMessages.build(USER_TO_UNFOLLOW_MUST_BE_SELLER));
        }

        if (!user.get().getFollowed().contains(sellerIdToUnfollow)) {
            throw new BadRequestException(ErrorMessages.build(USER_DOES_NOT_FOLLOW_SELLER, userToUnfollow));
        }

        return new SuccessDTO(SuccessMessages.build(SUCCESFUL_UNFOLLOW_ACTION));
    }

    @Override
    public FollowerCountDTO getSellerFollowersCount(Integer userId) {
        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        if (!user.get().canBeFollowed()) {
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_HAVE_FOLLOWERS, userId));
        }

        Integer followersCount = ((ISeller) user.get()).getFollowers().size();
        String userName = user.get().getName();

        return new FollowerCountDTO(userId, userName, followersCount);
    }

    @Override
    public FollowedListDTO getFollowedInfo(Integer userId, String order) {
        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        List<UserDTO> followed = getSocialInfo(user.get(), "followed", order);

        return new FollowedListDTO(
                user.get().getId(),
                user.get().getName(),
                followed
        );
    }

    @Override
    public FollowerListDTO getFollowersInfo(Integer userId, String order) {
        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));
        }

        if (!user.get().canBeFollowed()) {
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_HAVE_FOLLOWERS, userId));
        }

        List<UserDTO> followers = getSocialInfo(user.get(), "followers", order);

        return new FollowerListDTO(
                user.get().getId(),
                user.get().getName(),
                followers
        );
    }

    private List<UserDTO> getSocialInfo(IUser user, String type, String order) {
        List<UserDTO> users = new ArrayList<>();
        if (type.equalsIgnoreCase("followed")) {
            users = user.getFollowed()
                    .stream()
                    .map(f -> {
                        Optional<IUser> followedUser = userRepository.findById(f);
                        if (followedUser.isEmpty())
                            throw new NotFoundException(ErrorMessages.build(SELLER_INFORMATION_NOT_FOUND, f));

                        return modelMapper.map(followedUser.get(), UserDTO.class);
                    })
                    .toList();
        } else if (type.equalsIgnoreCase("followers")) {
            ISeller seller = (ISeller) user;
            users = seller.getFollowers()
                    .stream()
                    .map(f -> {
                        Optional<IUser> followerUser = userRepository.findById(f);
                        if (followerUser.isEmpty())
                            throw new NotFoundException(ErrorMessages.build(USER_INFORMATION_NOT_FOUND, f));

                        return modelMapper.map(followerUser.get(), UserDTO.class);
                    })
                    .toList();
        }

        if (order == null) {
            return users;
        }

        Comparator<UserDTO> comparator = switch (order.toLowerCase()) {
            case "name_asc" -> Comparator.comparing(UserDTO::getName);
            case "name_desc" -> Comparator.comparing(UserDTO::getName).reversed();
            default -> throw new BadRequestException(ErrorMessages.build(INVALID_NAME_ORDER_ARGUMENT));
        };

        return users
                .stream()
                .sorted(comparator)
                .toList();
    }
}
