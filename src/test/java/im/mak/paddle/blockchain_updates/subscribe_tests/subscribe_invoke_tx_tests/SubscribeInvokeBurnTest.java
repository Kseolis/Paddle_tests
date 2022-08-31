package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeBurnTest extends InvokeBaseTest {
    private PrepareInvokeTestsData testsData;

    @BeforeEach
    void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with Burn")
    void subscribeInvokeWithBurn() {
        testsData.prepareDataForBurnTests();
        InvokeScriptTransactionSender txSender =
                new InvokeScriptTransactionSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterBurnAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        txSender.invokeSender();

        height = node().getHeight();

        subscribeResponseHandler(CHANNEL, getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(getAssetDAppAccount());

        assertionsCheck();
    }

    private void assertionsCheck() {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getInvokeFee(), getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkBurnMetadata(0, 0, getAssetId().toString(), getAssetAmount().value()),
                () -> checkBurnMetadata(0, 1, null, getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getAssetDAppAddress(),
                        null,
                        0, getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(0,
                        2,
                        getAssetDAppAddress(),
                        getAssetId().toString(),
                        getDAppBalanceIssuedAssetsBeforeTransaction(), getDAppBalanceIssuedAssetsAfterTransaction())
        );

        checkStateUpdateAssets(0, 0, getIssueAssetData(), getAmountAfterInvokeIssuedAsset());
        checkStateUpdateAssets(0, 1, getAssetData(), getAmountAfterInvokeDAppIssuedAsset());
    }
}
