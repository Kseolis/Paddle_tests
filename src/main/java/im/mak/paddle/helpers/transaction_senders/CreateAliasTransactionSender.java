package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.CreateAliasTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;

public class CreateAliasTransactionSender extends BaseTransactionSender {
    private static CreateAliasTransaction createAliasTx;
    private final PrivateKey privateKey;
    private final String alias;
    private final long fee;
    private final int version;

    public CreateAliasTransactionSender(Account sender, String alias, long fee, int version) {
        this.privateKey = sender.privateKey();
        this.alias = alias;
        this.fee = fee;
        this.version = version;

        accountWavesBalance = sender.getWavesBalance();
        balanceAfterTransaction = accountWavesBalance - fee;
    }

    public void createAliasTransactionSender() {
        createAliasTx = CreateAliasTransaction
                .builder(alias)
                .fee(fee)
                .version(version)
                .getSignedWith(privateKey);

        afterSend();
    }

    public CreateAliasTransaction getCreateAliasTx() {
        return createAliasTx;
    }

    private void afterSend() {
        node().waitForTransaction(node().broadcast(createAliasTx).id());
        txInfo = node().getTransactionInfo(createAliasTx.id());
    }
}
