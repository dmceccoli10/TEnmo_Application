package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.execption.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    List<BigDecimal> getCurrentBalance(String username);
    List<String> getAllUsernames();





}
