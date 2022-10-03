package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.ReissueTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;

public class ReissueTransactionSender extends BaseTransactionSender {
    private ReissueTransaction reissueTx;
    private final Account account;
    private final Amount amount;
    private final AssetId assetId;

    public ReissueTransactionSender(Account account, Amount amount, AssetId assetId) {
        this.account = account;
        this.amount = amount;
        this.assetId = assetId;
    }

    public void reissueTransactionSender(long fee, int version) {
        accountWavesBalance = account.getWavesBalance();
        balanceAfterTransaction = account.getBalance(assetId) + amount.value();
        reissueTx = ReissueTransaction.builder(amount)
                .version(version)
                .fee(fee)
                .getSignedWith(account.privateKey());
        node().waitForTransaction(node().broadcast(reissueTx).id());

        txInfo = node().getTransactionInfo(reissueTx.id());
    }

    public ReissueTransaction getReissueTx() {
        return reissueTx;
    }

    public Account getAccount() {
        return account;
    }

    public Amount getAmount() {
        return amount;
    }

    public AssetId getAssetId() {
        return assetId;
    }

}
