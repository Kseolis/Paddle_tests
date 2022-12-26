package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.wavesj.info.TransactionInfo;

public abstract class BaseTransactionSender {
    protected long balanceAfterTransaction;
    protected long accountWavesBalance;
    protected static TransactionInfo txInfo;
    protected static int version;
    protected static long fee;
    protected static long extraFee = 0;

    public long getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public long getAccountWavesBalance() {
        return accountWavesBalance;
    }

    public TransactionInfo getTxInfo() {
        return txInfo;
    }

    public static void setFee(long fee) {
        BaseTransactionSender.fee = fee;
    }

    public static void setExtraFee(long extraFee) {
        BaseTransactionSender.extraFee = extraFee;
    }

    public static void setTxInfo(TransactionInfo txInfo) {
        BaseTransactionSender.txInfo = txInfo;
    }
}
