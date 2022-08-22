package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeDAppToDAppTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe dApp to dApp")
    void subscribeInvokeWithDAppToDApp() {
        getTestsData().prepareDataForDAppToDAppTests();

        InvokeScriptTransactionSender txSender =
                new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterDAppToDApp(getCallerAccount(), getDAppAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSender();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, getDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getDAppAccount());

        assertionsCheck(
                getKey1ForDAppEqualBar(),
                getKey2ForDAppEqualBalance(),
                getAssetId().toString()
        );
    }

    private void assertionsCheck(String key1, String key2, String assetId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(SUM_FEE, getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, getAssetDAppAddress()),
                () -> checkArgumentsMetadata(0, 1, INTEGER, String.valueOf(getIntArg())),
                () -> checkArgumentsMetadata(0, 2, STRING, key1),
                () -> checkArgumentsMetadata(0, 3, STRING, key2),
                () -> checkArgumentsMetadata(0, 4, BINARY_BASE58, assetId),

                () -> checkDataMetadata(0, 0,
                        INTEGER,
                        key1,
                        getInvokeResultData()),
                () -> checkDataMetadata(0, 1,
                        INTEGER,
                        key2,
                        String.valueOf(getAccBalanceWavesAfterTransaction())),

                () -> checkResultInvokesMetadata(0, 0,
                        getAssetDAppAddress(),
                        key1),
                () -> checkResultInvokesMetadataPayments(0, 0, 0, assetId, getAssetAmount().value()),
                () -> checkResultInvokesMetadataStateChanges(0, 0, 0,
                        WAVES_STRING_ID,
                        getDAppAddressBase58(),
                        getWavesAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getDAppAddress(),
                        WAVES_STRING_ID,
                        getDAppBalanceWavesBeforeTransaction(), getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        1,
                        getDAppAddress(),
                        assetId,
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        2,
                        getAssetDAppAddress(),
                        WAVES_STRING_ID,
                        getAccBalanceWavesBeforeTransaction(), getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        3,
                        getAssetDAppAddress(),
                        assetId,
                        getAccBalanceIssuedAssetsBeforeTransaction(), getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        4,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(0, 0,
                        getDAppAddress(),
                        key1,
                        getInvokeResultData()),
                () -> checkStateUpdateDataEntries(0, 1,
                        getDAppAddress(),
                        key2,
                        String.valueOf(getAccBalanceWavesAfterTransaction()))
        );
    }
}
