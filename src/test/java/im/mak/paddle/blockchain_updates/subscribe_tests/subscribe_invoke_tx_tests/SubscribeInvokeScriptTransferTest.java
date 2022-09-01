/*
package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
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
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptTransferTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with ScriptTransfer")
    void subscribeInvokeWithScriptTransfer() {
        long assetAmountValue = testData.getAssetAmount().value();
        testData.prepareDataForScriptTransferTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testData.getCallerAccount(), testData.getAssetDAppAccount(), testData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterCallerScriptTransfer(testData.getCallerAccount(),
                testData.getAssetDAppAccount(), testData.getDAppAccount(), testData.getAmounts(), testData.getAssetId());
        txSender.invokeSender();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testData.getCallerAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testData.getAssetDAppAccount(), testData);

        long dAppAssetAmountAfter = Long.parseLong(getIssueAssetData().get(VOLUME)) - assetAmountValue;
        assertionsCheck(testData.getAssetId().toString(), dAppAssetAmountAfter, assetAmountValue);
    }

    private void assertionsCheck(String assetId, long dAppAssetAmountAfter, long recipientAmountValueAfter) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey()),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, assetId),
                () -> checkArgumentsMetadata(0, 1, BINARY_BASE58, testData.getDAppAddress()),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),

                () -> checkTransfersMetadata(0, 0,
                        testData.getDAppAddressBase58(),
                        assetId,
                        testData.getAssetAmount().value()),
                () -> checkTransfersMetadata(0, 1,
                        testData.getDAppAddressBase58(),
                        null,
                        testData.getAssetAmount().value()),
                () -> checkTransfersMetadata(0, 2,
                        testData.getDAppAddressBase58(),
                        WAVES_STRING_ID,
                        testData.getWavesAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getAssetDAppAddress(),
                        WAVES_STRING_ID,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getAssetDAppAddress(),
                        null,
                        0, dAppAssetAmountAfter),
                () -> checkStateUpdateBalance(0,
                        3,
                        testData.getAssetDAppAddress(),
                        assetId,
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        4,
                        testData.getDAppAddress(),
                        WAVES_STRING_ID,
                        getAccBalanceWavesBeforeTransaction(), getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        5,
                        testData.getDAppAddress(),
                        assetId,
                        getAccBalanceIssuedAssetsBeforeTransaction(), getAccBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        6,
                        testData.getDAppAddress(),
                        null,
                        0, recipientAmountValueAfter),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume())
        );
    }
}
*/
