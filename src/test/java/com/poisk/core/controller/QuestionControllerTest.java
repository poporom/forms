package com.poisk.core.controller;

import com.poisk.core.model.Question;
import com.poisk.core.repository.QuestionRepository;
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
public class QuestionControllerTest {
    private static final int QUESTION_ID = 1;
    private static final int QUESTION_WRONG_ID = 2;
    private static final String QUESTION_CONTENT = "How much is the fish?";
    private Question question;
    private List list;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {

        question = new Question();
        question.setId(QUESTION_ID);
        question.setContent(QUESTION_CONTENT);
        list = new ArrayList();
        list.add(question);

        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnAllQuestions() throws Exception {

        when(questionRepository.findAll()).thenReturn(list);

        this.mockMvc.perform(get("/api/question"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", Matchers.is(QUESTION_CONTENT)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnById() throws Exception {
        when(questionRepository.getById(QUESTION_ID)).thenReturn(question);
        this.mockMvc.perform(get("/api/question/" + QUESTION_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.is(QUESTION_CONTENT)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnEmptyId() throws Exception {
       this.mockMvc.perform(get("/api/question/"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnWrongId() throws Exception {
        when(questionRepository.getById(QUESTION_ID)).thenReturn(question);
        this.mockMvc.perform(get("/api/question/" + QUESTION_WRONG_ID))
                .andExpect(status().isNotFound());
    }
}
