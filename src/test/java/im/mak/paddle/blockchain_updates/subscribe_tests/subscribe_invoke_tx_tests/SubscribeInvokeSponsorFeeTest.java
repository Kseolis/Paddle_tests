package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSender;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeSponsorFeeTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() {
        getTestsData().prepareDataForSponsorFeeTests();

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

        height = node().getHeight();
        subscribeResponseHandler(channel, getAssetDAppAccount(), height, height);
        prepareInvoke(getAssetDAppAccount());

        assertionsCheck(getAssetAmount().value());
    }

    private void assertionsCheck(long sponsorship) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(getFee(), getCallerPublicKey()),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, getAssetId().toString()),
                () -> checkIssueAssetMetadata(0, 0),
                () -> checkSponsorFeeMetadata(0, 0, getAssetId().toString(), getAssetAmount().value()),
                () -> checkSponsorFeeMetadata(0, 1, null, getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        getCallerAddress(),
                        WAVES_STRING_ID,
                        getCallerBalanceWavesBeforeTransaction(), getCallerBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(0,
                        1,
                        getAssetDAppAddress(),
                        null,
                        0, Long.parseLong(getIssueAssetData().get(VOLUME))),

                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume()),
                () -> checkStateUpdateAssets(0, 1,
                        getAssetData(),
                        Long.parseLong(getAssetData().get(VOLUME))),

                () -> checkStateUpdateAssetsSponsorship(0, 0, sponsorship),
                () -> checkStateUpdateAssetsSponsorship(0, 1, sponsorship)
        );
    }
}
