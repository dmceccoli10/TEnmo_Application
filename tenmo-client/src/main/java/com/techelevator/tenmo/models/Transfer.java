package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {

   int transferId;
   int transferType;
   int transferStatusId;
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

   public BigDecimal getAmount() {
      return amount;
   }

   public void setAmount(BigDecimal amount) {
      this.amount = amount;
   }

   @Override
   public String toString() {
      String returnValue = transferId + "   " + (transferType==1? " Request   " : " Send     ");
      switch(transferStatusId) {
         case 1:
            returnValue += " Pending     ";
            break;
         case 2:
            returnValue += " Approved    ";
            break;
         case 3:
            returnValue += " Rejected    ";
            break;
      }
      String space = " ";
      returnValue += " From: " + fromUsername;
      returnValue += space.repeat(8 - fromUsername.length()) + " To: " + toUsername;
      returnValue +=  space.repeat(8 - toUsername.length()) + " Amount: $" + amount.toString();
      return returnValue;
   }
}


