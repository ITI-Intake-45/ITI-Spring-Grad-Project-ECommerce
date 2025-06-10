package gov.iti.jet.ewd.ecom.controller;

import gov.iti.jet.ewd.ecom.dto.LoginRequestDTO;
import gov.iti.jet.ewd.ecom.entity.*;
import gov.iti.jet.ewd.ecom.repository.*;
import gov.iti.jet.ewd.ecom.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void placeOrder() throws Exception {
        // Arrange
        User user = getUserTestWithPlainText();
        String plainPassword = user.getPassword();

        user = userService.createUser(user);

        List<ProductCategory> categories = getProductCategoriesTestList();
        categories = productCategoryRepository.saveAll(categories);

        List<Product> products = getProductsTestList(categories);
        products = productRepository.saveAll(products);

        Cart cart = getCartTest(user, products);
        cartRepository.save(cart);

        // Act - Login and get session
        LoginRequestDTO loginRequestDto = new LoginRequestDTO();
        loginRequestDto.setEmail(user.getEmail());
        loginRequestDto.setPassword(plainPassword);

        String loginJson = objectMapper.writeValueAsString(loginRequestDto);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        // Get the session from the login request
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // Use the session for the order placement request
        String result = mockMvc.perform(post("/api/v1/orders/{userId}", user.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Disabled until I add in the cart.
//        // Assert
//        assertAll(
//                () -> assertTrue(result.contains("Order #")),
//                () -> assertTrue(result.contains("created successfully"))
//        );
//
//        Optional<Order> orderOptional = orderRepository.findByOrderId(1);
//        Order order = orderOptional.orElse(null);
//
//        assertNotNull(order);
//        assertNotNull(order.getUser());
//        assertEquals(user.getUserId(), order.getUser().getUserId());
    }

    private User getUserTestWithPlainText() {
        User user = new User();

        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("Test User");
        user.setAddress("123 Test Street");
        user.setCreditBalance(1000);
        user.setPhone("+235704422");

        return user;
    }

    private List<ProductCategory> getProductCategoriesTestList() {
        List<ProductCategory> categories = new ArrayList<>();

        categories.add(new ProductCategory("Coffee"));
        categories.add(new ProductCategory("Tea"));

        return categories;
    }

    private List<Product> getProductsTestList(List<ProductCategory> categories) {
        List<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.setName(categories.getFirst().getName() + " 1" );
        product1.setDescription(categories.getFirst().getName() + " 1 description" );
        product1.setPrice(20);
        product1.setCategory(categories.getFirst());
        product1.setImage(categories.getFirst().getName() + " 1.png");
        product1.setStock(10);

        Product product2 = new Product();
        product2.setName(categories.get(1).getName() + " 1" );
        product2.setDescription(categories.get(1).getName() + " 1 description" );
        product2.setPrice(10);
        product2.setCategory(categories.get(1));
        product2.setImage(categories.get(1).getName() + " 1.png");
        product2.setStock(5);

        products.add(product1);
        products.add(product2);

        return products;
    }

    private Cart getCartTest(User user, List<Product> products) {
        Cart cart = cartRepository.findByCartId(user.getUserId());

        Product product1 = products.get(0);
        Product product2 = products.get(1);

        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(product1);
        cartItem1.setQuantity(2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(1);

        cart.addItem(cartItem1);
        cart.addItem(cartItem2);

        return cart;
    }
}