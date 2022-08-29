package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.LeaseTransaction;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;

public class LeaseTransactionSender extends BaseTransactionSender {
    private static LeaseTransaction leaseTx;
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

    public LeaseTransaction getLeaseTx() {
        return leaseTx;
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
