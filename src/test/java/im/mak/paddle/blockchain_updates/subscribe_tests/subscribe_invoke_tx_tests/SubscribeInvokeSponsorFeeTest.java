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
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() {
        testsData.prepareDataForSponsorFeeTests();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (testsData.getCallerAccount(), testsData.getAssetDAppAccount(), testsData.getDAppCall());

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(testsData.getCallerAccount(), testsData.getAssetDAppAccount(), testsData.getAmounts(), testsData.getAssetId());
        txSender.invokeSender();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, testsData.getAssetDAppAccount(), height, height, getInvokeScriptId());
        prepareInvoke(testsData.getAssetDAppAccount(), testsData);

        assertionsCheck(testsData.getAssetAmount().value());
    }

    private void assertionsCheck(long sponsorship) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testsData.getInvokeFee(), testsData.getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, testsData.getAssetId().toString()),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkSponsorFeeMetadata(0, 0, testsData.getAssetId().toString(), testsData.getAssetAmount().value()),
                () -> checkSponsorFeeMetadata(0, 1, null, testsData.getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testsData.getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        testsData.getAssetDAppAddress(),
                        null,
                        0, Long.parseLong(getIssueAssetData().get(VOLUME))),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume()),
                () -> checkStateUpdateAssets(0, 1,
                        testsData.getAssetData(),
                        Long.parseLong(testsData.getAssetData().get(VOLUME))),

                () -> checkStateUpdateAssetsSponsorship(0, 0, sponsorship),
                () -> checkStateUpdateAssetsSponsorship(0, 1, sponsorship)
        );
    }
}
*/
