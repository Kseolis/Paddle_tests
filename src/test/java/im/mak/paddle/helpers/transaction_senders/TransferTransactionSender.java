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
    private static long senderBalanceAfterTransaction;
    private static long recipientBalanceAfterTransaction;
    private static TransferTransaction transferTx;
    private static Base58String base58StringAttachment;
    private static AssetId asset;

    public static void transferTransactionSender
            (Amount amount, Account from, Account to, String addressOrAlias, long fee, int version) {
        asset = amount.assetId();
        senderBalanceAfterTransaction = from.getBalance(asset) - amount.value() - (asset.isWaves() ? MIN_FEE : 0);
        recipientBalanceAfterTransaction = to.getBalance(asset) + amount.value();
        base58StringAttachment = new Base58String("attachment");
        Recipient transferTo = addressOrAlias.equals(ADDRESS) ? to.address() : to.getAliases().get(0);

        transferTx = TransferTransaction.builder(transferTo, amount)
                .attachment(base58StringAttachment)
                .version(version)
                .sender(from.publicKey())
                .fee(fee)
                .getSignedWith(from.privateKey());
        node().waitForTransaction(node().broadcast(transferTx).id());
        txInfo = node().getTransactionInfo(transferTx.id());
    }

    public static long getSenderBalanceAfterTransaction() {
        return senderBalanceAfterTransaction;
    }

    public static long getRecipientBalanceAfterTransaction() {
        return recipientBalanceAfterTransaction;
    }

    public static TransferTransaction getTransferTx() {
        return transferTx;
    }

    public static Base58String getBase58StringAttachment() {
        return base58StringAttachment;
    }

    public static AssetId getAsset() {
        return asset;
    }

}