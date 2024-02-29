package com.socialmeli2.be_java_hisp_w25_g11.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.ProductDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Product;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
import com.socialmeli2.be_java_hisp_w25_g11.entity.SellerPost;
import com.socialmeli2.be_java_hisp_w25_g11.repository.buyer.IBuyerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller.seller.ISellerRepository;
import com.socialmeli2.be_java_hisp_w25_g11.repository.seller_post.ISellerPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
        CreatePostRequestDTO payloadDTO = new CreatePostRequestDTO(
                3,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                new ProductDTO(
                        1,
                        "Pista carros",
                        "Jugueteria",
                        "LEGO",
                        "Varios",
                        "Disponible por tiempo limitado"
                ),
                1,
                100.0
        );

        SellerPostDTO responseDTO = new SellerPostDTO(
                3,
                0,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                new ProductDTO(
                        1,
                        "Pista carros",
                        "Jugueteria",
                        "LEGO",
                        "Varios",
                        "Disponible por tiempo limitado"
                ),                1,
                100.0
        );

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
    public void testGetFollowedPostsListOK() throws Exception {
        dummySetup();

        Optional<Buyer> buyer = buyerRepository.get(1);
        assertTrue(buyer.isPresent());
        buyer.get().setFollowed(Set.of(3, 4));

        Optional<Seller> optSeller1 = sellerRepository.get(3);
        assertTrue(optSeller1.isPresent());
        Seller seller1 = optSeller1.get();
        seller1.setFollowers(Set.of(1));
        seller1.setPosts(Set.of(
                new SellerPost(
                        seller1.getId(),
                        1,
                        LocalDate.now(),
                        new Product(
                                1,
                                "Figura Toy Story",
                                "Jugueteria",
                                "Mattel",
                                "Azul",
                                "Buen juguete"
                        ),
                        1,
                        100.0,
                        seller1
                )
        ));

        SellerPostsListDTO expectedResponseDTO = new SellerPostsListDTO(
                1,
                List.of(
                        new SellerPostDTO(
                                3,
                                1,
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                new ProductDTO(
                                        1,
                                        "Figura Toy Story",
                                        "Jugueteria",
                                        "Mattel",
                                        "Azul",
                                        "Buen juguete"
                                ),
                                1,
                                100.0
                        )
                )
        );

        ObjectWriter writer = new ObjectMapper()
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .writer();
        String expectedResponseJson = writer.writeValueAsString(expectedResponseDTO);


        MvcResult response = mockMvc.perform(get("/products/followed/{userId}/list", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedResponseJson, response.getResponse().getContentAsString());
    }
}
