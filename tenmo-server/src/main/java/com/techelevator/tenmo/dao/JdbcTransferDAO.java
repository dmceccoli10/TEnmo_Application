package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.execption.InsufficientFundsException;
import com.techelevator.tenmo.execption.InvalidAmountException;
import com.techelevator.tenmo.execption.InvalidTransactionException;
import com.techelevator.tenmo.execption.InvalidUserException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO{

   private JdbcTemplate jdbcTemplate;
   private UserDAO userDAO;

   public JdbcTransferDAO(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
      userDAO = new JdbcUserDAO(jdbcTemplate);
   }

   @Override
   public List<Transfer> getTransfers(String username) {
      String sql = "SELECT c.username AS account_from, d.username AS account_to, transfers.transfer_id, " +
      "transfers.amount, transfers.transfer_type_id, transfers.transfer_status_id " +
      "FROM transfers " +
      "INNER JOIN accounts a ON a.account_id = transfers.account_from " +
      "INNER JOIN accounts b on b.account_id = transfers.account_to " +
      "INNER JOIN users c on a.user_id = c.user_id " +
      "INNER JOIN users d on b.user_id = d.user_id " +
      "WHERE c.username = ? OR d.username = ?";
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username, username );
      List<Transfer> transfers = new ArrayList<>();
      while(results.next()) {
         transfers.add(mapTransferFromResult(results));
      }
      return transfers;
   }

   @Override
   public Transfer getTransfer(int id) {
      String sql = "SELECT * " +
            "FROM transfers " +
            "WHERE transfer_id = ?;";
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
      Transfer transfer = null;
      if(results.next()) {
         transfer = mapTransferFromResult(results);
      }
      return transfer;
   }

   @Override
   public BigDecimal transfer(String toUser, String fromUser, double amount) throws InsufficientFundsException, InvalidAmountException {
      int toUserId = userDAO.findIdByUsername(toUser);
      int fromUserId = userDAO.findIdByUsername(fromUser);

      if(amount <= 0) {
         throw new InvalidAmountException();
      }


      if(!transfer(toUserId, fromUserId, amount)){
         throw new InsufficientFundsException();
      }

      return getAccountFromUserId(toUserId).getBalance();
   }

   @Override
   public void request(String toUser, String fromUser, double amount) throws InvalidUserException, InvalidAmountException {
      int toUserId = userDAO.findIdByUsername(toUser);
      int fromUserId = userDAO.findIdByUsername(fromUser);

      Account fromAccount = getAccountFromUserId(fromUserId);
      Account toAccount = getAccountFromUserId(toUserId);

      if(amount <= 0) {
         throw new InvalidAmountException();
      }

      String sql = "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
            "VALUES ( ?, ?,  ?, ?, ?);" ;

      jdbcTemplate.update(sql,1,1,
            fromAccount.getAccountId(),
            toAccount.getAccountId(),
            amount);
   }

   private boolean transfer(int toUser, int fromUser, double amount) {

      Account fromAccount = getAccountFromUserId(fromUser);
      Account toAccount = getAccountFromUserId(toUser);

      if(fromAccount.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0) {

         String sql = "START TRANSACTION; " +

                 "UPDATE accounts " +
                 "SET balance = ? " +
                 "WHERE account_id = ?; " +

                 "UPDATE accounts " +
                 "SET balance = ? " +
                 "WHERE account_id = ?; " +

                 "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                 "VALUES ( 2, 2,  ?, ?, ?);" +

                 "COMMIT; END; ";


         jdbcTemplate.update(sql,
                 fromAccount.getBalance().subtract(BigDecimal.valueOf(amount)),
                 fromAccount.getAccountId(),
                 toAccount.getBalance().add(BigDecimal.valueOf(amount)),
                 toAccount.getAccountId(),
                 fromAccount.getAccountId(),
                 toAccount.getAccountId(),
                 amount);

         return true;
      }

      return false;
   }

   @Override
   public List<Transfer> getRequests(String username) {
      String sql = "SELECT transfers.account_from, amount, transfer_id, " +
              "transfers.transfer_type_id, transfers.transfer_status_id, users.username " +
               "FROM transfers " +
               "INNER JOIN accounts on transfers.account_from = accounts.account_id " +
               "INNER JOIN users on accounts.user_id = users.user_id " +
               "WHERE transfer_type_id = 1 AND TRANSFER_STATUS_ID = 1 AND transfers.account_to " +
              "IN (SELECT accounts.account_id FROM accounts WHERE accounts.user_id = ?); ";

      int userId = userDAO.findIdByUsername(username);
      SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
      List<Transfer> transfers = new ArrayList<>();
      while(results.next()) {
         Transfer transfer = new Transfer();
         transfer.setAmount(results.getBigDecimal("amount"));
         transfer.setTransferId(results.getInt("transfer_id"));
         transfer.setTransferType(results.getInt("transfer_type_id"));
         transfer.setFromUsername(username);
         transfer.setToUsername(results.getString("username"));

         transfers.add((transfer));
      }
      return transfers;
   }

   public void updateRequest(int transferId, int newStatus) throws InsufficientFundsException, InvalidTransactionException {
      Transfer t = getTransfer(transferId);
      String sqlGetAccounts = "SELECT account_to, account_from, transfer_status_id " +
              "FROM transfers " +
              "WHERE transfer_id = ? ";
      SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccounts, transferId);
      int accountFrom = 0;
      int accountTo = 0;
      int statusId = 0;
      if (results.next()) {
         statusId = results.getInt("transfer_status_id");
         accountTo = results.getInt("account_to");
         accountFrom = results.getInt("account_from");
      }

      if(statusId != 1) {
         throw new InvalidTransactionException();
      }

      String sqlGetAccount = "SELECT * " +
              "FROM accounts " +
              "WHERE account_id = ? ";

      Account fromAccount = null;
      results = jdbcTemplate.queryForRowSet(sqlGetAccount, accountFrom);
      if (results.next()) {
         fromAccount = mapAccountFromResult(results);
      }

      Account toAccount = null;
      results = jdbcTemplate.queryForRowSet(sqlGetAccount, accountTo);
      if (results.next()) {
         toAccount = mapAccountFromResult(results);
      }
      if (t.getAmount().compareTo(toAccount.getBalance()) > 0 && (newStatus == 2)) {
         throw new InsufficientFundsException();
      }

      if (newStatus == 3) {
         String sql = "START TRANSACTION; " +

                 "UPDATE transfers " +
                 "SET transfer_status_id = ? " +
                 "WHERE transfer_id = ?;" +

                 "COMMIT; END; ";


         jdbcTemplate.update(sql, newStatus, transferId);

      }
      else {
      String sql = "START TRANSACTION; " +

              "UPDATE accounts " +
              "SET balance = ? " +
              "WHERE account_id = ?; " +

              "UPDATE accounts " +
              "SET balance = ? " +
              "WHERE account_id = ?; " +

              "UPDATE transfers " +
              "SET transfer_status_id = ? " +
              "WHERE transfer_id = ?;" +

              "COMMIT; END; ";


      jdbcTemplate.update(sql, fromAccount.getBalance().add(t.getAmount()), fromAccount.getAccountId(), toAccount.getBalance().subtract(t.getAmount()), toAccount.getAccountId(), newStatus, transferId);
    }
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
      System.out.println(result.toString());
      account.setAccountId(result.getInt("account_id"));
      account.setUserId(result.getInt("user_id"));
      account.setBalance(result.getBigDecimal("balance"));
      return account;
   }


   private Transfer mapTransferFromResult(SqlRowSet result) {
      Transfer transfer = new Transfer();
      transfer.setTransferId(result.getInt("transfer_id"));
      transfer.setTransferType(result.getInt("transfer_type_id"));
      transfer.setTransferStatusId(result.getInt("transfer_status_id"));
      transfer.setAmount(result.getBigDecimal("amount"));
      transfer.setToUsername(result.getString("account_to"));
      transfer.setFromUsername(result.getString("account_from"));
      return transfer;
   }

   private Transfer mapTransferFromResult2(SqlRowSet result) {
      Transfer transfer = new Transfer();
      transfer.setTransferId(result.getInt("transfer_id"));
      transfer.setTransferType(result.getInt("transfer_type_id"));
      transfer.setTransferStatusId(result.getInt("transfer_status_id"));
      transfer.setAmount(result.getBigDecimal("amount"));
      transfer.setToUsername(result.getString("to_account"));
      transfer.setFromUsername(result.getString("from_account"));
      return transfer;
   }
}
