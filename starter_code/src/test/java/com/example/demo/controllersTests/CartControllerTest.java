package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private CartRepository cartRep = mock(CartRepository.class);
    private ItemRepository itemRep = mock(ItemRepository.class);
    private UserRepository userRep = mock(UserRepository.class);

    private List<Item> createListItems(){

        List<Item> itemsList = new ArrayList<>();
        for (long i = 0L; i < 4L; i++){
            Item item = new Item();
            item.setId(i+1L);
            item.setName("testItem");
            item.setDescription("testItem");
            item.setPrice(BigDecimal.valueOf(3.55));
            itemsList.add(item);
        }
        return itemsList;
    }
    private Item createOneItem() {
        Item item = new Item();
        item.setName("testItem");
        item.setDescription("testItem");
        item.setId(5L);
        item.setPrice(BigDecimal.valueOf(2));

        return item;
    }
    private Item getOneItem(){
        Item item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("testItem");
        item.setPrice(BigDecimal.valueOf(3.55));
        return item;
    }

    private User createUserWithCart(){
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        List<Item> itemsList = createListItems();
        for(Item i : itemsList){
            cart.addItem(i);
        }
        cart.setItems(itemsList);
        user.setCart(cart);

        return user;
    }

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRep);
        TestUtils.injectObjects(cartController, "itemRepository", itemRep);
        TestUtils.injectObjects(cartController, "userRepository", userRep);
    }

    @Test
    public void test_add_to_cart(){
        User user = createUserWithCart();
        Item item = createOneItem();
        when(userRep.findByUsername(user.getUsername())).thenReturn(createUserWithCart());
        when(itemRep.findById(item.getId())).thenReturn(java.util.Optional.of(createOneItem()));
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(2);
        final ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals(user.getCart().getItems().size() + 2, actualCart.getItems().size());
        BigDecimal addedPrice =new BigDecimal(modifyCartRequest.getQuantity()).multiply(item.getPrice());
        assertEquals(user.getCart().getTotal().add(addedPrice), actualCart.getTotal());
    }

    @Test
    public void test_remove_from_cart(){
        User user = createUserWithCart();
        Item item = getOneItem();
        when(userRep.findByUsername(user.getUsername())).thenReturn(createUserWithCart());
        when(itemRep.findById(item.getId())).thenReturn(java.util.Optional.of(getOneItem()));
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart actualCart = response.getBody();
        assertNotNull(actualCart);
        assertEquals(user.getCart().getItems().size() -1, actualCart.getItems().size());
        BigDecimal removedPrice =new BigDecimal(modifyCartRequest.getQuantity()).multiply(item.getPrice());
        assertEquals(user.getCart().getTotal().subtract(removedPrice), actualCart.getTotal());
    }
}
