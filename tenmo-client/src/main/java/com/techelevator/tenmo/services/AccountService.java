package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    public static String AUTH_TOKEN = "";

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        AUTH_TOKEN = authToken;
    }


    public BigDecimal[] getBalance() {
        return restTemplate.exchange(baseUrl+"balance", HttpMethod.GET, makeAuthEntity(), BigDecimal[].class).getBody();
    }

    public String[] getUsernames() {
        return restTemplate.exchange(baseUrl + "list", HttpMethod.GET, makeAuthEntity(), String[].class).getBody();
    }

    public void transfer(String toUser, BigDecimal amount) {
        restTemplate.put(baseUrl+"transfer/?toUser=" + toUser + "&amount=" + amount.toString(), makeAuthEntity());
    }

    public Transfer[] getTransfers() {
        try {
        return restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
        }
        catch (RestClientResponseException ex)
        {
            return null;
        }
    }

    public void updateTransfer(int transferId, String newStatus) {
        try {
            restTemplate.exchange(baseUrl + "requests/"+ transferId + "?status=" + newStatus, HttpMethod.PUT, makeAuthEntity(), Transfer.class );
        }
        catch (RestClientResponseException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    //Returns an HttpEntity with an Authorization Bearer header
    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    public void request(String fromUser, BigDecimal amount) {
        restTemplate.put(baseUrl+"requests/?fromUser=" + fromUser + "&amount=" + amount.toString(), makeAuthEntity());
    }

    public Transfer[] getRequests() {
        return restTemplate.exchange(baseUrl + "requests", HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
    }
}
