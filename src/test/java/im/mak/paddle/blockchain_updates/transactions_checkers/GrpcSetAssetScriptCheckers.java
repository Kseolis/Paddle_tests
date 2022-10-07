package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.SetAssetScriptTransaction;
import im.mak.paddle.helpers.transaction_senders.SetAssetScriptTransactionSender;

import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptComplexityAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetAssetScriptTransactionHandler.getAssetIdFromSetAssetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetAssetScriptTransactionHandler.getScriptFromSetAssetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.ONE_WAVES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcSetAssetScriptCheckers {
    private final int txIndex;
    private final IssueTransaction issueTx;
    private final String txId;
    private final String address;
    private final String publicKey;
    private final String assetId;
    private final byte[] scriptBefore;
    private final byte[] scriptAfter;
    private final long amountBefore;
    private final long amountAfter;

    public GrpcSetAssetScriptCheckers(int txIndex, SetAssetScriptTransactionSender txSender, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.issueTx = issueTx;

        this.scriptBefore = issueTx.script().bytes();
        this.scriptAfter = txSender.getScript().bytes();
        this.assetId = txSender.getAssetId().toString();

        this.address = txSender.getAccount().address().toString();
        this.publicKey = txSender.getAccount().publicKey().toString();

        this.txId = txSender.getSetAssetScriptTx().id().toString();
        this.amountBefore = txSender.getWavesAmountBeforeSetAssetScript();
        this.amountAfter = txSender.getWavesAmountAfterSetAssetScript();
    }


    public void checkSetAssetGrpc(int complexityBefore, int complexityAfter) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(ONE_WAVES),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(SetAssetScriptTransaction.LATEST_VERSION),
                () -> assertThat(getAssetIdFromSetAssetScript(txIndex)).isEqualTo(assetId),
                () -> assertThat(getScriptFromSetAssetScript(txIndex)).isEqualTo(scriptAfter),
                () -> assertThat(getTxId(txIndex)).isEqualTo(txId),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                // check asset before set asset script
                () -> assertThat(getAssetIdFromAssetBefore(txIndex, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerBefore(txIndex, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getReissueBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getNameBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.name())),
                () -> assertThat(getDescriptionBefore(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getDecimalsBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getScriptBefore(txIndex, 0)).isEqualTo(scriptBefore),
                () -> assertThat(getScriptComplexityBefore(txIndex, 0)).isEqualTo(complexityBefore),
                // check asset after set asset script
                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(assetId),
                () -> assertThat(getIssuerAfter(txIndex, 0)).isEqualTo(publicKey),
                () -> assertThat(getQuantityAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getReissueAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getNameAfter(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionAfter(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getDecimalsAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getScriptAfter(txIndex, 0)).isEqualTo(scriptAfter),
                () -> assertThat(getScriptComplexityAfter(txIndex, 0)).isEqualTo(complexityAfter)
        );
    }
}
