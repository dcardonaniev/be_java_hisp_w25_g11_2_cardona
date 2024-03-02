package com.socialmeli2.be_java_hisp_w25_g11.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages;
import com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ValidationMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages.INVALID_DATE_ORDER_ARGUMENT;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ErrorMessages.NON_EXISTENT_USER;
import static com.socialmeli2.be_java_hisp_w25_g11.utils.messages.ValidationMessages.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SellerPostIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ISellerPostRepository sellerPostRepository;

    @Autowired
    private IBuyerRepository buyerRepository;

    @Autowired
    private ISellerRepository sellerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    public void repositorySetup() {
        sellerPostRepository.clearData();
        buyerRepository.clearData();
        sellerRepository.clearData();
    }

    @Test
    public void testPostNewProductOK() throws Exception {
        Seller seller = sellerRepository.create(DummyUtils.createSeller());

        CreatePostRequestDTO payloadDTO = DummyUtils.createCreatePostRequestDTO(seller);
        SellerPostDTO responseDTO = modelMapper.map(payloadDTO, SellerPostDTO.class);
        responseDTO.setPostId(0);
        responseDTO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        ObjectWriter writer = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer();

        String payloadJson = writer.writeValueAsString(payloadDTO);
        String responseJson = writer.writeValueAsString(responseDTO);

        MvcResult response = mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(responseJson, response.getResponse().getContentAsString());
    }

    @Test
    public void testPostNewProductReturnsInvalidRequestFormat() throws Exception {
        String payloadJson = "{}";

        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages", hasSize(4)))
                .andExpect(jsonPath("$.messages", hasItem(ValidationMessages.DATE_CANNOT_BE_EMPTY)))
                .andExpect(jsonPath("$.messages", hasItem(ValidationMessages.PRICE_CANNOT_BE_EMPTY)))
                .andExpect(jsonPath("$.messages", hasItem(ValidationMessages.USER_ID_CANNOT_BE_EMPTY)))
                .andExpect(jsonPath("$.messages", hasItem(ValidationMessages.CATEGORY_CANNOT_BE_EMPTY)))
                .andReturn();
    }

    @Test
    public void testGetFollowedPostsListOK() throws Exception {
        Buyer buyer = buyerRepository.create(DummyUtils.createBuyer());
        Seller seller = sellerRepository.create(DummyUtils.createSeller());
        buyer.setFollowed(Set.of(seller.getId()));
        seller.setFollowers(Set.of(buyer.getId()));

        SellerPost post = DummyUtils.createNewSellerPost(seller);
        seller.setPosts(Set.of(post));
        SellerPostDTO expectedPost1 = modelMapper.map(post, SellerPostDTO.class);

        SellerPostsListDTO expectedResponseDTO = new SellerPostsListDTO(
                buyer.getId(),
                List.of(expectedPost1)
        );

        ObjectWriter writer = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer();
        String expectedResponseJson = writer.writeValueAsString(expectedResponseDTO);

        MvcResult response = mockMvc.perform(get("/products/followed/{userId}/list", buyer.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").exists())
                .andReturn();

        assertEquals(expectedResponseJson, response.getResponse().getContentAsString());
    }

    @Test
    public void testGetFollowedPostsListReturnsInvalidOrder() throws Exception {
        Buyer buyer = buyerRepository.create(DummyUtils.createBuyer());
        String invalidOrder = "RANDOM_WORD";

        mockMvc.perform((get("/products/followed/{userId}/list", buyer.getId()))
                        .param("order", invalidOrder)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessages.build(INVALID_DATE_ORDER_ARGUMENT)));
    }

    @Test
    public void testGetFollowedListsReturnsInvalidID() throws Exception {
        Integer invalidUserId = 100;

        mockMvc.perform(get("/products/followed/{userId}/list", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ErrorMessages.build(NON_EXISTENT_USER, invalidUserId)));
    }
}