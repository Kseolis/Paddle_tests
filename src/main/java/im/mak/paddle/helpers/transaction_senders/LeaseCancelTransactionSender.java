package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class LeaseCancelTransactionSender extends BaseTransactionSender {
    private static LeaseCancelTransaction leaseCancelTx;
    private final Account from;
    private final Account to;

    private static long effectiveBalanceAfterSendTransaction;
    private static long balanceAfterReceiving;

    private final long amountBefore;
    private final long amountAfter;
    private final long fee;

    public LeaseCancelTransactionSender(Account from, Account to, long fee) {
        this.from = from;
        this.to = to;
        this.fee = fee;
        amountBefore = from.getWavesBalance();
        amountAfter = amountBefore - fee;
    }

    public void leaseCancelTransactionSender(Id leaseId, long leaseSum, int version) {
        effectiveBalanceAfterSendTransaction = from.getWavesBalanceDetails().effective() - MIN_FEE + leaseSum;
        balanceAfterReceiving = to.getWavesBalanceDetails().effective() - leaseSum;

        leaseCancelTx = LeaseCancelTransaction
                .builder(leaseId)
                .fee(fee)
                .version(version)
                .getSignedWith(from.privateKey());

        node().waitForTransaction(node().broadcast(leaseCancelTx).id());
        txInfo = node().getTransactionInfo(leaseCancelTx.id());
    }

    public LeaseCancelTransaction getLeaseCancelTx() {
        return leaseCancelTx;
    }

    public long getEffectiveBalanceAfterSendTransaction() {
        return effectiveBalanceAfterSendTransaction;
    }

    public long getBalanceAfterReceiving() {
        return balanceAfterReceiving;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public long getAmountBefore() {
        return amountBefore;
    }

    public long getAmountAfter() {
        return amountAfter;
    }

    public long getFee() {
        return fee;
    }
}
