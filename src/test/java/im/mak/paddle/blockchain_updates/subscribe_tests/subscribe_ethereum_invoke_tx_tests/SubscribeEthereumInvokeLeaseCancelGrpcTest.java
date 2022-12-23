package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_ethereum_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.EthereumTestUser;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataResultLease.getEthereumInvokeMetadataCancelLeaseId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeLeaseCancelGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private String callerAddress;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private String senderAddressString;
    private long senderWavesBalanceBeforeTx;
    private long senderWavesBalanceAfterTx;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressString;
    private String dAppPK;
    private long dAppBalanceWavesBeforeTx;
    private long dAppBalanceWavesAfterTx;
    private long invokeFee;
    private List<Amount> payments;
    private long amountValue;
    private String encodeLeaseId;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForLeaseCancelTests(SUM_FEE, ONE_WAVES);
        setVersion(LATEST_VERSION);

        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    invokeFee = testData.getInvokeFee();
                },
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
                    senderAddressString = senderAddress.toString();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressString = testData.getDAppAddress();
                    dAppPK = testData.getDAppPublicKey();
                },
                () -> callerAddress = testData.getCallerAddress(),
                () -> payments = testData.getPayments(),
                () -> amountValue = testData.getWavesAmount().value(),
                () -> encodeLeaseId = Base58.encode(testData.getLeaseId())
        );

        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, testData.getAssetId());

        async(
                () -> {
                    senderWavesBalanceBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
                    senderWavesBalanceAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
                },
                () -> {
                    dAppBalanceWavesBeforeTx = calcBalances.getDAppBalanceWavesBeforeTransaction();
                    dAppBalanceWavesAfterTx = calcBalances.getDAppBalanceWavesAfterTransaction();
                }
        );
    }

    @Test
    @DisplayName("subscribe ethereum invoke with LeaseCancel and WAVES payment")
    void subscribeInvokeWithLeaseCancel() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, invokeFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        assertionsCheck(txSender, getTxIndex());
    }

    private void assertionsCheck(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, dAppAddressString, dAppCallFunction),
                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, encodeLeaseId),
                () -> checkEthereumPaymentMetadata(txIndex, 0, WAVES_STRING_ID, amountValue),
                () -> assertThat(getEthereumInvokeMetadataCancelLeaseId(txIndex, 0)).isEqualTo(testData.getLeaseId()),
                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID, senderWavesBalanceBeforeTx, senderWavesBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, dAppAddressString, null, dAppBalanceWavesBeforeTx, dAppBalanceWavesAfterTx),
                () -> checkStateUpdateBeforeLeasing(txIndex, 0, dAppAddressString, 0, amountValue),
                () -> checkStateUpdateBeforeLeasing(txIndex, 1, callerAddress, amountValue, 0),
                () -> checkStateUpdateAfterLeasing(txIndex, 0, dAppAddressString, 0, 0),
                () -> checkStateUpdateAfterLeasing(txIndex, 1, callerAddress, 0, 0),
                () -> checkStateUpdateIndividualLeases(txIndex, 0, amountValue, dAppPK, callerAddress, INACTIVE_STATUS_LEASE)
        );
    }
}
