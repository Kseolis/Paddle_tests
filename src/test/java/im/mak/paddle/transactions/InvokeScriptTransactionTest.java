package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeScriptTransactionTest {
    private static PrepareInvokeTestsData testData;
    private static Account caller;
    private static Account dAppAccount;
    private static Account otherDAppAccount;
    private static Account assetDAppAccount;
    private static List<Amount> payments;
    private static List<Amount> otherAmounts;
    private static AssetId assetId;
    private static DAppCall dAppCall;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
        caller = testData.getCallerAccount();
        dAppAccount = testData.getDAppAccount();
        otherDAppAccount = testData.getOtherDAppAccount();
        assetDAppAccount = testData.getAssetDAppAccount();
        payments = testData.getPayments();
        otherAmounts = testData.getOtherAmounts();
        assetId = testData.getAssetId();
    }

    @Test
    @DisplayName("invoke with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        testData.prepareDataForDataDAppTests(SUM_FEE, 0);
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
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
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
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
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterBurnAssetInvoke(caller.address(), assetDAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
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
            calcBalances.balancesAfterReissueAssetInvoke(caller.address(), assetDAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with Lease and WAVES payment")
    void invokeScriptWithLease() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForLeaseTests(SUM_FEE, ONE_WAVES);
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel and WAVES payment")
    void invokeScriptWithLeaseCancel() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForLeaseCancelTests(SUM_FEE, ONE_WAVES);
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
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
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), assetDAppAccount.address(), payments, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            testData.prepareDataForScriptTransferTests();
            dAppCall = testData.getDAppCall();
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterCallerScriptTransfer(caller.address(), assetDAppAccount.address(), dAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount, calcBalances);
            thirdAccountBalanceCheck(dAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        testData.prepareDataForDAppToDAppTests(SUM_FEE);
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterDAppToDApp(caller.address(),
                    dAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
            thirdAccountBalanceCheck(assetDAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke double nested for i.caller")
    void invokeDoubleNestedForCaller() {
        testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.caller", "i.caller");
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterDoubleNestedForCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId);
            txSender.invokeSender();
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
            thirdAccountBalanceCheck(assetDAppAccount, calcBalances);
            fourthAccountBalanceCheck(otherDAppAccount, calcBalances);
        }
    }

    @Test
    @DisplayName("invoke double nested for i.originCaller")
    void invokeDoubleNestedForOriginCaller() {
        testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.originCaller", "i.originCaller");
        dAppCall = testData.getDAppCall();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            setVersion(v);
            calcBalances.balancesAfterDoubleNestedForOriginCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId
            );
            txSender.invokeSender();
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount, calcBalances);
            thirdAccountBalanceCheck(assetDAppAccount, calcBalances);
            fourthAccountBalanceCheck(otherDAppAccount, calcBalances);
        }
    }

    private void checkInvokeTransaction(Account caller, long fee, InvokeScriptTransactionSender tx, List<Amount> payments) {
        assertAll(
                () -> assertThat(tx.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(tx.getInvokeScriptTx().dApp()).isEqualTo(dAppCall.getDApp()),
                () -> assertThat(tx.getInvokeScriptTx().function()).isEqualTo(dAppCall.getFunction()),
                () -> assertThat(tx.getInvokeScriptTx().sender()).isEqualTo(caller.publicKey()),
                () -> assertThat(tx.getInvokeScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(tx.getInvokeScriptTx().fee().value()).isEqualTo(fee),
                () -> assertThat(tx.getInvokeScriptTx().payments()).isEqualTo(payments),
                () -> assertThat(tx.getInvokeScriptTx().type()).isEqualTo(16)
        );
    }

    private void checkBalancesAfterInvoke(Account caller, Account dApp, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertAll(
                () -> assertThat(caller.getWavesBalance()).isEqualTo(calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> assertThat(dApp.getWavesBalance()).isEqualTo(calcBalances.getDAppBalanceWavesAfterTransaction())
        );
        if (assetId != null) {
            assertAll(
                    () -> assertThat(caller.getBalance(assetId)).isEqualTo(calcBalances.getCallerBalanceIssuedAssetsAfterTransaction()),
                    () -> assertThat(dApp.getBalance(assetId)).isEqualTo(calcBalances.getDAppBalanceIssuedAssetsAfterTransaction())
            );
        }
    }

    private void thirdAccountBalanceCheck(Account acc, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(acc.getWavesBalance()).isEqualTo(calcBalances.getAccBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(acc.getBalance(assetId)).isEqualTo(calcBalances.getAccBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void fourthAccountBalanceCheck(Account dApp2, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(dApp2.getWavesBalance()).isEqualTo(calcBalances.getOtherDAppBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(dApp2.getBalance(assetId)).isEqualTo(calcBalances.getOtherDAppBalanceIssuedAssetsAfterTransaction());
        }
    }
}