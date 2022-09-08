package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.transactions.common.AssetId.WAVES;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeScriptTransactionSubscribeTest {
    private PrepareInvokeTestsData testData;
    private Account caller;
    private Account dAppAccount;
    private Account assetDAppAccount;
    private List<Amount> amounts;
    private AssetId assetId;
    private DAppCall dAppCall;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        caller = testData.getCallerAccount();
        dAppAccount = testData.getDAppAccount();
        assetDAppAccount = testData.getAssetDAppAccount();
        amounts = testData.getAmounts();
        assetId = testData.getAssetId();
    }

    @Test
    @DisplayName("invoke with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        testData.prepareDataForDataDAppTests();
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller, dAppAccount, amounts, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with DeleteEntry")
    void invokeScriptDeleteEntryTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForDeleteEntryTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller, dAppAccount, amounts, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, SUM_FEE, txSender);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with Burn transaction")
    void invokeScriptWithBurn() {
        testData.prepareDataForBurnTests();
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, testData.getAssetDAppAccount(), dAppCall);

            setVersion(v);
            calcBalances.balancesAfterBurnAssetInvoke(caller, testData.getAssetDAppAccount(), amounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, testData.getAssetDAppAccount(), calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with Reissue Transaction")
    void invokeScriptWithReissue() {
        testData.prepareDataForReissueTests();
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, testData.getAssetDAppAccount(), dAppCall);

            setVersion(v);
            calcBalances.balancesAfterReissueAssetInvoke(caller, testData.getAssetDAppAccount(), amounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, testData.getAssetDAppAccount(), calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with Lease and WAVES payment")
    void invokeScriptWithLease() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForLeaseTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller, dAppAccount, amounts, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel and WAVES payment")
    void invokeScriptWithLeaseCancel() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForLeaseCancelTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller, dAppAccount, amounts, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with SponsorFee")
    void invokeScriptWithSponsorFee() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForSponsorFeeTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, testData.getAssetDAppAccount(), dAppCall);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller, testData.getAssetDAppAccount(), amounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, testData.getAssetDAppAccount(), calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForScriptTransferTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(testData.getAssetDAppAccount(), testData.getAssetDAppAccount(), dAppCall);

            setVersion(v);
            calcBalances.balancesAfterCallerInvokeAsset(testData.getAssetDAppAccount(), dAppAccount, amounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(testData.getAssetDAppAccount(), testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(testData.getAssetDAppAccount(), dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with payments")
    void invokeScriptPayments() {
        testData.prepareDataForPaymentsTests();
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);

            setVersion(v);
            calcBalances.balancesAfterCallerInvokeAsset(caller, dAppAccount, amounts, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        testData.prepareDataForDAppToDAppTests(SUM_FEE + ONE_WAVES);
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterDAppToDApp(caller, dAppAccount, assetDAppAccount, amounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender);
            checkBalancesAfterDAppToDAppInvoke(
                    caller,
                    dAppAccount,
                    testData.getAssetDAppAccount(),
                    calcBalances);
        }
    }

    private void checkInvokeTransaction(Account caller, long fee, InvokeScriptTransactionSender tx) {
        assertThat(tx.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED);
        assertThat(tx.getInvokeScriptTx().dApp()).isEqualTo(dAppCall.getDApp());
        assertThat(tx.getInvokeScriptTx().function()).isEqualTo(dAppCall.getFunction());
        assertThat(tx.getInvokeScriptTx().sender()).isEqualTo(caller.publicKey());
        assertThat(tx.getInvokeScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES);
        assertThat(tx.getInvokeScriptTx().fee().value()).isEqualTo(fee);
        assertThat(tx.getInvokeScriptTx().type()).isEqualTo(16);
    }

    private void checkBalancesAfterInvoke(Account caller, Account dApp, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(caller.getWavesBalance()).isEqualTo(calcBalances.getCallerBalanceWavesAfterTransaction());
        assertThat(dApp.getWavesBalance()).isEqualTo(calcBalances.getDAppBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(caller.getBalance(assetId)).isEqualTo(calcBalances.getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp.getBalance(assetId)).isEqualTo(calcBalances.getDAppBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void checkBalancesAfterDAppToDAppInvoke
            (Account caller, Account dApp, Account acc, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(caller.getWavesBalance()).isEqualTo(calcBalances.getCallerBalanceWavesAfterTransaction());
        assertThat(dApp.getWavesBalance()).isEqualTo(calcBalances.getDAppBalanceWavesAfterTransaction());
        assertThat(acc.getWavesBalance()).isEqualTo(calcBalances.getAccBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(caller.getBalance(assetId)).isEqualTo(calcBalances.getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp.getBalance(assetId)).isEqualTo(calcBalances.getDAppBalanceIssuedAssetsAfterTransaction());
            assertThat(acc.getBalance(assetId)).isEqualTo(calcBalances.getAccBalanceIssuedAssetsAfterTransaction());
        }
    }

    @AfterEach
    void after() {
        if (caller.getWavesBalance() < ONE_WAVES) {
            node().faucet().transfer(caller, DEFAULT_FAUCET, WAVES);
        }
    }
}