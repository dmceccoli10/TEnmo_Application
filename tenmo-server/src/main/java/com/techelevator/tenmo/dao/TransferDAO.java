package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.execption.InsufficientFundsException;
import com.techelevator.tenmo.execption.InvalidAmountException;
import com.techelevator.tenmo.execption.InvalidTransactionException;
import com.techelevator.tenmo.execption.InvalidUserException;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
   List<Transfer> getTransfers(String username);
   Transfer getTransfer(int id);
   BigDecimal transfer(String toUser, String fromUser, double amount) throws InsufficientFundsException, InvalidAmountException;
   void request(String fromUser, String toUser, double amount) throws InvalidUserException, InvalidAmountException;
   List<Transfer> getRequests(String fromUser);
   void updateRequest(int transferId, int newStatus) throws InsufficientFundsException, InvalidTransactionException;


}
