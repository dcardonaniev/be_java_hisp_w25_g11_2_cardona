package com.socialmeli2.be_java_hisp_w25_g11.service.user;

import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.*;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.exception.BadRequestException;
import com.socialmeli2.be_java_hisp_w25_g11.exception.NotFoundException;
import org.modelmapper.ModelMapper;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.seller.ISellerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
            throw new NotFoundException("El usuario con id="+id+" no existe.");
        }
    }

    @Override
    public SuccessDTO follow(Integer userId, Integer userIdToFollow) {
        Object user = getUser(userId);
        Object userToFollow = getUser(userIdToFollow);

        if (!(userToFollow instanceof Seller)) {
            throw new BadRequestException("El usuario a seguir debe ser un vendedor.");
        }

        if (userId.equals(userIdToFollow)) {
            throw new BadRequestException("El usuario no se puede seguir a si mismo.");
        }

        if (user instanceof Buyer) {
            if (((Buyer) user).getFollowed().contains(userIdToFollow)) {
                throw new BadRequestException("El comprador con id="+userId+" ya sigue al vendedor con id="+userIdToFollow+".");
            }
            buyerRepository.addFollowed((Buyer) user,userIdToFollow);
            sellerRepository.addFollower((Seller) userToFollow,userId);
        } else if (user instanceof Seller) {
            if (((Seller) user).getFollowed().contains(userIdToFollow)) {
                throw new BadRequestException("El vendedor con id="+userId+" ya sigue al vendedor con id="+userIdToFollow+".");
            }
            sellerRepository.addFollowed((Seller) user,userIdToFollow);
            sellerRepository.addFollower((Seller) userToFollow,userId);

        } else {
            throw new BadRequestException("El usuario con id="+userId+" no es ni comprador ni vendedor.");
        }
        return new SuccessDTO("El usuario con id="+userId+" ahora sigue al vendedor con id="+userIdToFollow+".");
    }

    @Override
    public FollowerCountDTO followersSellersCount(Integer sellerId) {
        Optional<Seller> seller = sellerRepository.get(sellerId);
        if(seller.isEmpty()){
            if(buyerRepository.get(sellerId).isPresent()){
                throw new BadRequestException("Un comprador no puede tener seguidores.");
            }
            throw new NotFoundException("El vendedor con id="+sellerId+" no existe.");
        }
        int followersCount = seller.get().getFollowers().size();
        System.out.println(followersCount);
        return new FollowerCountDTO (sellerId, seller.get().getName(), followersCount);
    }

    private List<UserDTO> getSocialUsersList(Integer userId, String type) {
        Optional<Buyer> buyerOptional = buyerRepository.get(userId);
        Optional<Seller> sellerOptional = sellerRepository.get(userId);
        if (buyerOptional.isPresent()) {
            return getSocialListHelper(buyerOptional.get().getFollowed());
        } else if (sellerOptional.isPresent()) {
            return type.equalsIgnoreCase("followers")
                    ? getSocialListHelper(sellerOptional.get().getFollowers())
                    : getSocialListHelper(sellerOptional.get().getFollowed());
        } else {
            throw new NotFoundException("No se encontró un usuario con ese ID");
        }
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
                        throw new NotFoundException("No se encontró uno de los IDs");
                })
                .toList();
    }

    @Override
    public SuccessDTO unfollow(Integer userId, Integer sellerIdToUnfollow) {
        Object user = getUser(userId);
        Object userToUnfollow = getUser(sellerIdToUnfollow);

        if (!(userToUnfollow instanceof Seller)) {
            throw new BadRequestException("El usuario a dejar de seguir debe ser un vendedor.");
        }

        if (user instanceof Buyer) {
            if (!((Buyer) user).getFollowed().contains(sellerIdToUnfollow)) {
                throw new BadRequestException("El comprador con id="+userId+" no sigue al vendedor con id="+sellerIdToUnfollow+".");
            }
            buyerRepository.removeFollowed((Buyer) user,sellerIdToUnfollow);
            sellerRepository.removeFollower((Seller) userToUnfollow,userId);
        } else if (user instanceof Seller) {
            if (!((Seller) user).getFollowed().contains(sellerIdToUnfollow)) {
                throw new BadRequestException("El vendedor con id="+userId+" no sigue al vendedor con id="+sellerIdToUnfollow+".");
            }
            sellerRepository.removeFollowed((Seller) user,sellerIdToUnfollow);
            sellerRepository.removeFollower((Seller) userToUnfollow,userId);
        } else {
            throw new BadRequestException("El usuario con id="+userId+" no es ni comprador ni vendedor.");
        }
        return new SuccessDTO("El usuario con id="+userId+" ha dejado de seguir al vendedor con id="+sellerIdToUnfollow+".");
    }

    @Override
    public FollowerDTO sortFollowers(Integer userId, String order) {
        Optional<Seller> seller = sellerRepository.get(userId);
        Optional<Buyer> buyer = buyerRepository.get(userId);
        String name;
        if (buyer.isPresent())
            throw new NotFoundException("Los compradores no pueden tener seguidores");
        else if (seller.isPresent())
            name = seller.get().getName();
        else
            throw new NotFoundException("No se encontró un vendedor con ese ID");

        List<UserDTO> users = this.getSocialUsersList(userId, "followers");

        if (order == null) {
            return new FollowerDTO(
                    userId,
                    name,
                    users
            );
        }

        Comparator<UserDTO> comparator = switch (order.toLowerCase()) {
            case "name_asc" -> Comparator.comparing(UserDTO::getName);
            case "name_desc" -> Comparator.comparing(UserDTO::getName).reversed();
            default -> throw new BadRequestException("Argumento invalido (order debe ser NAME_ASC o NAME_DESC)");
        };

        return new FollowerDTO(
                userId,
                name,
                users
                        .stream()
                        .sorted(comparator)
                        .toList()
        );
    }

    @Override
    public FollowedDTO sortFollowed(Integer userId, String order) {
        Optional<Buyer> buyer = buyerRepository.get(userId);
        Optional<Seller> seller = sellerRepository.get(userId);
        String name;
        if (buyer.isPresent())
            name = buyer.get().getName();
        else if (seller.isPresent())
            name = seller.get().getName();
        else
            throw new NotFoundException("No se encontró un usuario con ese ID");

        List<UserDTO> users = this.getSocialUsersList(userId, "followed");

        if (order == null) {
            return new FollowedDTO(
                    userId,
                    name,
                    users
            );
        }

        Comparator<UserDTO> comparator = switch (order.toLowerCase()) {
            case "name_asc" -> Comparator.comparing(UserDTO::getName);
            case "name_desc" -> Comparator.comparing(UserDTO::getName).reversed();
            default -> throw new BadRequestException("Argumento invalido (order debe ser NAME_ASC o NAME_DESC)");
        };

        return new FollowedDTO(
                userId,
                name,
                users
                        .stream()
                        .sorted(comparator)
                        .toList()
        );
    }
}