package data;

public class CardInfo {
    private final String cardNumber;
    private final String month;
    private final String year;
    private final String owner;
    private final String cvc;


    public CardInfo(String cardNumber, String month, String year, String owner, String cvc) {
        this.cardNumber = cardNumber;
        this.month = month;
        this.year = year;
        this.owner = owner;
        this.cvc = cvc;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getOwner() {
        return owner;
    }

    public String getCvc() {
        return cvc;
    }
}
