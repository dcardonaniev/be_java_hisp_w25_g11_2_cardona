package com.socialmeli2.be_java_hisp_w25_g11.service.seller_post;

import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.*;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.SellerPostRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.IUserRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.UserRepositoryImp;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages.*;

@Service
public class SellerPostServiceImp implements ISellerPostService {
    private final ISellerPostRepository sellerPostRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    public SellerPostServiceImp(
            SellerPostRepositoryImp sellerPostRepository,
            UserRepositoryImp userRepository,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.sellerPostRepository = sellerPostRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SellerPostDTO createPost(CreatePostRequestDTO request) {
        Optional<IUser> user = userRepository.findById(request.getUserId());
        if (user.isEmpty())
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, request.getUserId()));
        if (!user.get().canPost())
            throw new BadRequestException(ErrorMessages.build(USER_CANNOT_CREATE_POSTS, request.getUserId()));

        Seller seller = (Seller) user.get();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        SellerPost sellerPost = new SellerPost(
                request.getUserId(),
                null,
                LocalDate.parse(request.getDate(), dateTimeFormatter),
                modelMapper.map(request.getProduct(), Product.class),
                request.getCategory(),
                request.getPrice(),
                seller
        );

        sellerPost = sellerPostRepository.create(sellerPost);
        seller.getPosts().add(sellerPost);

        return modelMapper.map(sellerPost, SellerPostDTO.class);
    }

    @Override
    public SellerPostsListDTO getFollowedSellersLatestPosts(Integer userId, String order) {
        Optional<IUser> user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_SELLER, userId));

        List<SellerPost> posts = getMergedPostsList(user.get().getFollowed());

        if (order == null) {
            return new SellerPostsListDTO(
                    userId,
                    posts
                            .stream()
                            .map(v -> modelMapper.map(v, SellerPostDTO.class))
                            .toList()
            );
        }

        Comparator<SellerPost> comparator = switch (order.toLowerCase()) {
            case "date_asc" -> Comparator.comparing(SellerPost::getDate);
            case "date_desc" -> Comparator.comparing(SellerPost::getDate).reversed();
            default -> throw new BadRequestException(ErrorMessages.build(INVALID_DATE_ORDER_ARGUMENT));
        };

        return new SellerPostsListDTO(
                userId,
                posts
                        .stream()
                        .sorted(comparator)
                        .map(p -> modelMapper.map(p, SellerPostDTO.class))
                        .toList()
        );
    }

    private List<SellerPost> getMergedPostsList(Set<Integer> followed) {
        return followed
                .stream()
                .map(s -> {
                    Optional<IUser> followedUser = userRepository.findById(s);
                    if (followedUser.isEmpty() || !followedUser.get().canPost())
                        throw new NotFoundException(ErrorMessages.build(SELLER_INFORMATION_NOT_FOUND, s));
                    Seller seller = (Seller) followedUser.get();

                    return seller
                            .getPosts()
                            .stream()
                            .filter(p -> p.getDate().isAfter(LocalDate.now().minusWeeks(2)))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();
    }
}