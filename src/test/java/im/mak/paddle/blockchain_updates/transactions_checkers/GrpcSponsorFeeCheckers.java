package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;
import im.mak.paddle.helpers.transaction_senders.SponsorFeeTransactionSender;

import static com.wavesplatform.transactions.SponsorFeeTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptComplexityAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.SponsorFeeTransactionHandler.getAmountFromSponsorFee;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.SponsorFeeTransactionHandler.getAssetIdFromSponsorFee;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTransactionVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcSponsorFeeCheckers {
    private final int txIndex;
    private final IssueTransaction issueTx;
    private final long sponsorFeeAmount;
    private final long fee;
    private final long wavesAmountBefore;
    private final long wavesAmountAfter;

    private final String assetId;
    private final String assetOwnerPublicKey;
    private final String assetOwnerAddress;
    private final String txId;

    public GrpcSponsorFeeCheckers(int txIndex, SponsorFeeTransactionSender txSender, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.issueTx = issueTx;

        this.wavesAmountBefore = txSender.getWavesAmountBefore();
        this.wavesAmountAfter = txSender.getWavesAmountAfter();
        this.sponsorFeeAmount = txSender.getSponsorFee();
        this.assetId = txSender.getAssetId().toString();
        this.assetOwnerPublicKey = txSender.getAssetOwner().publicKey().toString();
        this.assetOwnerAddress = txSender.getAssetOwner().address().toString();
        this.txId = txSender.getSponsorTx().id().toString();
        this.fee = txSender.getSponsorTx().fee().value();
    }

    public void checkSponsorFeeGrpc() {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(assetOwnerPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getAssetIdFromSponsorFee(txIndex)).isEqualTo(assetId),
                () -> assertThat(getAmountFromSponsorFee(txIndex)).isEqualTo(sponsorFeeAmount),
                () -> assertThat(getTxId(txIndex)).isEqualTo(txId),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(assetOwnerAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(wavesAmountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(wavesAmountAfter),
                // check asset before sponsor fee transaction
                () -> assertThat(getAssetIdFromAssetBefore(txIndex, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerBefore(txIndex, 0)).isEqualTo(assetOwnerPublicKey),
                () -> assertThat(getDecimalsBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getNameBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.name())),
                () -> assertThat(getDescriptionBefore(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getReissueBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getQuantityBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getScriptComplexityBefore(txIndex, 0)).isEqualTo(0),
                // check asset after sponsor fee transaction
                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerAfter(txIndex, 0)).isEqualTo(assetOwnerPublicKey),
                () -> assertThat(getDecimalsAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getNameAfter(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionAfter(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getReissueAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getQuantityAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getScriptComplexityAfter(txIndex, 0)).isEqualTo(0)
        );
    }
}
