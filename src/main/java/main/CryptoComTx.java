package main;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvNumber;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CryptoComTx {

    @CsvCustomBindByName(column = "Timestamp (UTC)", converter = ZonedDateTimeConverter.class)
    private ZonedDateTime date;
    @CsvBindByName(column = "Transaction Description")
    private String description;
    @CsvBindByName(column = "Currency")
    private String currency;
    @CsvBindByName(column = "Amount", locale = "en-US")
    @CsvNumber("#.########")
    private BigDecimal amount;
    @CsvBindByName(column = "To Currency")
    private String toCurrency;
    @CsvBindByName(column = "To Amount", locale = "en-US")
    private BigDecimal toAmount;
    @CsvBindByName(column = "Native Currency")
    private String nativeCurrency;
    @CsvBindByName(column = "Native Amount", locale = "en-US")
    private BigDecimal nativeAmount;
    @CsvBindByName(column = "Native Amount (in USD)", locale = "en-US")
    private BigDecimal nativeAmountInUSD;
    @CsvCustomBindByName(column = "Transaction Kind", converter = TransactionTypeConverter.class)
    private EnumTransactionType transactionType;
    @CsvBindByName(column = "Transaction Hash")
    private String transactionHash;

    public CryptoComTx() {
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public String getNativeCurrency() {
        return nativeCurrency;
    }

    public void setNativeCurrency(String nativeCurrency) {
        this.nativeCurrency = nativeCurrency;
    }

    public BigDecimal getNativeAmount() {
        return nativeAmount;
    }

    public void setNativeAmount(BigDecimal nativeAmount) {
        this.nativeAmount = nativeAmount;
    }

    public BigDecimal getNativeAmountInUSD() {
        return nativeAmountInUSD;
    }

    public void setNativeAmountInUSD(BigDecimal nativeAmountInUSD) {
        this.nativeAmountInUSD = nativeAmountInUSD;
    }

    public EnumTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(EnumTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
}
