package main;

public enum EnumTransactionType {
    AIRDROP, STAKING, CONVERT, WITHDRAWAL, DEPOSIT, BUY, SELL, UNKNOWN;

    public static EnumTransactionType fromString(String str) {
        if (str == null) {
            return UNKNOWN;
        }
        return switch (str.toLowerCase()) {
            case "coinbase earn" -> AIRDROP;
            case "rewards income" -> STAKING;
            case "convert" -> CONVERT;
            case "deposit" -> DEPOSIT;
            case "send" -> WITHDRAWAL;
            case "buy" -> BUY;
            case "sell" -> SELL;
            default -> UNKNOWN;
        };
    }
}
