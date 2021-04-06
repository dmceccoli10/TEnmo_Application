package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.execption.InsufficientFundsException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;
    private UserDAO userDAO;

    public JdbcAccountDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        userDAO = new JdbcUserDAO(jdbcTemplate);
    }

    @Override
    public List<BigDecimal> getCurrentBalance(String username) {
        long id = userDAO.findIdByUsername(username);                   //getIdFromUsername(username);
        String sql = "SELECT account_id, balance " +
                "FROM accounts " +
                "INNER JOIN users ON users.user_id = accounts.user_id " +
                "WHERE users.user_id = ?; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        List<BigDecimal> accountBalances = new ArrayList<>();
        while(results.next()) {
            accountBalances.add(results.getBigDecimal("balance"));
        }
        return accountBalances;
    }

    @Override
    public List<String> getAllUsernames() {
        List<User> users = userDAO.findAll();
        List<String> usernames = new ArrayList<>();
        for(User u : users) {
            usernames.add(u.getUsername());
        }
        return usernames;
    }


    private Account getAccountFromUserId(int userId) {
        String sql  = "SELECT * " +
              "FROM accounts " +
              "INNER JOIN users ON users.user_id = accounts.user_id " +
              "WHERE users.user_id = ?; ";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

        Account account = null;
        if(results.next()) {
            account = mapAccountFromResult(results);
        }
        return account;

    }

    private Account mapAccountFromResult(SqlRowSet result) {
        Account account = new Account();
        account.setAccountId(result.getInt("account_id"));
        account.setUserId(result.getInt("user_id"));
        account.setBalance(result.getBigDecimal("balance"));
        return account;
    }


}
