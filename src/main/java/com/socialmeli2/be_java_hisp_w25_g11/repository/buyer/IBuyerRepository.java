package com.socialmeli2.be_java_hisp_w25_g11.repository.buyer;

import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.repository.ICrudRepository;

public interface IBuyerRepository extends ICrudRepository <Buyer, Integer> {
    Boolean addFollowed(Buyer user, Integer UserIdToFollow);
    Boolean removeFollowed(Buyer user, Integer userIdToRemove);
}
