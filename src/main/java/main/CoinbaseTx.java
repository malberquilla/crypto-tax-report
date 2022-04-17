package main;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CoinbaseTx implements Comparable<CoinbaseTx> {

    // Timestamp,Transaction Type,Asset,Quantity Transacted,Spot Price Currency,Spot Price at Transaction,Subtotal,Total (inclusive of fees),Fees,Notes
    @CsvBindByName(column = "Timestamp")
    @CsvDate("yyyy-MM-dd'T'HH:mm:ssX")
    private ZonedDateTime timestamp;
    @CsvCustomBindByName(column = "Transaction Type", converter = TransactionTypeConverter.class)
    private EnumTransactionType transactionType;
    @CsvBindByName(column = "Asset")
    private String asset;
    @CsvBindByName(column = "Quantity Transacted", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal quantityTransacted;
    @CsvBindByName(column = "Spot Price Currency")
    private String spotPriceCurrency;
    @CsvBindByName(column = "Spot Price at Transaction", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal spotPrice;
    @CsvBindByName(column = "Subtotal", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal subtotal;
    @CsvBindByName(column = "Total (inclusive of fees)", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal total;
    @CsvBindByName(column = "Fees", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal fees;
    @CsvBindByName(column = "Notes")
    private String notes;

    public CoinbaseTx() {
    }

    public CoinbaseTx(ZonedDateTime timestamp, EnumTransactionType transactionType, String asset,
        BigDecimal quantityTransacted, String spotPriceCurrency, BigDecimal spotPrice,
        BigDecimal subtotal, BigDecimal total, BigDecimal fees, String notes) {
        this.timestamp = timestamp;
        this.transactionType = transactionType;
        this.asset = asset;
        this.quantityTransacted = quantityTransacted;
        this.spotPriceCurrency = spotPriceCurrency;
        this.spotPrice = spotPrice;
        this.subtotal = subtotal;
        this.total = total;
        this.fees = fees;
        this.notes = notes;
    }

    public double getTotalAsDouble() {
        return this.total.doubleValue();
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public EnumTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(EnumTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getQuantityTransacted() {
        return quantityTransacted;
    }

    public void setQuantityTransacted(BigDecimal quantityTransacted) {
        this.quantityTransacted = quantityTransacted;
    }

    public String getSpotPriceCurrency() {
        return spotPriceCurrency;
    }

    public void setSpotPriceCurrency(String spotPriceCurrency) {
        this.spotPriceCurrency = spotPriceCurrency;
    }

    public BigDecimal getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(BigDecimal spotPrice) {
        this.spotPrice = spotPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Tx{" +
            "timestamp=" + timestamp +
            ", transactionType='" + transactionType + '\'' +
            ", asset='" + asset + '\'' +
            ", quantityTransacted=" + quantityTransacted +
            ", spotPriceCurrency='" + spotPriceCurrency + '\'' +
            ", spotPrice=" + spotPrice +
            ", subtotal=" + subtotal +
            ", total=" + total +
            ", fees=" + fees +
            ", notes='" + notes + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CoinbaseTx coinbaseTx = (CoinbaseTx) o;
        return Objects.equals(timestamp, coinbaseTx.timestamp) && Objects.equals(
            transactionType, coinbaseTx.transactionType) && Objects.equals(asset,
            coinbaseTx.asset)
            && Objects.equals(quantityTransacted, coinbaseTx.quantityTransacted)
            && Objects.equals(spotPriceCurrency, coinbaseTx.spotPriceCurrency)
            && Objects.equals(spotPrice, coinbaseTx.spotPrice) && Objects.equals(subtotal,
            coinbaseTx.subtotal) && Objects.equals(total, coinbaseTx.total) && Objects.equals(
            fees, coinbaseTx.fees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, transactionType, asset, quantityTransacted,
            spotPriceCurrency,
            spotPrice, subtotal, total, fees);
    }

    @Override
    public int compareTo(CoinbaseTx o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }
}
