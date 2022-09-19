package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;

import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.IssueTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.IssueTransactionHandler.getAssetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcIssueCheckers {
    private final int txIndex;
    private final String address;
    private final String publicKey;
    private final IssueTransaction issueTx;

    public GrpcIssueCheckers(int txIndex, String address, String publicKey, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.address = address;
        this.publicKey = publicKey;
        this.issueTx = issueTx;
    }

    public void checkIssueGrpc(long amountBefore, long amountAfter) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(publicKey),
                () -> assertThat(getAssetName(txIndex)).isEqualTo(issueTx.name()),
                () -> assertThat(getAssetDescription(txIndex)).isEqualTo(issueTx.description()),
                () -> assertThat(getAssetAmount(txIndex)).isEqualTo(issueTx.quantity()),
                () -> assertThat(getAssetReissuable(txIndex)).isEqualTo(issueTx.reissuable()),
                () -> assertThat(getAssetDecimals(txIndex)).isEqualTo(issueTx.decimals()),
                () -> assertThat(getAssetScript(txIndex)).isEqualTo(issueTx.script().bytes()),
                () -> assertThat(getTxVersion(txIndex)).isEqualTo(IssueTransaction.LATEST_VERSION),
                () -> assertThat(getTxFeeAmount(txIndex)).isEqualTo(issueTx.fee().value()),
                () -> assertThat(getTxId(txIndex)).isEqualTo(issueTx.id().toString()),
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(address),
                // check waves balance from balances
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                // check assetId and balance from balances
                () -> assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(issueTx.assetId().toString()),
                () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(0),
                () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(issueTx.quantity()),
                // check from assets
                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(issueTx.assetId().toString()),
                () -> assertThat(getIssuerAfter(txIndex, 0)).isEqualTo(publicKey),
                // check asset info
                () -> assertThat(getNameAfter(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionAfter(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getQuantityAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getDecimalsAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getReissueAfter(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getScriptAfter(txIndex, 0)).isEqualTo(issueTx.script().bytes())
        );
    }
}
