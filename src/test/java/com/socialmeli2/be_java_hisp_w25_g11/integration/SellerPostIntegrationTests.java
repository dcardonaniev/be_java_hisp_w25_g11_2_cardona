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
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
import com.socialmeli2.be_java_hisp_w25_g11.utils.DummyUtils;
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
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private void dummySetup() {
        Buyer buyer1 = new Buyer(1, "Superman");
        Buyer buyer2 = new Buyer(2, "Aquaman");
        Seller seller1 = new Seller(3, "Batman");
        Seller seller2 = new Seller(4, "Catwoman");

        buyerRepository.createAll(List.of(buyer1, buyer2));
        sellerRepository.createAll(List.of(seller1, seller2));
    }

    @Test
    public void testPostNewProductOK() throws Exception {
        dummySetup();
        Optional<Seller> seller = sellerRepository.get(3);
        assertTrue(seller.isPresent());
        CreatePostRequestDTO payloadDTO = DummyUtils.createCreatePostRequestDTO(seller.get());

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
                .andExpect(jsonPath("$.messages", hasItem("La fecha no puede estar vacía.")))
                .andExpect(jsonPath("$.messages", hasItem("El precio no puede estar vacío")))
                .andExpect(jsonPath("$.messages", hasItem("El  id no puede estar vacío")))
                .andExpect(jsonPath("$.messages", hasItem("La categoria no puede estar vacío")))
                .andReturn();
    }

    @Test
    public void testGetFollowedPostsListOK() throws Exception {
        dummySetup();

        Optional<Buyer> optBuyer = buyerRepository.get(1);
        assertTrue(optBuyer.isPresent());
        Buyer buyer = optBuyer.get();
        buyer.setFollowed(Set.of(3));

        Optional<Seller> optSeller1 = sellerRepository.get(3);
        assertTrue(optSeller1.isPresent());
        Seller seller1 = optSeller1.get();
        seller1.setFollowers(Set.of(1));

        SellerPost post1 = DummyUtils.createNewSellerPost(seller1);
        seller1.setPosts(Set.of(post1));
        SellerPostDTO expectedPost1 = modelMapper.map(post1, SellerPostDTO.class);

        SellerPostsListDTO expectedResponseDTO = new SellerPostsListDTO(
                1,
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
    public void testGetFollowedListsReturnsInvalidID() throws Exception {
        Integer invalidUserId = 100;

        mockMvc.perform(get("/products/followed/{userId}/list", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró un usuario con el id 100"));
    }
}
