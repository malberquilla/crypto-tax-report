package main;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Locale;

public class Transaction implements Comparable<Transaction> {

    private String platform;
    private String transactionId;
    private ZonedDateTime date;
    private EnumTransactionType transactionType;
    private Currency spotPrice;
    private Currency executed;
    private Currency subTotal;
    private Currency fees;
    private Currency converted;

    public Transaction(BinanceTx tx) {
        this.platform = "Binance";
        this.transactionId = tx.getTransactionId();
        this.date = tx.getDate();
        this.transactionType = tx.getTransactionType();
        var coin = tx.getExecuted().getCoin();
        var spotCoin = tx.getPair().replaceAll(coin, "");
        this.spotPrice = new Currency(tx.getPrice(), spotCoin);
        this.executed = tx.getExecuted();
        this.subTotal = tx.getAmount();
        this.fees = tx.getFee();
    }

    public Transaction(CoinbaseTx tx) {
        this.platform = "Coinbase";
        this.date = tx.getDate();
        this.transactionType = tx.getTransactionType();
        this.spotPrice = new Currency(tx.getSpotPrice(), tx.getSpotPriceCurrency());
        this.executed = new Currency(tx.getQuantityTransacted(), tx.getAsset());
        this.subTotal = new Currency(tx.getSubtotal(), tx.getSpotPriceCurrency());
        this.fees = new Currency(tx.getFees(), tx.getSpotPriceCurrency());
        // For conversion, extract from notes
        if (EnumTransactionType.CONVERT.equals(this.transactionType)) {
            var notes = tx.getNotes();
            var notesTokens = notes.split(" to ");
            var to = notesTokens[1];
            var matcher = CoinbaseTx.COINBASE_CURRENCY_PATTERN.matcher(to);
            if (matcher.find()) {
                NumberFormat format = NumberFormat.getInstance(Locale.ITALIAN);
                Number parseConverted;
                try {
                    parseConverted = format.parse(matcher.group(1));
                } catch (ParseException e) {
                    parseConverted = BigDecimal.ZERO;
                }
                this.converted = new Currency(new BigDecimal(parseConverted.toString()),
                    matcher.group(2));
            }
        }
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public EnumTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(EnumTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Currency getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(Currency spotPrice) {
        this.spotPrice = spotPrice;
    }

    public Currency getExecuted() {
        return executed;
    }

    public void setExecuted(Currency executed) {
        this.executed = executed;
    }

    public Currency getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Currency subTotal) {
        this.subTotal = subTotal;
    }

    public Currency getFees() {
        return fees;
    }

    public void setFees(Currency fees) {
        this.fees = fees;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "platform='" + platform + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", date=" + date +
            ", transactionType=" + transactionType +
            ", spotPrice=" + spotPrice +
            ", executed=" + executed +
            ", amount=" + subTotal +
            ", fees=" + fees +
            ", converted=" + converted +
            '}';
    }

    @Override
    public int compareTo(Transaction o) {
        return 0;
    }
}
