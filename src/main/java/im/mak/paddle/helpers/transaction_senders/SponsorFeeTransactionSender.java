package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.SponsorFeeTransaction;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.exceptions.ApiError;

import static im.mak.paddle.Node.node;
import static org.assertj.core.api.Assertions.assertThat;

public class SponsorFeeTransactionSender extends BaseTransactionSender {
    private static SponsorFeeTransaction sponsorTx;
    private final Account assetOwner;
    private final long sponsorFee;
    private final AssetId assetId;

    public SponsorFeeTransactionSender(Account assetOwner, long sponsorFee, AssetId assetId) {
        this.assetOwner = assetOwner;
        this.sponsorFee = sponsorFee;
        this.assetId = assetId;
    }

    public void sponsorFeeTransactionSender(long fee, int version) {
        sponsorTx = SponsorFeeTransaction.builder(assetId, sponsorFee)
                .version(version)
                .fee(fee)
                .getSignedWith(assetOwner.privateKey());
        node().waitForTransaction(node().broadcast(sponsorTx).id());
        txInfo = node().getTransactionInfo(sponsorTx.id());
    }

    public void cancelSponsorFeeSender(Account assetOwner, Account sender, Account recipient, int version) {
        sponsorTx = SponsorFeeTransaction.builder(assetId, 0).version(version)
                .getSignedWith(assetOwner.privateKey());
        node().waitForTransaction(node().broadcast(sponsorTx).id());

        txInfo = node().getTransactionInfo(sponsorTx.id());

        try {
            sender.transfer(recipient.address(), 1, assetId, i -> i.feeAssetId(assetId));
        } catch (ApiError e) {
            assertThat(e.getMessage()).isEqualTo("insufficient fee");
        }
    }

    public Account getAssetOwner() {
        return assetOwner;
    }

    public long getSponsorFee() {
        return sponsorFee;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public SponsorFeeTransaction getSponsorTx() {
        return sponsorTx;
    }
}
