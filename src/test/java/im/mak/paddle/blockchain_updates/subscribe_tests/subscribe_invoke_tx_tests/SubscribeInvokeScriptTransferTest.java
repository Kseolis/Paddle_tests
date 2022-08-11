package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.checkIssueAssetMetadata;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSender;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptTransferTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with ScriptTransfer")
    void subscribeInvokeWithScriptTransfer() {
        getTestsData().prepareDataForScriptTransferTests();

        setVersion(LATEST_VERSION);
        balancesAfterCallerInvokeAsset(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        height = node().getHeight();
        subscribeResponseHandler(channel, getCallerAccount(), height, height);
        prepareInvoke(getAssetDAppAccount());

        System.out.println(getAppend());

        assertionsCheck(getAssetId().toString());
    }

    private void assertionsCheck(String assetId) {
        System.out.println("callerAddress: " + getCallerAddress());
        System.out.println("dAppAddress: " + getDAppAddress());

        assertAll(
                () -> checkTransfersMetadata(0, 0, getAssetDAppAddress(), assetId, getAssetAmount().value())
        );
        assertAll(
                () -> checkInvokeSubscribeTransaction(getFee(), getCallerPublicKey()),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, assetId),
                () -> checkArgumentsMetadata(0, 1, BINARY_BASE58, getDAppAddress()),
                () -> checkIssueAssetMetadata(0, 0)
/*
                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        2,
                        getDAppAddress(),
                        WAVES_STRING_ID,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        4,
                        getDAppAddress(),
                        null,
                        0, getIssueAssetVolume()),
                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume())*/
        );
    }
}
