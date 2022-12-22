package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.UpdateAssetInfoTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;

import static im.mak.paddle.Node.node;

public class UpdateAssetInfoSender extends BaseTransactionSender {
    private final AssetId assetId;
    private final String newName;
    private final String description;
    private final Amount fee;
    private final PrivateKey senderPrivateKey;
    private UpdateAssetInfoTransaction updAssetInfoTx;
    private Id updAssetInfoTxId;

    public UpdateAssetInfoSender(AssetId assetId, String newName, String newDescription, Amount fee, PrivateKey senderPrivateKey) {
        this.assetId = assetId;
        this.newName = newName;
        this.description = newDescription;
        this.fee = fee;
        this.senderPrivateKey = senderPrivateKey;
    }

    public final void updateAssetInfoSending(int updateAssetInfoVersion, long extraFee) {
        updAssetInfoTx = UpdateAssetInfoTransaction
                .builder(assetId, newName, description)
                .fee(fee)
                .extraFee(extraFee)
                .version(updateAssetInfoVersion)
                .getSignedWith(senderPrivateKey);


        updAssetInfoTxId = updAssetInfoTx.id();
        node().broadcast(updAssetInfoTx);
        node().waitForTransaction(updAssetInfoTxId);
        txInfo = node().getTransactionInfo(updAssetInfoTxId);
    }

    public UpdateAssetInfoTransaction getUpdAssetInfoTx() {
        return updAssetInfoTx;
    }

    public Id getUpdAssetInfoTxId() {
        return updAssetInfoTxId;
    }
}
