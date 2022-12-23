package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;
import im.mak.paddle.helpers.transaction_senders.UpdateAssetInfoSender;

import static com.wavesplatform.transactions.UpdateAssetInfoTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.getScriptAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.UpdateAssetInfoTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcUpdateAssetInfoCheckers {
    private final String senderPublicKeyString;
    private final String senderAddressString;
    private final String assetIdString;
    private final String txId;
    private final String newAssetName;
    private final String newAssetDescription;
    private final long feeAmountValue;
    private final IssueTransaction issueInfo;

    public GrpcUpdateAssetInfoCheckers(UpdateAssetInfoSender txSender, IssueTransaction issueInfo) {
        this.senderPublicKeyString =  txSender.getUpdAssetInfoTx().sender().toString();
        this.senderAddressString =  txSender.getUpdAssetInfoTx().sender().address().toString();
        this.feeAmountValue = txSender.getUpdAssetInfoTx().fee().value();
        this.assetIdString = txSender.getUpdAssetInfoTx().assetId().toString();
        this.txId = txSender.getUpdAssetInfoTxId().toString();
        this.newAssetName = txSender.getUpdAssetInfoTx().name();
        this.newAssetDescription = txSender.getUpdAssetInfoTx().description();
        this.issueInfo = issueInfo;
    }

    public void checkUpdateAssetInfo(int txIndex, long amountBefore, long amountAfter) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKeyString),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(feeAmountValue),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getUpdateAssetInfoTransactionAssetId(txIndex)).isEqualTo(assetIdString),
                () -> assertThat(getUpdateAssetInfoTransactionName(txIndex)).isEqualTo(newAssetName),
                () -> assertThat(getUpdateAssetInfoTransactionDescription(txIndex)).isEqualTo(newAssetDescription),
                () -> assertThat(getTxId(txIndex)).isEqualTo(txId),
                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID, amountBefore, amountAfter),
                () -> assertThat(getAssetIdFromAssetBefore(txIndex, 0)).isEqualTo(assetIdString),
                () -> assertThat(getIssuerBefore(txIndex, 0)).isEqualTo(senderPublicKeyString),

                () -> assertThat(getQuantityBefore(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.quantity())),
                () -> assertThat(getReissueBefore(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.reissuable())),
                () -> assertThat(getNameBefore(txIndex, 0)).isEqualTo(issueInfo.name()),
                () -> assertThat(getDescriptionBefore(txIndex, 0)).isEqualTo(issueInfo.description()),
                () -> assertThat(getDecimalsBefore(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.decimals())),
                () -> assertThat(getScriptBefore(txIndex, 0)).isEqualTo(issueInfo.script().bytes()),

                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(assetIdString),
                () -> assertThat(getIssuerAfter(txIndex, 0)).isEqualTo(senderPublicKeyString),
                () -> assertThat(getQuantityAfter(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.quantity())),
                () -> assertThat(getReissueAfter(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.reissuable())),
                () -> assertThat(getNameAfter(txIndex, 0)).isEqualTo(newAssetName),
                () -> assertThat(getDescriptionAfter(txIndex, 0)).isEqualTo(newAssetDescription),
                () -> assertThat(getDecimalsAfter(txIndex, 0)).isEqualTo(String.valueOf(issueInfo.decimals())),
                () -> assertThat(getScriptAfter(txIndex, 0)).isEqualTo(issueInfo.script().bytes())
        );
    }
}
