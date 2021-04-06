package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.execption.InsufficientFundsException;
import com.techelevator.tenmo.execption.InvalidAmountException;
import com.techelevator.tenmo.execption.InvalidTransactionException;
import com.techelevator.tenmo.execption.InvalidUserException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController

public class TransfersController {

   private TransferDAO transferDAO;
   private AccountDAO accountDAO;

   public TransfersController(TransferDAO transferDAO) {
      this.transferDAO = transferDAO;
   }

   @RequestMapping(value = "/transfers", method = RequestMethod.GET)
   public List<Transfer> getTransfers(Principal principal)
   {
      return transferDAO.getTransfers(principal.getName());
   }

   @RequestMapping(value = "/transfers/{id}", method = RequestMethod.GET)
   public Transfer getTransfer(@PathVariable int id)
   {
      return transferDAO.getTransfer(id);
   }

   @RequestMapping(value = "/transfer", method = RequestMethod.PUT)
   public BigDecimal transfer(@Valid @RequestParam(required = true) String toUser, @RequestParam(required = true) double amount, Principal principal) throws InsufficientFundsException, InvalidUserException, InvalidAmountException {
      if(principal.getName().equals(toUser)) {
         throw new InvalidUserException();
      }
      if(amount <= 0) {
         throw new InvalidAmountException();
      }
      transferDAO.transfer(toUser, principal.getName(), amount);
      return null;
   }

   @RequestMapping(value = "/requests", method = RequestMethod.PUT)
   public void request(@Valid Principal principal, @RequestParam(required = true) String fromUser, @RequestParam(required = true) double amount) throws InvalidUserException, InvalidAmountException {
      if(principal.getName().equals(fromUser)) {
         throw new InvalidUserException();
      }
      if(amount <= 0) {
         throw new InvalidAmountException();
      }
      transferDAO.request(fromUser, principal.getName(), amount);
   }

   @RequestMapping(value = "/requests/{id}", method = RequestMethod.PUT)
   public void updateTransfer(@Valid Principal principal, @RequestParam(required = true) String status, @PathVariable int id) throws InsufficientFundsException, InvalidTransactionException {
      int newStatus = 1;
      switch (status) {
         case "accept" :
            newStatus = 2;
            break;
         case "reject":
            newStatus = 3;
            break;
         default:
            break;
      }
      transferDAO.updateRequest(id, newStatus);
   }

   @RequestMapping(value = "/requests", method = RequestMethod.GET)
   public List<Transfer> getRequests(Principal principal)
   {
      return transferDAO.getRequests(principal.getName());
   }


}
