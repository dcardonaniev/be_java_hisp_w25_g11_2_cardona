package com.socialmeli2.be_java_hisp_w25_g11.service.seller_post;

import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Product;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
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
    private final IBuyerRepository buyerRepository;
    private final ISellerRepository sellerRepository;
    private final ModelMapper modelMapper;

    public SellerPostServiceImp(
            ISellerPostRepository sellerPostRepository,
            IBuyerRepository buyerRepository,
            ISellerRepository sellerRepository,
            ModelMapper modelMapper
    ) {
        this.sellerPostRepository = sellerPostRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SellerPostDTO createPost(CreatePostRequestDTO request) {
        Optional<Seller> seller = sellerRepository.get(request.getUserId());
        if (seller.isEmpty())
            throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_SELLER, request.getUserId()));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        SellerPost sellerPost = new SellerPost(
                request.getUserId(),
                null,
                LocalDate.parse(request.getDate(), dateTimeFormatter),
                modelMapper.map(request.getProduct(), Product.class),
                request.getCategory(),
                request.getPrice(),
                seller.get()
        );

        sellerPost = sellerPostRepository.create(sellerPost);
        seller.get().getPosts().add(sellerPost);

        return modelMapper.map(sellerPost, SellerPostDTO.class);
    }

    @Override
    public SellerPostsListDTO getFollowedSellersLatestPosts(Integer userId, String order) {
        List<SellerPost> posts;

        Optional<Buyer> buyer = buyerRepository.get(userId);
        Optional<Seller> seller = sellerRepository.get(userId);
        if (buyer.isPresent())
            posts = getMergedPostsList(buyer.get().getFollowed());
        else if (seller.isPresent())
            posts = getMergedPostsList(seller.get().getFollowed());
        else throw new NotFoundException(ErrorMessages.build(NON_EXISTENT_USER, userId));

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
        List<SellerPost> posts;
        posts = followed
                .stream()
                .map(s -> {
                    Optional<Seller> followedSeller = sellerRepository.get(s);
                    if (followedSeller.isEmpty())
                        throw new NotFoundException(ErrorMessages.build(SELLER_INFORMATION_NOT_FOUND, s));

                    return followedSeller
                            .get()
                            .getPosts()
                            .stream()
                            .filter(p -> p.getDate().isAfter(LocalDate.now().minusWeeks(2)))
                            .toList();
                })
                .flatMap(List::stream)
                .toList();

        return posts;
    }
}