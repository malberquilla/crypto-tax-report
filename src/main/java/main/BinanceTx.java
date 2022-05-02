package main;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvNumber;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public class BinanceTx implements Comparable<BinanceTx> {

    // Date(UTC),Pair,Side,Price,Executed,Amount,Fee
    @CsvCustomBindByName(column = "Date(UTC)", converter = ZonedDateTimeConverter.class)
    private ZonedDateTime date;
    @CsvBindByName(column = "Pair")
    private String pair;
    @CsvCustomBindByName(column = "Side", converter = TransactionTypeConverter.class)
    private EnumTransactionType transactionType;
    @CsvBindByName(column = "Price", locale = "en_US")
    @CsvNumber("###,###,###.00000000")
    private BigDecimal price;
    @CsvCustomBindByName(column = "Executed", converter = CurrencyConverter.class)
    private Currency executed;
    @CsvCustomBindByName(column = "Amount", converter = CurrencyConverter.class)
    private Currency amount;
    @CsvCustomBindByName(column = "Fee", converter = CurrencyConverter.class)
    private Currency fee;
    @CsvIgnore
    private String transactionId;
    @CsvIgnore
    private String method;

    public BinanceTx() {
        // Mandatory for OpenCSV
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public EnumTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(EnumTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getExecuted() {
        return executed;
    }

    public void setExecuted(Currency currency) {
        this.executed = currency;
    }

    public Currency getAmount() {
        return amount;
    }

    public void setAmount(Currency amount) {
        this.amount = amount;
    }

    public Currency getFee() {
        return fee;
    }

    public void setFee(Currency fee) {
        this.fee = fee;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BinanceTx binanceTx = (BinanceTx) o;
        return Objects.equals(date, binanceTx.date) && Objects.equals(pair,
            binanceTx.pair) && transactionType == binanceTx.transactionType && Objects.equals(
            price, binanceTx.price) && Objects.equals(executed, binanceTx.executed)
            && Objects.equals(amount, binanceTx.amount) && Objects.equals(fee,
            binanceTx.fee) && Objects.equals(transactionId, binanceTx.transactionId)
            && Objects.equals(method, binanceTx.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, pair, transactionType, price, executed, amount, fee,
            transactionId,
            method);
    }

    @Override
    public int compareTo(BinanceTx o) {
        return getDate().compareTo(o.getDate());
    }

    @Override
    public String toString() {
        return "BinanceTx{" +
            "date=" + date +
            ", pair='" + pair + '\'' +
            ", transactionType=" + transactionType +
            ", price=" + price +
            ", executed='" + executed + '\'' +
            ", amount='" + amount + '\'' +
            ", fee='" + fee + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", method='" + method + '\'' +
            '}';
    }
}
