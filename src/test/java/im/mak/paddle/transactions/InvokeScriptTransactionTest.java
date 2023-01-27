package im.mak.paddle.transactions;

import com.wavesplatform.transactions.InvokeScriptTransaction;
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
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeScriptTransactionTest {
    private PrepareInvokeTestsData testData;
    private Account caller;
    private Account dAppAccount;
    private Account otherDAppAccount;
    private Account assetDAppAccount;
    private List<Amount> payments;
    private List<Amount> otherAmounts;
    private AssetId assetId;
    private DAppCall dAppCall;
    private InvokeCalculationsBalancesAfterTx calcBalances;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        async(
                () -> caller = testData.getCallerAccount(),
                () -> dAppAccount = testData.getDAppAccount(),
                () -> otherDAppAccount = testData.getOtherDAppAccount(),
                () -> assetDAppAccount = testData.getAssetDAppAccount(),
                () -> payments = testData.getPayments(),
                () -> otherAmounts = testData.getOtherAmounts(),
                () -> assetId = testData.getAssetId()
        );
    }

    @Test
    @DisplayName("invoke with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForDataDAppTests(SUM_FEE, 0);
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment(v);
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with DeleteEntry")
    void invokeScriptDeleteEntryTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForDeleteEntryTests();
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment(v);
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with Burn transaction")
    void invokeScriptWithBurn() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForBurnTests();
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
            calcBalances.balancesAfterBurnAssetInvoke(caller.address(), assetDAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, testData.getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Reissue Transaction")
    void invokeScriptWithReissue() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForReissueTests();
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, testData.getAssetDAppAccount(), dAppCall);
            calcBalances.balancesAfterReissueAssetInvoke(caller.address(), assetDAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with Lease and WAVES payment")
    void invokeScriptWithLease() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForLeaseTests(SUM_FEE, ONE_WAVES);
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel and WAVES payment")
    void invokeScriptWithLeaseCancel() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForLeaseCancelTests(SUM_FEE, ONE_WAVES);
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, payments);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), payments, assetId);
            txSender.invokeSenderWithPayment(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with SponsorFee")
    void invokeScriptWithSponsorFee() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForSponsorFeeTests();
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
            calcBalances.balancesAfterPaymentInvoke(caller.address(), assetDAppAccount.address(), payments, assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForScriptTransferTests();
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
            calcBalances.balancesAfterCallerScriptTransfer(caller.address(), assetDAppAccount.address(), dAppAccount.address(), otherAmounts, assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, testData.getInvokeFee(), txSender, payments);
            checkBalancesAfterInvoke(caller, assetDAppAccount);
            thirdAccountBalanceCheck(dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForDAppToDAppTests(SUM_FEE);
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
            calcBalances.balancesAfterDAppToDApp(caller.address(),
                    dAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
            thirdAccountBalanceCheck(assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke double nested for i.caller")
    void invokeDoubleNestedForCaller() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            caller = new Account(FIVE_WAVES);
            testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.caller", "i.caller");
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);

            calcBalances.balancesAfterDoubleNestedForCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId);
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
            thirdAccountBalanceCheck(assetDAppAccount);
            fourthAccountBalanceCheck(otherDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke double nested for i.originCaller")
    void invokeDoubleNestedForOriginCaller() {
        caller = new Account(FIVE_WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.originCaller", "i.originCaller");
            dAppCall = testData.getDAppCall();
            calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
            InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
            calcBalances.balancesAfterDoubleNestedForOriginCaller(
                    caller.address(),
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    otherAmounts,
                    assetId
            );
            txSender.invokeSender(v);
            checkInvokeTransaction(caller, SUM_FEE, txSender, payments);
            checkBalancesAfterInvoke(caller, dAppAccount);
            thirdAccountBalanceCheck(assetDAppAccount);
            fourthAccountBalanceCheck(otherDAppAccount);
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
                () -> assertThat(tx.getInvokeScriptTx().type()).isEqualTo(InvokeScriptTransaction.TYPE)
        );
    }

    private void checkBalancesAfterInvoke(Account caller, Account dApp) {
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

    private void thirdAccountBalanceCheck(Account acc) {
        assertThat(acc.getWavesBalance()).isEqualTo(calcBalances.getAccBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(acc.getBalance(assetId)).isEqualTo(calcBalances.getAccBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void fourthAccountBalanceCheck(Account dApp2) {
        assertThat(dApp2.getWavesBalance()).isEqualTo(calcBalances.getOtherDAppBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(dApp2.getBalance(assetId)).isEqualTo(calcBalances.getOtherDAppBalanceIssuedAssetsAfterTransaction());
        }
    }
}