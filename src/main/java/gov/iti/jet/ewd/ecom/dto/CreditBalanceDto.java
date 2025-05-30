package gov.iti.jet.ewd.ecom.dto;

public class CreditBalanceDto {
    public double getBalance() {
        return creditBalance;
    }
    
    public CreditBalanceDto(){}

    public CreditBalanceDto(double balance) {
        this.creditBalance = balance;
    }

    public void setBalance(double balance) {
        this.creditBalance = balance;
    }

    double creditBalance;
}
