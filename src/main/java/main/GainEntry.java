package main;

import java.time.ZonedDateTime;

public class GainEntry {

    private ZonedDateTime transmissionDate;
    private ZonedDateTime purchaseDate;
    private Currency transferValue;
    private Currency purchaseValue;

    public GainEntry() {
    }

    public GainEntry(ZonedDateTime transmissionDate, ZonedDateTime purchaseDate,
        Currency transferValue,
        Currency purchaseValue) {
        this.transmissionDate = transmissionDate;
        this.purchaseDate = purchaseDate;
        this.transferValue = transferValue;
        this.purchaseValue = purchaseValue;
    }

    public ZonedDateTime getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(ZonedDateTime transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public ZonedDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(ZonedDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Currency getTransferValue() {
        return transferValue;
    }

    public void setTransferValue(Currency transferValue) {
        this.transferValue = transferValue;
    }

    public Currency getPurchaseValue() {
        return purchaseValue;
    }

    public void setPurchaseValue(Currency purchaseValue) {
        this.purchaseValue = purchaseValue;
    }

    public Currency getGain() {
        return purchaseValue.subtract(transferValue);
    }

    @Override
    public String toString() {
        return "GainEntry{" +
            "purchaseDate=" + purchaseDate +
            ", purchaseValue=" + purchaseValue +
            ", transmissionDate=" + transmissionDate +
            ", transferValue=" + transferValue +
            ", gain=" + getGain() +
            '}';
    }
}
