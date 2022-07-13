package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.wavesj.info.TransactionInfo;

public class BaseTransactionSender {
    protected static long balanceAfterTransaction;
    protected static long accountWavesBalance;
    protected static TransactionInfo txInfo;

    public static long getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public static void setBalanceAfterTransaction(long balanceAfterTransaction) {
        BaseTransactionSender.balanceAfterTransaction = balanceAfterTransaction;
    }

    public static long getAccountWavesBalance() {
        return accountWavesBalance;
    }

    public static TransactionInfo getTxInfo() {
        return txInfo;
    }

}
