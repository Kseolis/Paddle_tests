package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeSponsorFeeGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private String assetIdToStr;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() {
        testData.prepareDataForSponsorFeeTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        assetIdToStr = assetId.toString();

        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account assetDAppAccount = testData.getAssetDAppAccount();
        final List<Amount> amounts = testData.getAmounts();
        final long assetAmountValue = testData.getAssetAmount().value();

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);

        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterPaymentInvoke(caller, assetDAppAccount, amounts, assetId);
        txSender.invokeSender();

        String txId = txSender.getInvokeScriptId();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);

        assertionsCheck(assetAmountValue, txId);
    }

    private void assertionsCheck(long sponsorship, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, BINARY_BASE58, assetIdToStr),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),
                () -> checkSponsorFeeMetadata(0, 0, assetIdToStr, testData.getAssetAmount().value()),
                () -> checkSponsorFeeMetadata(0, 1, null, testData.getAssetAmount().value()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),

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
