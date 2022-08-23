package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.LeaseCancelTransaction;
import com.wavesplatform.transactions.LeaseTransaction;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class LeaseTransactionSender extends BaseTransactionSender {
    private static LeaseTransaction leaseTx;
    private static LeaseCancelTransaction leaseCancelTx;
    private static long effectiveBalanceAfterSendTransaction;
    private static long balanceAfterReceiving;
    private final Account from;
    private final Account to;

    public LeaseTransactionSender(Account from, Account to) {
        this.from = from;
        this.to = to;
    }

    public void leaseTransactionSender(long amount, long fee, int version) {
        effectiveBalanceAfterSendTransaction = from.getWavesBalanceDetails().effective() - fee - amount;
        balanceAfterReceiving = to.getWavesBalanceDetails().effective() + amount;

        leaseTx = LeaseTransaction
                .builder(to.address(), amount)
                .version(version)
                .fee(fee)
                .getSignedWith(from.privateKey());

        node().waitForTransaction(node().broadcast(leaseTx).id());

        txInfo = node().getTransactionInfo(leaseTx.id());
    }

    public void leaseCancelTransactionSender(Id index, long leaseSum, long fee, int version) {
        effectiveBalanceAfterSendTransaction = from.getWavesBalanceDetails().effective() - MIN_FEE + leaseSum;
        balanceAfterReceiving = to.getWavesBalanceDetails().effective() - leaseSum;

        leaseCancelTx = LeaseCancelTransaction
                .builder(index)
                .fee(fee)
                .version(version)
                .getSignedWith(from.privateKey());

        node().waitForTransaction(node().broadcast(leaseCancelTx).id());
        txInfo = node().getTransactionInfo(leaseCancelTx.id());
    }

    public LeaseTransaction getLeaseTx() {
        return leaseTx;
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
}
