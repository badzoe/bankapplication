package com.bankapp.bankapplication.clients;

import com.bankapp.bankapplication.dto.integration.InvestProductRequest;
import com.bankapp.bankapplication.dto.integration.InvestmentLoginDTO;
import com.bankapp.bankapplication.dto.integration.InvestorCreation;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="Investment-Feign-Client", url = "localhost:8080")
public interface InvestmentFeignClient {

    @PostMapping("/api/investor/register")
    ResponseEntity<String> addNewUser(@RequestBody InvestorCreation investor);

    @PostMapping("/api/investor/login")
    ResponseEntity<String> authenticateAndGetToken(@RequestBody final InvestmentLoginDTO investmentLoginDTO);

    @PostMapping("/api/products/addInvestorInvestment")
    @Headers("Authorization: Bearer {token}")
    ResponseEntity<String> addProductsPerInvestor(@RequestBody InvestProductRequest investProductRequest, @RequestHeader("Authorization") String token);


}
