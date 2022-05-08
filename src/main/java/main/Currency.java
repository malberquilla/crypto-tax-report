package main;

import java.math.BigDecimal;

public class Currency implements Comparable<Currency> {

    private BigDecimal amount;
    private String coin;

    public Currency() {
        amount = BigDecimal.ZERO;
        coin = "USD";
    }

    public Currency(BigDecimal amount, String coin) {
        this.amount = amount;
        this.coin = coin;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    @Override
    public String toString() {
        return amount + coin;
    }

    @Override
    public int compareTo(Currency o) {
        return this.getAmount().compareTo(o.getAmount());
    }

    public Currency multiply(Currency multiplier) {
        return new Currency(this.getAmount().multiply(multiplier.getAmount()),
            this.getCoin());
    }

    public Currency multiply(BigDecimal multiplier) {
        return new Currency(this.getAmount().multiply(multiplier), this.getCoin());
    }

    public Currency subtract(Currency substract) {
        return new Currency(this.getAmount().subtract(substract.getAmount()), this.getCoin());
    }

    public void add(Currency txValue) {
        this.amount = this.amount.add(txValue.getAmount());
    }
}
