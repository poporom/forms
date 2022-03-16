package com.poisk.core.controller;

import com.poisk.core.model.Survey;
import com.poisk.core.repository.SurveyRepository;
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
public class SurveyControllerTest {
    private static final String SURVEY_HASHED_ID = "1";
    private static final int SURVEY_WRONG_ID = 2;
    private static final String SURVEY_NAME = "New survey";
    Survey survey;
    List list;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    private SurveyRepository surveyRepository;

    @Before
    public void setUp() throws Exception {

        survey = new Survey();
        survey.setHashedId(SURVEY_HASHED_ID);
        survey.setName(SURVEY_NAME);
        survey.setIsActive(true);
        list = new ArrayList();
        list.add(survey);

        MockitoAnnotations.openMocks(this);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnAllSurveys() throws Exception {
        when(surveyRepository.findAll()).thenReturn(list);
        this.mockMvc.perform(get("/api/survey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", Matchers.is(SURVEY_NAME)));
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnByHashedId() throws Exception {
        when(surveyRepository.findByHashedId(SURVEY_HASHED_ID)).thenReturn(survey);
        this.mockMvc.perform(get("/api/survey/" + SURVEY_HASHED_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(SURVEY_NAME)));
    }


    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnEmptyId() throws Exception {
         this.mockMvc.perform(get("/api/survey"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    @Test
    public void testReturnWrongHashedId() throws Exception {
        when(surveyRepository.findByHashedId(SURVEY_HASHED_ID)).thenReturn(survey);
        this.mockMvc.perform(get("/api/survey/" + SURVEY_WRONG_ID))
                .andExpect(status().isNotFound());
    }
}
