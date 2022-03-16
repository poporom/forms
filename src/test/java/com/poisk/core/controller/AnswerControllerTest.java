package com.poisk.core.controller;

import com.poisk.core.model.Answer;
import com.poisk.core.repository.AnswerRepository;
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
public class AnswerControllerTest {
    private static final int ANSWER_ID = 1;
    private static final int ANSWER_WRONG_ID = 2;
    private static final int QUESTION_ID = 1;
    private static final String ANSWER_CONTENT = "I like programming";
    private Answer answer;
    private List list;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    private AnswerRepository answerRepository;

    @Before
    public void setUp() throws Exception {

        answer = new Answer();
        answer.setId(ANSWER_ID);
        answer.setContent(ANSWER_CONTENT);
        answer.setQuestionId(QUESTION_ID);

        list = new ArrayList();
        list.add(answer);

        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();


    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnAllAnswers() throws Exception {
        when(answerRepository.findAll()).thenReturn(list);
        this.mockMvc.perform(get("/api/answer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", Matchers.is(ANSWER_CONTENT)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnById() throws Exception {
        when(answerRepository.getById(ANSWER_ID)).thenReturn(answer);
        this.mockMvc.perform(get("/api/answer/" + ANSWER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.is(ANSWER_CONTENT)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnByEmptyId() throws Exception {
        this.mockMvc.perform(get("/api/answer/"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnByWrongId() throws Exception {
        when(answerRepository.getById(ANSWER_ID)).thenReturn(answer);
        this.mockMvc.perform(get("/api/answer/" + ANSWER_WRONG_ID))
                .andExpect(status().isNotFound());
    }
}
