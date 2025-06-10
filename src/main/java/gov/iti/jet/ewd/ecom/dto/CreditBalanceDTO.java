package gov.iti.jet.ewd.ecom.dto;

public class CreditBalanceDTO {
    public double getBalance() {
        return creditBalance;
    }
    
    public CreditBalanceDTO(){}

    public CreditBalanceDTO(double balance) {
        this.creditBalance = balance;
    }

    public void setBalance(double balance) {
        this.creditBalance = balance;
    }

    double creditBalance;
}
