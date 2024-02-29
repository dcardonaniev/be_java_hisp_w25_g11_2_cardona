package com.socialmeli2.be_java_hisp_w25_g11.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.socialmeli2.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.CreatePostRequestDTO;
import com.socialmeli2.be_java_hisp_w25_g11.dto.request.ProductDTO;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Buyer;
import com.socialmeli2.be_java_hisp_w25_g11.entity.Seller;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Buyer buyer = new Buyer(1, "Superman");
        Seller seller = new Seller(2, "Batman");

        buyerRepository.create(buyer);
        sellerRepository.create(seller);
    }

    @Test
    public void testPostNewProduct() throws Exception {
        dummySetup();
        CreatePostRequestDTO payloadDTO = new CreatePostRequestDTO(
                2,
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
                2,
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
}
