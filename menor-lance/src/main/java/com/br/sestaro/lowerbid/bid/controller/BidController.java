package com.br.sestaro.lowerbid.bid.controller;

import com.br.sestaro.lowerbid.bid.exceptions.NoBidsException;
import com.br.sestaro.lowerbid.bid.model.Bid;
import com.br.sestaro.lowerbid.bid.model.Result;
import com.br.sestaro.lowerbid.bid.service.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class BidController {

    private BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "/lance", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<String> sendBid(@Valid @RequestBody Bid bid) {

        bidService.saveBid(bid);

        return ResponseEntity.ok("Lance registrado com sucesso.");
    }

    @GetMapping(value = "/resultado", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Result> getResult() {

        return ResponseEntity.ok(bidService.getResult());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoBidsException.class)
    public String handleNoBidsExceptionExceptions(
            NoBidsException ex) {

        return ex.getMessage();
    }
}
