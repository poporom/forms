package com.poisk.core.controller;

import com.poisk.core.model.User;
import com.poisk.core.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final int USER_ID = 1;
    private static final int USER_WRONG_ID = 2;
    private static final String USER_NAME = "New survey";
    User user;
    List list;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {

        user = new User();
        user.setId(USER_ID);
        user.setUsername(USER_NAME);
        list = new ArrayList();
        list.add(user);

        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnAllUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(list);
        this.mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", Matchers.is(USER_NAME)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnById() throws Exception {
        when(userRepository.getById(USER_ID)).thenReturn(user);
        this.mockMvc.perform(get("/api/users/" + USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is(USER_NAME)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnByUserName() throws Exception {
        when(userRepository.findByUsername(USER_NAME)).thenReturn(user);
        this.mockMvc.perform(get("/api/users/user/" + USER_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is(USER_NAME)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnByWrongId() throws Exception {
        when(userRepository.findByUsername(USER_NAME)).thenReturn(user);
        this.mockMvc.perform(get("/api/users/" + USER_WRONG_ID))
                .andExpect(status().isNotFound());
    }

}
