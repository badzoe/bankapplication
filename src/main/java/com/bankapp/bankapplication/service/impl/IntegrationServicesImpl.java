package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.config.JwtTokenProvider;
import com.bankapp.bankapplication.dto.integration.InvestProductRequest;
import com.bankapp.bankapplication.dto.integration.InvestmentLoginDTO;
import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;
import com.bankapp.bankapplication.entity.User;
import com.bankapp.bankapplication.entity.Tokens;
import com.bankapp.bankapplication.repository.TokensRepository;
import com.bankapp.bankapplication.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@AllArgsConstructor
@Service
@Slf4j
public class IntegrationServicesImpl implements IntegrationServices {
    /**
     * @param username
     * @param password
     * @return
     */

    private final com.bankapp.bankapplication.clients.InvestmentFeignClient investmentFeignClient;
    private final UserRepository userRepository;
    private final TokensRepository tokensRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Override
    public String loginToInvestmentService(String username, String password) {
        log.info(username);
        log.info(password);

        ResponseEntity<String> response = investmentFeignClient.authenticateAndGetToken(new InvestmentLoginDTO(username, password));
        if(response.getStatusCode().is2xxSuccessful()){

            Boolean savedToken =saveInvestmentsToken(response.getBody().toString(),userRepository.findByEmail(username).get());
            return savedToken ? "Successfully saved Investment JWT Token" : "Failed to save Investment JWT Token";



        }else {
            return "Failed to connect to Investment Micro Service ";

        }
    }

    /**
     * @param investmentTransactionsDTO
     * @return
     */
    @Override
    public ResponseEntity<String> transactOnInvestments(InvestmentTransactionsDTO investmentTransactionsDTO,String token) {
        switch (investmentTransactionsDTO.transactionType()){
            case 'W'->
            {

                //ndanzwa ngungo yekuisa code iyi in another class and doing seperation of concerns
                String url = "http://localhost:8080/api/withdrawals/{userId}/{productId}/{amount}";

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer "+token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>("", headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class,
                        jwtTokenProvider.getUserIdFromToken(token),
                        investmentTransactionsDTO.productID(),
                        investmentTransactionsDTO.balance()
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    // ADD LOGIC TO DEDUCT THE BALANCE AND ALSO DONT FORGET TO CHECK IF THE TRANSACTION HE WANTS TO PERFORM HE OR SHE  HAS THE BALANCE
                    return ResponseEntity.ok().body(response.getBody().toString());
                } else {
                    return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                }

            }
            case 'D'-> {
                log.info("deposit");
                ResponseEntity<String> response =investmentFeignClient.addProductsPerInvestor(new InvestProductRequest(jwtTokenProvider.getUserIdFromToken(token), investmentTransactionsDTO.productID(), investmentTransactionsDTO.balance()), "Bearer "+token);
                log.info(response.toString());
                if(response.getStatusCode().is2xxSuccessful()) {

                    // ADD LOGIC TO DEDUCT THE BALANCE AND ALSO DONT FORGET TO CHECK IF THE TRANSACTION HE WANTS TO PERFORM HE OR SHE  HAS THE BALANCE

                    // Deduct from the relavant account
                    return  ResponseEntity.ok("Investment of "+ investmentTransactionsDTO.balance() + "  was successful");

                }else {
                    return  ResponseEntity.status (response.getStatusCode()).body("Investment of "+ investmentTransactionsDTO.balance() + " was unsuccessful because" + response.getBody().toString());
                }

            }
            default -> {
                log.info("transaction type not recognised");
                return ResponseEntity.badRequest().body("Transaction not recognised within the system");
            }
        }
    }

    private boolean saveInvestmentsToken(String token, final User user ){
        Tokens newTokens = new Tokens();
        newTokens.setId(null);
        newTokens.setToken(token);
        newTokens.setUser(user);
        newTokens.setCreateDate(new Date());
        newTokens.setExpiryDate(jwtTokenProvider.getExpirationDate(token));
        Tokens saved =tokensRepository.save(newTokens);
        return saved != null;
    }
}