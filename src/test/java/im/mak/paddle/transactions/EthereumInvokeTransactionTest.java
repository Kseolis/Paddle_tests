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
    private static Account recipient;
    private static Address recipientAddress;
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
                    recipient = testData.getDAppAccount();
                    recipientAddress = recipient.address();
                }

        );
    }

    @Test
    @DisplayName("Ethereum invoke with DataDApp and issue asset payment")
    void ethereumInvokeScriptWithDataDAppTest() throws NodeException, IOException {
        testData.prepareDataForDataDAppTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, recipientAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, WAVES);
    }

    @Test
    @DisplayName("Ethereum invoke with DeleteEntry")
    void ethereumInvokeScriptDeleteEntryTest() throws NodeException, IOException {
        testData.prepareDataForDeleteEntryTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, recipientAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, WAVES);
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

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, testData.getAssetId());
    }

    @Test
    @DisplayName("Ethereum invoke with Reissue transaction")
    void ethereumInvokeScriptReissueTest() throws NodeException, IOException {
        testData.prepareDataForReissueTests();
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        long invokeBurnFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, testData.getAssetId());
    }

    @Test
    @DisplayName("Ethereum invoke with Lease and WAVES payment")
    void ethereumInvokeScriptWithLeaseTest() throws NodeException, IOException {
        testData.prepareDataForLeaseTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, recipientAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, WAVES);
    }

    @Test
    @DisplayName("Ethereum invoke with Lease cancel and WAVES payment")
    void ethereumInvokeScriptWithLeaseCancelTest() throws NodeException, IOException {
        testData.prepareDataForLeaseCancelTests();
        dAppCall = testData.getDAppCall();
        payments = testData.getPayments();
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, recipientAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, WAVES);
    }

    @Test
    @DisplayName("Ethereum invoke with SponsorFee transaction")
    void ethereumInvokeScriptSponsorFeeTest() throws NodeException, IOException {
        testData.prepareDataForSponsorFeeTests();
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));

        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);

        long invokeBurnFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), payments);

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, testData.getAssetId());
    }

    @Test
    @DisplayName("Ethereum invoke with ScriptTransfer transaction")
    void ethereumInvokeScriptScriptTransferTest() throws NodeException, IOException {
        testData.prepareDataForScriptTransferTests();
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        long invokeBurnFee = testData.getInvokeFee();
        AssetDAppAccount assetDAppAccount = testData.getAssetDAppAccount();
        Address assetDAppAddress = assetDAppAccount.address();
        assetDAppAccount.transfer(senderAddress, Amount.of(10_000_000L, testData.getAssetId()));

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, assetDAppAddress, payments, invokeBurnFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), testData.getOtherAmounts());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, testData.getAssetId());
    }

    @Test
    @DisplayName("Ethereum invoke dApp to dApp")
    void ethereumInvokeDAppToDAppTest() throws NodeException, IOException {
        testData.prepareDataForDAppToDAppTests(SUM_FEE);
        dAppCall = testData.getDAppCall();
        payments.add(Amount.of(0));
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(senderAddress, recipientAddress, payments, SUM_FEE);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction(), testData.getOtherAmounts());

        EthereumTransaction.Invocation payload = (EthereumTransaction.Invocation) txSender.getEthTx().payload();
        checkEthereumInvoke(txSender, payload);
        checkBalancesAfterTx(txSender, testData.getAssetId());
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

    private void checkBalancesAfterTx(EthereumInvokeTransactionSender txSender, AssetId assetId) {
        assertAll(
                () -> assertThat(node().getBalance(senderAddress)).isEqualTo(txSender.getBalances().getSenderBalanceAfterEthTransaction()),
                () -> assertThat(node().getBalance(txSender.getRecipientAddress())).isEqualTo(txSender.getBalances().getRecipientBalanceAfterEthTransaction())
        );
        if (!assetId.isWaves()) {
            assertAll(
                    () -> assertThat(node().getAssetBalance(senderAddress, assetId)).isEqualTo(txSender.getBalances().getSenderAssetBalanceAfterTransaction()),
                    () -> assertThat(node().getAssetBalance(txSender.getRecipientAddress(), assetId)).isEqualTo(txSender.getBalances().getRecipientAssetBalanceAfterTransaction())
            );
        }
    }
}
