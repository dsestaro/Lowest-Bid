package com.br.sestaro.lowerbid.bid.data;

import com.br.sestaro.lowerbid.bid.model.Bid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BidRepository extends CrudRepository<Bid, Long> {

    List<Bid> findAll();

    @Query(value = "SELECT b.id, b.value, b.name FROM BID AS b INNER JOIN (SELECT c.value, c.name FROM BID c GROUP BY c.value HAVING count(c.value) = 1) AS a ON a.value = b.value ORDER BY b.value ASC", nativeQuery = true)
    List<Bid> findWinner();
}
