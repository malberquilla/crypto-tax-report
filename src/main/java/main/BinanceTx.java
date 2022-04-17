package main;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
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
    @CsvBindByName(column = "Executed")
    private String executed;
    @CsvBindByName(column = "Amount")
    private String amount;
    @CsvBindByName(column = "Fee")
    private String fee;

    public BinanceTx() {
    }

    public BinanceTx(ZonedDateTime date, String pair, EnumTransactionType transactionType,
        BigDecimal price, String executed, String amount, String fee) {
        this.date = date;
        this.pair = pair;
        this.transactionType = transactionType;
        this.price = price;
        this.executed = executed;
        this.amount = amount;
        this.fee = fee;
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
            '}';
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

    public String getExecuted() {
        return executed;
    }

    public void setExecuted(String executed) {
        this.executed = executed;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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
            binanceTx.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, pair, transactionType, price, executed, amount, fee);
    }

    @Override
    public int compareTo(BinanceTx o) {
        return getDate().compareTo(o.getDate());
    }
}