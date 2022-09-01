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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeSponsorFeeTest extends InvokeBaseTest {
    private static PrepareInvokeTestsData testData;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() {
        testData.prepareDataForSponsorFeeTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testData.getCallerAccount(), testData.getAssetDAppAccount(), testData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(testData.getCallerAccount(), testData.getAssetDAppAccount(), testData.getAmounts(), testData.getAssetId());
        txSender.invokeSender();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testData.getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testData.getAssetDAppAccount(), testData);

        assertionsCheck(testData.getAssetAmount().value());
    }

    private void assertionsCheck(long sponsorship) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, testData.getAssetId().toString()),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkSponsorFeeMetadata(0, 0, testData.getAssetId().toString(), testData.getAssetAmount().value()),
                () -> checkSponsorFeeMetadata(0, 1, null, testData.getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getAssetDAppAddress(),
                        null,
                        0, Long.parseLong(getIssueAssetData().get(VOLUME))),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume()),
                () -> checkStateUpdateAssets(0, 1,
                        testData.getAssetData(),
                        Long.parseLong(testData.getAssetData().get(VOLUME))),

                () -> checkStateUpdateAssetsSponsorship(0, 0, sponsorship),
                () -> checkStateUpdateAssetsSponsorship(0, 1, sponsorship)
        );
    }
}
*/
