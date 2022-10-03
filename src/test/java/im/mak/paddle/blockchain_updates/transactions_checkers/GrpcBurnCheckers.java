package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.IssueTransaction;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.BurnTransactionSender;

import static com.wavesplatform.transactions.BurnTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;
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
    private final IssueTransaction issueTx;

    private final String senderPublicKey;
    private final String senderAddress;

    private final String burnTxId;
    private final long burnAssetFee;
    private final String burnedAssetId;
    private final long burnAssetAmount;
    private final long wavesBeforeBurn;
    private final long wavesAfterBurn;

    public GrpcBurnCheckers(int txIndex, Account sender, BurnTransactionSender txSender, IssueTransaction issueTx) {
        this.txIndex = txIndex;
        this.issueTx = issueTx;

        burnTxId = txSender.getBurnTx().id().toString();
        burnAssetFee = txSender.getFee();
        burnedAssetId = txSender.getAmount().assetId().toString();
        burnAssetAmount = txSender.getAmount().value();

        wavesBeforeBurn = txSender.getAccountWavesBalance();
        wavesAfterBurn = wavesBeforeBurn - burnAssetFee;

        senderPublicKey = sender.publicKey().toString();
        senderAddress = sender.address().toString();
    }

    public void checkBurnSubscribe(long assetAmountBefore, long assetAmountAfter, long quantityAfter) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(burnAssetFee),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getBurnAssetId(txIndex)).isEqualTo(burnedAssetId),
                () -> assertThat(getBurnAssetAmount(txIndex)).isEqualTo(burnAssetAmount),
                () -> assertThat(getTxId(txIndex)).isEqualTo(burnTxId),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(wavesBeforeBurn),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(wavesAfterBurn),
                // check asset balance
                () -> assertThat(getAddress(txIndex, 1)).isEqualTo(senderAddress),
                () -> assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(burnedAssetId),
                () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(assetAmountBefore),
                () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(assetAmountAfter),
                // check asset before burn
                () -> assertThat(getAssetIdFromAssetBefore(txIndex, 0)).isEqualTo(burnedAssetId),
                () -> assertThat(getIssuerBefore(txIndex, 0)).isEqualTo(senderPublicKey),
                () -> assertThat(getQuantityBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.quantity())),
                () -> assertThat(getReissueBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.reissuable())),
                () -> assertThat(getNameBefore(txIndex, 0)).isEqualTo(issueTx.name()),
                () -> assertThat(getDescriptionBefore(txIndex, 0)).isEqualTo(issueTx.description()),
                () -> assertThat(getDecimalsBefore(txIndex, 0)).isEqualTo(String.valueOf(issueTx.decimals())),
                () -> assertThat(getScriptBefore(txIndex, 0)).isEqualTo(issueTx.script().bytes()),
                // check asset after burn
                () -> assertThat(getAssetIdFromAssetAfter(txIndex, 0)).isEqualTo(burnedAssetId),
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
