package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.SponsorFeeTransaction;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.exceptions.ApiError;

import static im.mak.paddle.Node.node;
import static org.assertj.core.api.Assertions.assertThat;

public class SponsorFeeTransactionSender extends BaseTransactionSender {
    private static SponsorFeeTransaction sponsorTx;

    public static void sponsorFeeTransactionSender(Account account, long sponsorFee, AssetId assetId, long fee, int version) {

        sponsorTx = SponsorFeeTransaction.builder(assetId, sponsorFee)
                .version(version)
                .fee(fee)
                .getSignedWith(account.privateKey());
        node().waitForTransaction(node().broadcast(sponsorTx).id());
        txInfo = node().getTransactionInfo(sponsorTx.id());
    }

    public static void cancelSponsorFeeSender
            (Account assetOwner, Account sender, Account recipient, AssetId assetId, int version) {

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

    public static SponsorFeeTransaction getSponsorTx() {
        return sponsorTx;
    }
}
