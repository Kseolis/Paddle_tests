package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.getTxInfo;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeScriptTransactionTest {
    private static PrepareInvokeTestsData testsData;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    @Test
    @DisplayName("invoke with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        testsData.prepareDataForDataDAppTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());
            checkInvokeTransaction(getCallerAccount(), SUM_FEE);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with DeleteEntry")
    void invokeScriptDeleteEntryTest() {
        testsData.prepareDataForDeleteEntryTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());
            checkInvokeTransaction(getCallerAccount(), SUM_FEE);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Burn transaction")
    void invokeScriptWithBurn() {
        testsData.prepareDataForBurnTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterBurnAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());
            checkInvokeTransaction(getCallerAccount(), getFee());
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Reissue Transaction")
    void invokeScriptWithReissue() {
        testsData.prepareDataForReissueTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterReissueAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());
            checkInvokeTransaction(getCallerAccount(), getFee());
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Lease and WAVES payment")
    void invokeScriptWithLease() {
        testsData.prepareDataForLeaseTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());
            checkInvokeTransaction(getCallerAccount(), SUM_FEE);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel and WAVES payment")
    void invokeScriptWithLeaseCancel() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            testsData.prepareDataForLeaseCancelTests();
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());
            checkInvokeTransaction(getCallerAccount(), SUM_FEE);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with SponsorFee")
    void invokeScriptWithSponsorFee() {
        testsData.prepareDataForSponsorFeeTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());
            checkInvokeTransaction(getCallerAccount(), getFee());
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        testsData.prepareDataForScriptTransferTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterCallerInvokeAsset(getAssetDAppAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSender(getAssetDAppAccount(), getAssetDAppAccount(), getDAppCall());
            checkInvokeTransaction(getAssetDAppAccount(), getFee());
            checkBalancesAfterInvoke(getAssetDAppAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with payments")
    void invokeScriptPayments() {
        testsData.prepareDataForPaymentsTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterCallerInvokeAsset(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());
            checkInvokeTransaction(getCallerAccount(), getFee());
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        testsData.prepareDataForDAppToDAppTests();
        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterDAppToDApp(getCallerAccount(), getDAppAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            invokeSender(getCallerAccount(), getDAppAccount(), getDAppCall());
            checkInvokeTransaction(getCallerAccount(), SUM_FEE);
            checkBalancesAfterDAppToDAppInvoke(getCallerAccount(), getDAppAccount(), getAssetDAppAccount());
        }
    }

    private void checkInvokeTransaction(Account caller, long fee) {
        assertThat(getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED);
        assertThat(getInvokeScriptTx().dApp()).isEqualTo(getDAppCall().getDApp());
        assertThat(getInvokeScriptTx().function()).isEqualTo(getDAppCall().getFunction());
        assertThat(getInvokeScriptTx().sender()).isEqualTo(caller.publicKey());
        assertThat(getInvokeScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES);
        assertThat(getInvokeScriptTx().fee().value()).isEqualTo(fee);
        assertThat(getInvokeScriptTx().type()).isEqualTo(16);
    }

    private void checkBalancesAfterInvoke(Account caller, Account dApp) {
        assertThat(caller.getWavesBalance()).isEqualTo(getCallerBalanceWavesAfterTransaction());
        assertThat(dApp.getWavesBalance()).isEqualTo(getDAppBalanceWavesAfterTransaction());
        if (getAssetId() != null) {
            assertThat(caller.getBalance(getAssetId())).isEqualTo(getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp.getBalance(getAssetId())).isEqualTo(getDAppBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void checkBalancesAfterDAppToDAppInvoke(Account caller, Account dApp, Account acc) {
        assertThat(caller.getWavesBalance()).isEqualTo(getCallerBalanceWavesAfterTransaction());
        assertThat(dApp.getWavesBalance()).isEqualTo(getDAppBalanceWavesAfterTransaction());
        assertThat(acc.getWavesBalance()).isEqualTo(getAccBalanceWavesAfterTransaction());
        if (getAssetId() != null) {
            assertThat(caller.getBalance(getAssetId())).isEqualTo(getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp.getBalance(getAssetId())).isEqualTo(getDAppBalanceIssuedAssetsAfterTransaction());
            assertThat(acc.getBalance(getAssetId())).isEqualTo(getAccBalanceIssuedAssetsAfterTransaction());
        }
    }
}