package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRep = mock(UserRepository.class);
    private OrderRepository orderRep = mock(OrderRepository.class);

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

    private List<UserOrder> createUserOrders(){
        User user = createUserWithCart();
        List<Item> itemList = createListItems();
        List<UserOrder> userOrdersList = new ArrayList<>();

        for (long i = 0L; i < 3L; i++){
            UserOrder userOrder = new UserOrder();
            userOrder.setId(i+1L);
            userOrder.setUser(user);
            userOrder.setItems(itemList);
            userOrder.setTotal(user.getCart().getTotal());
            userOrdersList.add(userOrder);
        }

        return userOrdersList;
    }

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRep);
        TestUtils.injectObjects(orderController, "orderRepository", orderRep);
    }

    @Test
    public void test_submit_order(){
        User userExpected = createUserWithCart();
        when(userRep.findByUsername("user")).thenReturn(userExpected);
        final ResponseEntity<UserOrder> response = orderController.submit("user");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(userExpected.getUsername(), response.getBody().getUser().getUsername());

    }

    @Test
    public void test_get_orders_for_user(){
        User userExpected = createUserWithCart();
//        System.out.println(userExpected.getId() + " " + userExpected.getUsername());
        List<UserOrder> userOrdersList = createUserOrders();
//        System.out.println(userOrdersList.get(0).getUser().getId() + " " + userOrdersList.get(0).getUser().getUsername() + " "
//        + userOrdersList.get(0).getTotal());

        when(userRep.findByUsername("user")).thenReturn(createUserWithCart());
        when(orderRep.findByUser(any())).thenReturn(userOrdersList);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user");
        List<UserOrder> actualOrders = response.getBody();
        //System.out.println(actualOrders);
        Assert.assertNotNull(response);
        Assert.assertNotNull(actualOrders);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(userOrdersList.get(0).getTotal(), actualOrders.get(0).getTotal());
        Assert.assertEquals(userOrdersList.get(0).getUser().getUsername(), actualOrders.get(0).getUser().getUsername());
    }
}
