package im.mak.paddle.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.EthereumTestUser;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.dapps.AssetDAppAccount;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.transactions.common.AssetId.WAVES;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EthereumInvokeTransactionTest {
    private static PrepareInvokeTestsData testData;
    private static EthereumTestUser ethInstance;
    private static Address senderAddress;
    private static Account dAppAccount;
    private static Address dAppAddress;
    private static AssetId assetId;
    private static List<Amount> payments = new ArrayList<>();
    private DAppCall dAppCall;

    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    testData = new PrepareInvokeTestsData();
                    assetId = testData.getAssetId();
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAccount.transfer(senderAddress, testData.getAssetAmount());
                }
        );

    }

    @Test
    @DisplayName("Ethereum invoke with DataDApp and issue asset payment")
    void ethereumInvokeScriptWithDataDAppTest() throws NodeException, IOException {
        testData.prepareDataForDataDAppTests(SUM_FEE, 0);
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, WAVES)
        );
    }

    @Test
    @DisplayName("Ethereum invoke with DeleteEntry")
    void ethereumInvokeScriptDeleteEntryTest() throws NodeException, IOException {
        testData.prepareDataForDeleteEntryTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, WAVES)
        );
    }

    @Test
    @DisplayName("Ethereum invoke with Burn transaction")
    void ethereumInvokeScriptBurnTest() throws NodeException, IOException {
        testData.prepareDataForBurnTests();
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        long invokeBurnFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterBurnAssetInvoke(senderAddress, assetDAppAddress, testData.getOtherAmounts(), assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId())
        );
    }

    @Test
    @DisplayName("Ethereum invoke with Reissue transaction")
    void ethereumInvokeScriptReissueTest() throws NodeException, IOException {
        testData.prepareDataForReissueTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getOtherAmounts();
        long invokeReissueFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterEthereumReissueAssetInvoke(senderAddress, assetDAppAddress, payments, assetId, 2);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeReissueFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId())
        );
    }

    @Test
    @DisplayName("Ethereum invoke with Lease and WAVES payment")
    void ethereumInvokeScriptWithLeaseTest() throws NodeException, IOException {
        testData.prepareDataForLeaseTests(SUM_FEE, 0);
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, WAVES)
        );
    }

    @Test
    @DisplayName("Ethereum invoke with Lease cancel and WAVES payment")
    void ethereumInvokeScriptWithLeaseCancelTest() throws NodeException, IOException {
        testData.prepareDataForLeaseCancelTests(SUM_FEE, 0);
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, WAVES)
        );
    }

    @Test
    @DisplayName("Ethereum invoke with SponsorFee transaction")
    void ethereumInvokeScriptSponsorFeeTest() throws NodeException, IOException {
        testData.prepareDataForSponsorFeeTests();
        dAppCall = testData.getDAppCall();
        long invokeBurnFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        payments.add(Amount.of(0));
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, assetDAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();

        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId())
        );
    }

    @Test
    @DisplayName("Ethereum invoke with ScriptTransfer transaction")
    void ethereumInvokeScriptScriptTransferTest() throws NodeException, IOException {
        testData.prepareDataForScriptTransferTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getOtherAmounts();
        long invokeFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, assetId));

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesEthereumAfterCallerScriptTransfer(senderAddress, assetDAppAddress, dAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, assetId),
                () -> thirdAccountBalanceCheck(dAppAddress, calcBalances)
        );
    }

    @Test
    @DisplayName("Ethereum invoke dApp to dApp")
    void ethereumInvokeDAppToDAppTest() throws NodeException, IOException {
        testData.prepareDataForDAppToDAppTests(SUM_FEE);
        dAppCall = testData.getDAppCall();
        payments = testData.getOtherAmounts();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterEthereumDAppToDApp(senderAddress, dAppAddress, assetDAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId()),
                () -> thirdAccountBalanceCheck(assetDAppAddress, calcBalances)
        );
    }

    @Test
    @DisplayName("Ethereum invoke double nested for i.caller")
    void ethereumInvokeDoubleNestedForCaller() throws NodeException, IOException {
        testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.caller", "i.caller");
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        Address otherDAppAddress = testData.getOtherDAppAccount().address();
        Address assetDAppAddress = testData.getAssetDAppAccount().address();

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterDoubleNestedForCaller(senderAddress, dAppAddress, otherDAppAddress, assetDAppAddress, testData.getOtherAmounts(), assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId()),
                () -> thirdAccountBalanceCheck(assetDAppAddress, calcBalances),
                () -> fourthAccountBalanceCheck(otherDAppAddress, calcBalances)
        );
    }

    @Test
    @DisplayName("Ethereum invoke double nested for i.originCaller")
    void ethereumInvokeDoubleNestedForOriginCaller() throws NodeException, IOException {
        testData.prepareDataForDoubleNestedTest(SUM_FEE, "i.originCaller", "i.originCaller");
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        Address otherDAppAddress = testData.getOtherDAppAccount().address();
        Address assetDAppAddress = testData.getAssetDAppAccount().address();

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterDoubleNestedForOriginCaller(senderAddress, dAppAddress, otherDAppAddress, assetDAppAddress, testData.getOtherAmounts(), assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        assertAll(
                () -> checkEthereumInvoke(txSender, payload),
                () -> checkBalancesAfterTx(txSender, calcBalances, testData.getAssetId()),
                () -> thirdAccountBalanceCheck(assetDAppAddress, calcBalances),
                () -> fourthAccountBalanceCheck(otherDAppAddress, calcBalances)
        );
    }

    private void checkEthereumInvoke(EthereumInvokeTransactionSender txSender, EthereumTransaction.Invocation payload) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getEthTx().chainId()).isEqualTo(node().chainId()),
                () -> assertThat(txSender.getEthTx().type()).isEqualTo(EthereumTransaction.TYPE_TAG),
                () -> assertThat(txSender.getEthTx().version()).isEqualTo(ETHEREUM_TX_LATEST_VERSION),
                () -> assertThat(txSender.getEthTx().gasPrice()).isEqualTo(EthereumTransaction.DEFAULT_GAS_PRICE),
                () -> assertThat(txSender.getEthTx().timestamp()).isEqualTo(txSender.getTimestamp()),
                () -> assertThat(txSender.getEthTx().fee().assetId()).isEqualTo(WAVES),
                () -> assertThat(txSender.getEthTx().fee().value()).isEqualTo(txSender.getEthInvokeFee()),
                () -> assertThat(txSender.getEthTx().id()).isEqualTo(txSender.getEthTxId()),
                () -> assertThat(txSender.getEthTx().sender().address()).isEqualTo(senderAddress),
                () -> assertThat(payload.payments()).isEqualTo(payments),
                () -> assertThat(payload.function()).isEqualTo(dAppCall.getFunction()),
                () -> assertThat(payload.dApp()).isEqualTo(dAppCall.getDApp())
        );
    }

    private void checkBalancesAfterTx(EthereumInvokeTransactionSender txSender, InvokeCalculationsBalancesAfterTx calcBalances, AssetId assetId) {
        assertAll(
                () -> assertThat(node().getBalance(senderAddress)).isEqualTo(calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> assertThat(node().getBalance(txSender.getRecipientAddress())).isEqualTo(calcBalances.getDAppBalanceWavesAfterTransaction())
        );
        if (!assetId.isWaves()) {
            assertAll(
                    () -> assertThat(node().getAssetBalance(senderAddress, assetId)).isEqualTo(calcBalances.getCallerBalanceIssuedAssetsAfterTransaction()),
                    () -> assertThat(node().getAssetBalance(txSender.getRecipientAddress(), assetId)).isEqualTo(calcBalances.getDAppBalanceIssuedAssetsAfterTransaction())
            );
        }
    }

    private void thirdAccountBalanceCheck(Address acc, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(node().getBalance(acc)).isEqualTo(calcBalances.getAccBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(node().getAssetBalance(acc, assetId)).isEqualTo(calcBalances.getAccBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void fourthAccountBalanceCheck(Address dApp2, InvokeCalculationsBalancesAfterTx calcBalances) {
        assertThat(node().getBalance(dApp2)).isEqualTo(calcBalances.getOtherDAppBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(node().getAssetBalance(dApp2, assetId)).isEqualTo(calcBalances.getOtherDAppBalanceIssuedAssetsAfterTransaction());
        }
    }
}
