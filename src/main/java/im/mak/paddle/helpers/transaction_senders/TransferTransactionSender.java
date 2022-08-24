package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.TransferTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.transactions.common.Recipient;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.ADDRESS;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class TransferTransactionSender extends BaseTransactionSender {
    private long senderBalanceAfterTransaction;
    private long senderWavesBalanceAfterTransaction;
    private long recipientBalanceAfterTransaction;
    private long recipientWavesBalanceAfterTransaction;
    private TransferTransaction transferTx;
    private Base58String base58StringAttachment;
    private AssetId asset;
    private Recipient transferTo;

    private final Amount amount;
    private final Account sender;
    private final Account recipient;
    private final long fee;

    public TransferTransactionSender(Amount amount, Account sender, Account recipient, long fee) {
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
        this.fee = fee;
    }

    public void transferTransactionSender(String addressOrAlias, int version) {
        prepareSender(addressOrAlias);

        transferTx = TransferTransaction.builder(transferTo, amount)
                .attachment(base58StringAttachment)
                .version(version)
                .sender(sender.publicKey())
                .fee(fee)
                .getSignedWith(sender.privateKey());
        node().waitForTransaction(node().broadcast(transferTx).id());
        txInfo = node().getTransactionInfo(transferTx.id());
    }

    public long getSenderBalanceAfterTransaction() {
        return senderBalanceAfterTransaction;
    }

    public long getRecipientBalanceAfterTransaction() {
        return recipientBalanceAfterTransaction;
    }

    public long getSenderWavesBalanceAfterTransaction() {
        return senderWavesBalanceAfterTransaction;
    }

    public long getRecipientWavesBalanceAfterTransaction() {
        return recipientWavesBalanceAfterTransaction;
    }

    public TransferTransaction getTransferTx() {
        return transferTx;
    }

    public Base58String getBase58StringAttachment() {
        return base58StringAttachment;
    }

    public AssetId getAsset() {
        return asset;
    }

    public Amount getAmount() {
        return amount;
    }

    public Account getSender() {
        return sender;
    }

    public Account getRecipient() {
        return recipient;
    }

    public long getFee() {
        return fee;
    }

    private void prepareSender(String addressOrAlias) {
        asset = amount.assetId().isWaves() ? AssetId.as("") : amount.assetId();
        senderBalanceAfterTransaction = sender.getBalance(asset) - amount.value() - (asset.isWaves() ? MIN_FEE : 0);
        recipientBalanceAfterTransaction = recipient.getBalance(asset) + amount.value();
        base58StringAttachment = new Base58String("attachment");
        transferTo = addressOrAlias.equals(ADDRESS) ? recipient.address() : recipient.getAliases().get(0);

        if (!asset.isWaves()) {
            senderWavesBalanceAfterTransaction = sender.getWavesBalance() - fee;
            recipientWavesBalanceAfterTransaction = recipient.getWavesBalance();
        }
    }
}
