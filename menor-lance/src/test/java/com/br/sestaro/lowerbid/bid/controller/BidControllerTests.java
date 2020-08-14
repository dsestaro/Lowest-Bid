package com.br.sestaro.lowerbid.bid.controller;

import com.br.sestaro.lowerbid.bid.exceptions.NoBidsException;
import com.br.sestaro.lowerbid.bid.model.Result;
import com.br.sestaro.lowerbid.bid.service.BidService;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class BidControllerTests {

    @MockBean
    BidService bidService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeTestMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenPostWithInvalidNameIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"name\": \"\", \"value\" : \"0.38\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("Nome é obrigatório.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostWithNullNameIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"value\" : \"0.38\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("Nome é obrigatório.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostWithNullValueIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"name\": \"Test\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Is.is("Lance deve ser maior do que zero.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostWithNegativeValueIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"name\": \"Test\", \"value\" : \"-4\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Is.is("Lance deve ser maior do que zero.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostWithValueWithThreeDecimalCasesIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"name\": \"Test\", \"value\" : \"0.398\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Is.is("Lance deve conter o máximo de duas casas decimais.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostWithValidRequestIsSentThenValidateCorrectResponseTest() throws Exception {

        String bid = "{\"name\": \"Test\", \"value\" : \"110.39\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/lance")
                .content(bid)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenGetResultsThenValidateCorrectResponseTest() throws Exception {

        Result result = new Result();

        result.setRevenue(39.83);
        result.setValue(0.11);
        result.setWinner("Test");

        when(bidService.getResult()).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get("/resultado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.revenue", Is.is(39.83)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Is.is(0.11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.winner", Is.is("Test")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenGetResultsWithNoWinnersThenValidateCorrectResponseTest() throws Exception {

        Result result = new Result();

        result.setRevenue(39.83);
        result.setValue(0);
        result.setWinner("Sem vencedores.");

        when(bidService.getResult()).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get("/resultado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.revenue", Is.is(39.83)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", Is.is(0.0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.winner", Is.is("Sem vencedores.")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenGetResultsWithNoBidsThenValidateCorrectResponseTest() throws Exception {

        when(bidService.getResult()).thenThrow(new NoBidsException("Não houve apostas.", null));

        mockMvc.perform(MockMvcRequestBuilders.get("/resultado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
