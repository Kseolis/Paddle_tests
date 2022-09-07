package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkIssueAssetMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptTransferTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with ScriptTransfer")
    void subscribeInvokeWithScriptTransfer() {
        long assetAmountValue = getAssetAmount().value();
        getTestsData().prepareDataForScriptTransferTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterCallerScriptTransfer(getCallerAccount(),
                getAssetDAppAccount(), getDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSender();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, getCallerAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getAssetDAppAccount());

        long dAppAssetAmountAfter = Long.parseLong(getIssueAssetData().get(VOLUME)) - assetAmountValue;
        assertionsCheck(getAssetId().toString(), dAppAssetAmountAfter, assetAmountValue);
    }

    private void assertionsCheck(String assetId, long dAppAssetAmountAfter, long recipientAmountValueAfter) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getFee(), getCallerPublicKey()),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, assetId),
                () -> checkArgumentsMetadata(0, 1, BINARY_BASE58, getDAppAddress()),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),

                () -> checkTransfersMetadata(0, 0,
                        getDAppAddressBase58(),
                        assetId,
                        getAssetAmount().value()),
                () -> checkTransfersMetadata(0, 1,
                        getDAppAddressBase58(),
                        null,
                        getAssetAmount().value()),
                () -> checkTransfersMetadata(0, 2,
                        getDAppAddressBase58(),
                        WAVES_STRING_ID,
                        getWavesAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getAssetDAppAddress(),
                        WAVES_STRING_ID,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        2,
                        getAssetDAppAddress(),
                        null,
                        0, dAppAssetAmountAfter),
                () -> checkStateUpdateBalance(0,
                        3,
                        getAssetDAppAddress(),
                        assetId,
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        4,
                        getDAppAddress(),
                        WAVES_STRING_ID,
                        getAccBalanceWavesBeforeTransaction(), getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        5,
                        getDAppAddress(),
                        assetId,
                        getAccBalanceIssuedAssetsBeforeTransaction(), getAccBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        6,
                        getDAppAddress(),
                        null,
                        0, recipientAmountValueAfter),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume())
        );
    }
}
