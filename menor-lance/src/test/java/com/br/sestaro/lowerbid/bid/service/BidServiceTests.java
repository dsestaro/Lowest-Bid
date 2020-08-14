package com.br.sestaro.lowerbid.bid.service;

import com.br.sestaro.lowerbid.bid.data.BidRepository;
import com.br.sestaro.lowerbid.bid.exceptions.NoBidsException;
import com.br.sestaro.lowerbid.bid.model.Bid;
import com.br.sestaro.lowerbid.bid.model.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BidServiceTests {

    @MockBean
    BidRepository bidRepository;

    @Autowired
    BidService bidService;

    @TestConfiguration
    static class bidServiceConfiguration {

        @Bean
        public BidService bidService() {
            return new BidService();
        }
    }

    @BeforeTestMethod
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NoBidsException.class)
    public void whenGetResultsWithNoBidsTest() throws Exception {

        when(bidRepository.count()).thenReturn((long) 0);

        bidService.getResult();
    }

    @Test
    public void whenGetResultsWithWinnerTest() throws Exception {

        when(bidRepository.count()).thenReturn((long) 4);

        List<Bid> bids = new ArrayList<>();

        Bid lowestBid = new Bid();
        lowestBid.setId((long) 5);
        lowestBid.setName("Winner");
        lowestBid.setValue(0.94);

        Bid highestBid = new Bid();
        highestBid.setId((long) 3);
        highestBid.setName("Loser");
        highestBid.setValue(0.99);

        bids.add(lowestBid);
        bids.add(highestBid);

        when(bidRepository.findWinner()).thenReturn(bids);

        Result result = bidService.getResult();

        assertEquals(result.getWinner(), "Winner");
        assertEquals(result.getValue(), 0.94, 0);
        assertEquals(result.getRevenue(), 3.92, 0);
    }

    @Test
    public void whenGetResultsWithNoWinnerTest() throws Exception {

        when(bidRepository.count()).thenReturn((long) 4);

        List<Bid> bids = new ArrayList<>();

        when(bidRepository.findWinner()).thenReturn(bids);

        Result result = bidService.getResult();

        assertEquals(result.getWinner(), "Sem vencedores.");
        assertEquals(result.getValue(), 0, 0);
        assertEquals(result.getRevenue(), 3.92, 0);
    }

    @Test
    public void whenSaveBidWithMoreThan999BidsTest() throws Exception {

        when(bidRepository.count()).thenReturn((long) 999);

        Bid bid = new Bid();
        bid.setName("Test");
        bid.setValue(0.94);

        bidService.saveBid(bid);

        verify(bidRepository, times(0)).save(bid);
    }

    @Test
    public void whenSaveBidWithLessThan999BidsTest() throws Exception {

        when(bidRepository.count()).thenReturn((long) 1);

        Bid bid = new Bid();
        bid.setName("Test");
        bid.setValue(0.94);

        bidService.saveBid(bid);

        verify(bidRepository, times(1)).save(bid);
    }
}
