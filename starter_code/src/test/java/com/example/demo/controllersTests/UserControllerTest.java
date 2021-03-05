package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRep = mock(UserRepository.class);
    private CartRepository cartRep = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private User createUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");
        return user;
    }

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRep);
        TestUtils.injectObjects(userController, "cartRepository", cartRep);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void test_create_user() throws Exception{
        when(encoder.encode("password")).thenReturn("HashedPass");
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername("name");
        userReq.setPassword("password");
        userReq.setConfirmPassword("password");
        ResponseEntity<User> createdUser = userController.createUser(userReq);
        Assert.assertNotNull(createdUser);
        Assert.assertEquals(200, createdUser.getStatusCodeValue());
        User user = createdUser.getBody();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getId());
        Assert.assertEquals("name", user.getUsername());
        Assert.assertEquals("HashedPass", user.getPassword());
    }

    @Test
    public void test_find_by_username(){
        when(userRep.findByUsername("user")).thenReturn(createUser());
        final ResponseEntity<User> response = userController.findByUserName("user");
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals("user", response.getBody().getUsername());
    }

    @Test
    public void test_find_by_id(){
        when(userRep.findById(1L)).thenReturn(java.util.Optional.of(createUser()));
        final ResponseEntity<User> response = userController.findById(1L);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(1L, response.getBody().getId());
    }

}
