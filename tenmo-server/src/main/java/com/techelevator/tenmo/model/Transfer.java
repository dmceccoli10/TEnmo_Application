package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
   int transferId;
   int transferType;
   int transferStatusId;
   int accountFrom;
   int accountTo;
   String toUsername;
   String fromUsername;
   BigDecimal amount;

   public int getTransferId() {
      return transferId;
   }

   public void setTransferId(int transferId) {
      this.transferId = transferId;
   }

   public int getTransferType() {
      return transferType;
   }

   public void setTransferType(int transferType) {
      this.transferType = transferType;
   }

   public int getTransferStatusId() {
      return transferStatusId;
   }

   public void setTransferStatusId(int transferStatusId) {
      this.transferStatusId = transferStatusId;
   }

   public int getAccountFrom() {
      return accountFrom;
   }

   public void setAccountFrom(int accountFrom) {
      this.accountFrom = accountFrom;
   }

   public int getAccountTo() {
      return accountTo;
   }

   public void setAccountTo(int accountTo) {
      this.accountTo = accountTo;
   }

   public BigDecimal getAmount() {
      return amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   public String getToUsername() {
      return toUsername;
   }

   public void setToUsername(String toUsername) {
      this.toUsername = toUsername;
   }

   public String getFromUsername() {
      return fromUsername;
   }

   public void setFromUsername(String fromUsername) {
      this.fromUsername = fromUsername;
   }
}
