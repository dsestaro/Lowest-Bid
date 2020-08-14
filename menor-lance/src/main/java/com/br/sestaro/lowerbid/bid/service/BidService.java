package com.br.sestaro.lowerbid.bid.service;

import com.br.sestaro.lowerbid.bid.data.BidRepository;
import com.br.sestaro.lowerbid.bid.exceptions.NoBidsException;
import com.br.sestaro.lowerbid.bid.model.Bid;
import com.br.sestaro.lowerbid.bid.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public void saveBid(Bid bid) {
        if(bidRepository.count() != 999) {

            bidRepository.save(bid);
        }
    }

    public Result getResult() {

        Result result = new Result();

        if(bidRepository.count() == 0) {

            throw new NoBidsException("Não houve apostas.", null);
        }

        List<Bid> lowestBids = bidRepository.findWinner();

        if(lowestBids.size() != 0) {

            result.setWinner(lowestBids.get(0).getName());
            result.setValue(lowestBids.get(0).getValue());
        } else {
            result.setWinner("Sem vencedores.");
            result.setValue(0);
        }

        result.setRevenue(bidRepository.count() * 0.98);

        return result;
    }
}
