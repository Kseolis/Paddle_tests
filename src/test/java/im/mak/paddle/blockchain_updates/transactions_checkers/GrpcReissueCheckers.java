package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.ReissueTransactionSender;

import static com.wavesplatform.transactions.ReissueTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.ReissueTransactionHandler.getReissueAssetAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.ReissueTransactionHandler.getReissueAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.SUM_FEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcReissueCheckers {
    private final int txIndex;
    private final IssueTransaction issueTx;

    private final String senderPublicKey;
    private final String senderAddress;

    private final String reissueTxId;
    private final String reissuedAssetId;
    private final long reissueAssetAmount;
    private final long wavesBeforeReissue;
    private final long wavesAfterReissue;

    public GrpcReissueCheckers(int txIndex, Account sender, ReissueTransactionSender txSender, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.issueTx = issueTx;

        reissueTxId = txSender.getReissueTx().id().toString();
        reissuedAssetId = txSender.getAmount().assetId().toString();
        reissueAssetAmount = txSender.getAmount().value();

        wavesBeforeReissue = txSender.getAccountWavesBalance();
        wavesAfterReissue = wavesBeforeReissue - SUM_FEE;

        senderPublicKey = sender.publicKey().toString();
        senderAddress = sender.address().toString();
    }

    public void checkReissueSubscribe
            (long assetAmountBefore, long assetAmountAfter, long quantityBefore, long quantityAfter) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(SUM_FEE),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getReissueAssetAmount(txIndex)).isEqualTo(reissueAssetAmount),
                () -> assertThat(getReissueAssetId(txIndex)).isEqualTo(reissuedAssetId),
                () -> assertThat(getTxId(txIndex)).isEqualTo(reissueTxId),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(wavesBeforeReissue),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(wavesAfterReissue),
                // check asset balance
                () -> assertThat(getAddress(txIndex, 1)).isEqualTo(senderAddress),
                () -> assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(reissuedAssetId),
                () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(assetAmountBefore),
                () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(assetAmountAfter),
                // check asset before reissue
                () -> assertThat(getAssetIdFromAssetBefore(txIndex, 0)).isEqualTo(reissuedAssetId),
                () -> assertThat(getIssuerBefore(txIndex, 0)).isEqualTo(senderPublicKey),
                () -> assertThat(getQuantityBefore(txIndex, 0)).isEqualTo(String.valueOf(quantityBefore)),
                () -> assertThat(getReissueBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getNameBefore(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionBefore(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getDecimalsBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getScriptBefore(txIndex, 0)).isEqualTo(issueTx.script().bytes()),
                // check asset after reissue
                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(reissuedAssetId),
                () -> assertThat(getIssuerAfter(txIndex, 0)).isEqualTo(senderPublicKey),
                () -> assertThat(getQuantityAfter(txIndex, 0)).isEqualTo(String.valueOf(quantityAfter)),
                () -> assertThat(getReissueAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getNameAfter(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionAfter(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getDecimalsAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getScriptAfter(txIndex, 0)).isEqualTo(issueTx.script().bytes())
        );
    }
}
