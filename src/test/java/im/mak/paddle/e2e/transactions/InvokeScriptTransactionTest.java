package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeBaseTest;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setFee;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeScriptTransactionTest extends InvokeBaseTest {
    @Test
    @DisplayName("invoke with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        getTestsData().prepareDataForDataDAppTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(getCallerAccount(), SUM_FEE, txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with DeleteEntry")
    void invokeScriptDeleteEntryTest() {
        getTestsData().prepareDataForDeleteEntryTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(getCallerAccount(), SUM_FEE, txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Burn transaction")
    void invokeScriptWithBurn() {
        getTestsData().prepareDataForBurnTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

            setVersion(v);
            balancesAfterBurnAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSender();
            checkInvokeTransaction(getCallerAccount(), getFee(), txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Reissue Transaction")
    void invokeScriptWithReissue() {
        getTestsData().prepareDataForReissueTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

            setVersion(v);
            balancesAfterReissueAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSender();
            checkInvokeTransaction(getCallerAccount(), getFee(), txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with Lease and WAVES payment")
    void invokeScriptWithLease() {
        long fee = ONE_WAVES + SUM_FEE;
        getTestsData().prepareDataForLeaseTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(getCallerAccount(), fee, txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel and WAVES payment")
    void invokeScriptWithLeaseCancel() {

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

            getTestsData().prepareDataForLeaseCancelTests();
            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(getCallerAccount(), SUM_FEE, txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with SponsorFee")
    void invokeScriptWithSponsorFee() {
        getTestsData().prepareDataForSponsorFeeTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());

            setVersion(v);
            balancesAfterPaymentInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSender();
            checkInvokeTransaction(getCallerAccount(), getFee(), txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getAssetDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        getTestsData().prepareDataForScriptTransferTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getAssetDAppAccount(), getAssetDAppAccount(), getDAppCall());

            setVersion(v);
            balancesAfterCallerInvokeAsset(getAssetDAppAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSender();
            checkInvokeTransaction(getAssetDAppAccount(), getFee(), txSender);
            checkBalancesAfterInvoke(getAssetDAppAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke with payments")
    void invokeScriptPayments() {
        getTestsData().prepareDataForPaymentsTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

            setVersion(v);
            balancesAfterCallerInvokeAsset(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSenderWithPayment();
            checkInvokeTransaction(getCallerAccount(), getFee(), txSender);
            checkBalancesAfterInvoke(getCallerAccount(), getDAppAccount());
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        getTestsData().prepareDataForDAppToDAppTests();

        for (int v = 1; v <= LATEST_VERSION; v++) {
            InvokeScriptTransactionSender txSender =
                    new InvokeScriptTransactionSender(getCallerAccount(), getDAppAccount(), getDAppCall());

            setVersion(v);
            balancesAfterDAppToDApp(getCallerAccount(), getDAppAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
            txSender.invokeSender();
            checkInvokeTransaction(getCallerAccount(), getFee(), txSender);
            checkBalancesAfterDAppToDAppInvoke(getCallerAccount(), getDAppAccount(), getAssetDAppAccount());
        }
    }

    private void checkInvokeTransaction(Account caller, long fee, InvokeScriptTransactionSender tx) {
        assertThat(tx.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED);
        assertThat(tx.getInvokeScriptTx().dApp()).isEqualTo(getDAppCall().getDApp());
        assertThat(tx.getInvokeScriptTx().function()).isEqualTo(getDAppCall().getFunction());
        assertThat(tx.getInvokeScriptTx().sender()).isEqualTo(caller.publicKey());
        assertThat(tx.getInvokeScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES);
        assertThat(tx.getInvokeScriptTx().fee().value()).isEqualTo(fee);
        assertThat(tx.getInvokeScriptTx().type()).isEqualTo(16);
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