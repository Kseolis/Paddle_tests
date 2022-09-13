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
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers.InvokeTransactionAssertions.checkPaymentsSubscribe;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetVolume;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptPaymentsGrpcTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("subscribe invoke with payments")
    void subscribeInvokeWithScriptPayments() {
        testData.prepareDataForPaymentsTests();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        final AssetId assetId = testData.getAssetId();
        final DAppCall dAppCall = testData.getDAppCall();
        final Account caller = testData.getCallerAccount();
        final Account dAppAccount = testData.getDAppAccount();
        final List<Amount> amounts = testData.getAmounts();
        final long wavesAmountValue = testData.getWavesAmount().value();
        final long assetAmountValue = testData.getAssetAmount().value();
        final String intArgToStr = String.valueOf(testData.getIntArg());

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender
                (caller, dAppAccount, dAppCall, amounts);

        setVersion(LATEST_VERSION);
        calcBalances.balancesAfterCallerInvokeAsset(caller, dAppAccount, amounts, assetId);
        txSender.invokeSenderWithPayment();

        String txId = txSender.getInvokeScriptId();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, dAppAccount, height, height, txId);
        prepareInvoke(dAppAccount, testData);

        assertionsCheck(
                wavesAmountValue,
                assetAmountValue,
                assetId.toString(),
                intArgToStr,
                txId
        );
    }

    private void assertionsCheck(long paymentWaves, long paymentAsset, String assetId, String intArg, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(testData.getInvokeFee(), testData.getCallerPublicKey(), txId),
                () -> checkPaymentsSubscribe(0, 0, paymentWaves, ""),
                () -> checkPaymentsSubscribe(0, 1, paymentAsset, assetId),

                () -> checkMainMetadata(0),
                () -> checkArgumentsMetadata(0, 0, INTEGER, intArg),
                () -> checkPaymentMetadata(0, 0, null, paymentWaves),
                () -> checkPaymentMetadata(0, 1, assetId, paymentAsset),
                () -> checkDataMetadata(0, 0, INTEGER, DATA_ENTRY_INT, intArg),
                () -> checkIssueAssetMetadata(0, 0, getIssueAssetData()),

                () -> checkStateUpdateBalance(0,
                        0,
                        testData.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        1,
                        testData.getCallerAddress(),
                        assetId,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getCallerBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        2,
                        testData.getDAppAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        3,
                        testData.getDAppAddress(),
                        assetId,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateBalance(0,
                        4,
                        testData.getDAppAddress(),
                        null,
                        0, getIssueAssetVolume()),
                () -> checkStateUpdateDataEntries(0, 0, getDAppAccountAddress(), DATA_ENTRY_INT, intArg),
                () -> checkStateUpdateAssets(0, 0, getIssueAssetData(), getIssueAssetVolume())
        );
    }
}
