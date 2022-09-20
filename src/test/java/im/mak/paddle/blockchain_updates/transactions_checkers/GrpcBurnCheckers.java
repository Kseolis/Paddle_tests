package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;

import static com.wavesplatform.transactions.BurnTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.BurnTransactionHandler.getBurnAssetAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.BurnTransactionHandler.getBurnAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcBurnCheckers {
    private final int txIndex;
    private final String address;
    private final String publicKey;
    private final IssueTransaction issueTx;

    public GrpcBurnCheckers(int txIndex, String address, String publicKey, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.address = address;
        this.publicKey = publicKey;
        this.issueTx = issueTx;
    }


    private void checkBurnSubscribe(String assetId, long amount, long burnAssetFee, byte[] script) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(burnAssetFee),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getBurnAssetId(0)).isEqualTo(assetId),
                () -> assertThat(getBurnAssetAmount(0)).isEqualTo(amount),
                () -> assertThat(getTransactionId()).isEqualTo(getTransactionId()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(wavesAmountBeforeBurn),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(wavesAmountAfterBurn),
                // check asset balance
                () -> assertThat(getAddress(0, 1)).isEqualTo(address),
                () -> assertThat(getAssetIdAmountAfter(0, 1)).isEqualTo(assetId),
                () -> assertThat(getAmountBefore(0, 1)).isEqualTo(assetQuantity),
                () -> assertThat(getAmountAfter(0, 1)).isEqualTo(quantityAfterBurn),
                // check asset before burn
                () -> assertThat(getAssetIdFromAssetBefore(0, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerBefore(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityBefore(0, 0)).isEqualTo(String.valueOf(assetQuantity)),
                () -> assertThat(getReissueBefore(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameBefore(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionBefore(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsBefore(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptBefore(0, 0)).isEqualTo(script),
                // check asset after burn
                () -> assertThat(getAssetIdFromAssetAfter(0, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerAfter(0, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityAfter(0, 0)).isEqualTo(String.valueOf(quantityAfterBurn)),
                () -> assertThat(getReissueAfter(0, 0)).isEqualTo(String.valueOf(true)),
                () -> assertThat(getNameAfter(0, 0)).isEqualTo(assetName),
                () -> assertThat(getDescriptionAfter(0, 0)).isEqualTo(assetDescription),
                () -> assertThat(getDecimalsAfter(0, 0)).isEqualTo(String.valueOf(assetDecimals)),
                () -> assertThat(getScriptAfter(0, 0)).isEqualTo(script)
        );
    }
}
