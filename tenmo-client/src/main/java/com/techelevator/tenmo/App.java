package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private static final String REQUEST_MENU_ACCEPT = "Accept";
	private static final String REQUEST_MENU_REJECT = "Reject";
	private static final String REQUEST_MENU_EXIT = "Don't approve or reject";
	private static final String[] REQUEST_MENU_OPTIONS = {REQUEST_MENU_ACCEPT, REQUEST_MENU_REJECT, REQUEST_MENU_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		BigDecimal[] accountBalances = accountService.getBalance();
		console.printList(accountBalances);
	}

	private void viewTransferHistory() {
		Transfer[] transfers = accountService.getTransfers();

		System.out.println("-------------------------------------------\n" +
				"Transfers\n" +
				"ID          From/To                 Amount\n" +
				"-------------------------------------------");


		for(Transfer t : transfers) {
			System.out.print(t.getTransferId());
			System.out.print("        ");
			String space = " ";
			if(t.getToUsername().equals(currentUser.getUser().getUsername())) {
				System.out.print("From: " + t.getFromUsername()+ space.repeat(18 - t.getFromUsername().length()));
			} else if (t.getFromUsername().equals(currentUser.getUser().getUsername())) {
					System.out.print("To: " + t.getToUsername()+ space.repeat(20 - t.getToUsername().length()));
				}
			System.out.println("$" + t.getAmount());
		}

		int transferID = 0;
		do {
			transferID = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel) ");

			Transfer t = null;
			for (Transfer transfer : transfers) {
				if (transfer.getTransferId() == transferID) {
					t = transfer;
				}
			}

			if (t != null) {
				String transferType = "";
				switch(t.getTransferType()) {
					case 1:
						transferType = "Request";
						break;
					case 2:
						transferType = "Send";
				}

				String transferStatus = "";
				switch(t.getTransferStatusId()) {
					case 1:
						transferStatus = "Pending";
						break;
					case 2:
						transferStatus = "Approved";
						break;
					case 3:
						transferStatus = "Rejected";
				}
				System.out.println("-------------------------------------------");
				System.out.println("Transfer Details");
				System.out.println("-------------------------------------------");
				System.out.println("Id: " + transferID);
				System.out.println("From: " + t.getFromUsername());
				System.out.println("To: " + t.getToUsername());
				System.out.println("Type: " + transferType);
				System.out.println("Status: " + transferStatus);
				System.out.println("Amount: " + t.getAmount());

			}
			else if(t == null && transferID != 0)
				System.out.println("\nInvalid transfer ID.\n");
		} while(transferID != 0);
	}

	private void viewPendingRequests() {

		Transfer[] requests = accountService.getRequests();

		System.out.println(
				"-------------------------------------------\n" +
				"Requests\n" +
				"ID          From                    Amount\n" +
				"-------------------------------------------");
		for(Transfer t : requests) {
			System.out.print(t.getTransferId());
			System.out.print("        ");
			String space = " ";

			System.out.print("From: " + t.getToUsername()+ space.repeat(18 - t.getToUsername().length()));
			System.out.println("$" + t.getAmount());
		}
		System.out.println("---------");
		int transferID = 0;
		do {

			transferID = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");

			Transfer r = null;
			for (Transfer transfer : requests) {
				if (transfer.getTransferId() == transferID) {
					r = transfer;
				}
			}
			if(transferID != 0 && r != null) {
			String choice = (String)console.getChoiceFromOptions(REQUEST_MENU_OPTIONS);

				switch (choice) {
					case REQUEST_MENU_ACCEPT:
						accountService.updateTransfer(r.getTransferId(), REQUEST_MENU_ACCEPT.toLowerCase());
						break;
					case REQUEST_MENU_REJECT:
						accountService.updateTransfer(r.getTransferId(), REQUEST_MENU_REJECT.toLowerCase());
						break;
					default:
						break;
				}
			}
			else if(r == null && transferID != 0)
				System.out.println("\nInvalid transfer ID.\n");

		} while(transferID !=0);


	}

	private void sendBucks() {
		String[] usernames = accountService.getUsernames();
		String toUser = (String) console.getChoiceFromOptions(usernames);
		int amount = console.getUserInputInteger("Enter the amount to transfer");
		try {
			accountService.transfer(toUser, BigDecimal.valueOf(amount));
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage() + '\n');
			System.out.flush();
		}
		
	}

	private void requestBucks() {
		String[] usernames = accountService.getUsernames();
		String fromUser = (String) console.getChoiceFromOptions(usernames);
		int amount = console.getUserInputInteger("Enter the amount to request");
		try {
			accountService.request(fromUser, BigDecimal.valueOf(amount));
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage() + '\n');
		}
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				accountService.setAuthToken(currentUser.getToken());
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
