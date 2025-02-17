package com.socialmeli2.be_java_hisp_w25_g11.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.socialmeli2.be_java_hisp_w25_g11.dto.UserDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowedListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.FollowerListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.ISeller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.IUser;
import com.socialmeli2.be_java_hisp_w25_g11.repository.user.IUserRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages.SUCCESFUL_FOLLOW_ACTION;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.SuccessMessages.SUCCESFUL_UNFOLLOW_ACTION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    public void repositorySetup() {
        userRepository.clearData();
    }

    @Test
    @DisplayName("HAPPY PATH - Verifies that the follow functionality works correctly")
    public void testFollowOK() throws Exception {
        IUser userThatFollows =  userRepository.create(DummyUtils.createBuyer());
        ISeller sellerThatIsFollowed =  (ISeller) userRepository.create(DummyUtils.createSeller());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", userThatFollows.getId(), sellerThatIsFollowed.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessMessages.build(SUCCESFUL_FOLLOW_ACTION, sellerThatIsFollowed.getId())));
    }

    @Test
    @DisplayName("HAPPY PATH - Verifies that the unfollow functionality works correctly")
    public void testUnfollowOK() throws Exception {
        IUser userThatFollows =  userRepository.create(DummyUtils.createBuyer());
        ISeller sellerThatIsFollowed =  (ISeller) userRepository.create(DummyUtils.createSeller());

         userRepository.addFollowed(userThatFollows.getId(), sellerThatIsFollowed.getId());
         userRepository.addFollower(sellerThatIsFollowed.getId(), userThatFollows.getId());

        mockMvc.perform(post("/users/{userId}/unfollow/{userIdToFollow}", userThatFollows.getId(), sellerThatIsFollowed.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessMessages.build(SUCCESFUL_UNFOLLOW_ACTION, sellerThatIsFollowed.getId())));
    }

    @Test
    @DisplayName("HAPPY PATH - Verifies that the amount of followers a seller has is retrieved correctly")
    public void testFollowersCountOK() throws Exception {
        ISeller seller =  (ISeller) userRepository.create(DummyUtils.createSeller());

        IUser follower1 =  userRepository.create(DummyUtils.createBuyer());
        IUser follower2 =  userRepository.create(DummyUtils.createBuyer());

        seller.setFollowers(Set.of(follower1.getId(), follower2.getId()));

        mockMvc.perform(get("/users/{userId}/followers/count", seller.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getId()))
                .andExpect(jsonPath("$.user_name").value(seller.getName()))
                .andExpect(jsonPath("$.followers_count").value(seller.getFollowers().size()));
    }

    @Test
    @DisplayName("HAPPY PATH - Verifies that the list of people that follow a seller is retrieved correctly")
    public void testFollowersListOK() throws Exception {
        ISeller seller = (ISeller) userRepository.create(DummyUtils.createSeller());
        IUser follower1 = userRepository.create(DummyUtils.createBuyer());
        IUser follower2 = userRepository.create(DummyUtils.createBuyer());

        seller.setFollowers(Set.of(follower1.getId(), follower2.getId()));

        List<UserDTO> expectedFollowerList = seller.getFollowers()
                .stream()
                .map(v -> {
                    Optional<IUser> foundBuyer =  userRepository.findById(v);
                    assertTrue(foundBuyer.isPresent());

                    return modelMapper.map(foundBuyer.get(), UserDTO.class);
                })
                .toList();

        FollowerListDTO responseDTO = new FollowerListDTO(
                seller.getId(),
                seller.getName(),
                expectedFollowerList
        );

        ObjectWriter writer = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer();
        String expectedResponseJson = writer.writeValueAsString(responseDTO);

        mockMvc.perform(get("/users/{userId}/followers/list", seller.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getId()))
                .andExpect(jsonPath("$.user_name").value(seller.getName()))
                .andExpect(content().json(expectedResponseJson));
    }

    @Test
    @DisplayName("HAPPY PATH - Verifies that the list of people an user follows is retrieved correctly")
    public void testFollowedListOK() throws Exception {
        IUser buyer =  userRepository.create(DummyUtils.createBuyer());

        IUser followed1 =  userRepository.create(DummyUtils.createSeller());
        IUser followed2 =  userRepository.create(DummyUtils.createSeller());

        buyer.setFollowed(Set.of(followed1.getId(), followed2.getId()));

        List<UserDTO> expectedFollowedList = buyer.getFollowed()
                .stream()
                .map(v -> {
                    Optional<IUser> foundSeller =  userRepository.findById(v);
                    assertTrue(foundSeller.isPresent());
                    IUser seller = foundSeller.get();

                    return modelMapper.map(seller, UserDTO.class);
                })
                .toList();

        FollowedListDTO responseDTO = new FollowedListDTO(
                buyer.getId(),
                buyer.getName(),
                expectedFollowedList
        );

        ObjectWriter writer = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer();
        String expectedResponseJson = writer.writeValueAsString(responseDTO);

        mockMvc.perform(get("/users/{userId}/followed/list", buyer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(buyer.getId()))
                .andExpect(jsonPath("$.user_name").value(buyer.getName()))
                .andExpect(content().json(expectedResponseJson));
    }
}
