package com.socialmeli2.be_java_hisp_w25_g11.controller;

import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowedListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerCountDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SuccessDTO;
import com.socialmeli2.be_java_hisp_w25_g11.service.user.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService){
        this.userService = userService;
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<SuccessDTO> follow(
        @PathVariable Integer userId,
        @PathVariable Integer userIdToFollow
    ) {
        return new ResponseEntity<>(userService.follow(userId, userIdToFollow), HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowerCountDTO> followersCount(
        @PathVariable Integer userId
    ) {
        return new ResponseEntity<>(userService.getSellerFollowersCount(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowerListDTO>  followersList(
        @PathVariable Integer userId,
        @RequestParam(required = false) String order
    ) {
        return ResponseEntity.ok(this.userService.getFollowersInfo(userId, order));
    }

    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<FollowedListDTO> followedList(
        @PathVariable Integer userId,
        @RequestParam(required = false) String order
    ) {
        return ResponseEntity.ok(this.userService.getFollowedInfo(userId, order));
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<SuccessDTO> unfollow(
        @PathVariable Integer userId,
        @PathVariable Integer userIdToUnfollow
    ) {
        return new ResponseEntity<>(userService.unfollow(userId, userIdToUnfollow), HttpStatus.OK);
    }
}
