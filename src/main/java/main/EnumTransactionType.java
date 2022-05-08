package main;

public enum EnumTransactionType {
    AIRDROP, STAKING, CONVERT, WITHDRAWAL, DEPOSIT, BUY, SELL, UNKNOWN;

    public static EnumTransactionType fromString(String str) {
        if (str == null) {
            return UNKNOWN;
        }
        return switch (str.toLowerCase()) {
            case "coinbase earn", "referral_gift", "reimbursement", "referral_card_cashback", "rewards_platform_deposit_credited" ->
                AIRDROP;
            case "rewards income", "pay_checkout_reward" -> STAKING;
            case "convert", "crypto_exchange" -> CONVERT;
            case "deposit", "crypto_deposit" -> DEPOSIT;
            case "send", "crypto_withdrawal" -> WITHDRAWAL;
            case "buy", "crypto_purchase" -> BUY;
            case "sell" -> SELL;
            default -> UNKNOWN;
        };
    }
}
