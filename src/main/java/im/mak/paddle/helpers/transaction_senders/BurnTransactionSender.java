package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.BurnTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;

public class BurnTransactionSender extends BaseTransactionSender {
    private static BurnTransaction burnTx;
    private final Amount amount;
    private final Account sender;
    private final long fee;
    private final long assetBalanceAfterTransaction;
    private final int version;

    public BurnTransactionSender(Account sender, Amount amount, long fee, int version) {
        this.sender = sender;
        this.amount = amount;
        this.fee = fee;
        this.version = version;

        accountWavesBalance = sender.getBalance(AssetId.WAVES);
        balanceAfterTransaction = accountWavesBalance - fee;
        assetBalanceAfterTransaction = sender.getAssetBalance(amount.assetId()) - amount.value();
    }

    public void burnTransactionSender() {
        burnTx = BurnTransaction.builder(amount)
                .version(version)
                .sender(sender.publicKey())
                .fee(fee)
                .getSignedWith(sender.privateKey());
        node().waitForTransaction(node().broadcast(burnTx).id());

        txInfo = node().getTransactionInfo(burnTx.id());
    }

    public BurnTransaction getBurnTx() {
        return burnTx;
    }

    public Amount getAmount() {
        return amount;
    }

    public Account getSender() {
        return sender;
    }

    public long getFee() {
        return fee;
    }

    public int getVersion() {
        return version;
    }

    public long getAssetBalanceAfterTransaction() {
        return assetBalanceAfterTransaction;
    }
}
