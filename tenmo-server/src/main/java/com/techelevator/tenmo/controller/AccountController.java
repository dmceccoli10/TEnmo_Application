package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.JdbcAccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.execption.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController

public class AccountController {

    private AccountDAO accountDAO;

    public AccountController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    //Get balance for an account
    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public List<BigDecimal> getBalances(Principal principal) {
        try {
            return accountDAO.getCurrentBalance(principal.getName());
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<String> listUsers(@Valid  Principal principal) {

        return accountDAO.getAllUsernames();
    }


}
