package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSender;
import static im.mak.paddle.util.Constants.WAVES_STRING_ID;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeBurnTest extends InvokeBaseTest {

    @Test
    @DisplayName("subscribe invoke with Burn")
    void subscribeInvokeWithBurn() {
        testsData.prepareDataForBurnTests();

        setVersion(LATEST_VERSION);
        balancesAfterBurnAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        height = node().getHeight();

        subscribeResponseHandler(channel, getAssetDAppAccount(), height, height);
        prepareInvoke(getAssetDAppAccount());

        System.out.println(getAppend());

        assertionsCheck();
    }

    private void assertionsCheck() {
        assertAll(
                () -> checkInvokeSubscribe(getAssetAmount().value(), getFee()),
                () -> checkMainMetadata(0),
                () -> checkIssueAssetMetadata(0, 0),
                () -> checkBurnMetadata(0, 0, getAssetId().toString(), getAssetAmount().value()),
                () -> checkBurnMetadata(0, 1, WAVES_STRING_ID, getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(),
                        getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(1,
                        getAssetDAppAddress(),
                        null,
                        0,
                        getAmountAfterInvokeIssuedAsset()),

                () -> checkStateUpdateBalance(2,
                        getAssetDAppAddress(),
                        getAssetId().toString(),
                        getDAppBalanceIssuedAssetsBeforeTransaction(),
                        getDAppBalanceIssuedAssetsAfterTransaction())
        );

        checkStateUpdateAssets(0, 0, getIssueAssetData(), getAmountAfterInvokeIssuedAsset());
        checkStateUpdateAssets(0, 1, getAssetData(), getAmountAfterInvokeDAppIssuedAsset());
    }
}
