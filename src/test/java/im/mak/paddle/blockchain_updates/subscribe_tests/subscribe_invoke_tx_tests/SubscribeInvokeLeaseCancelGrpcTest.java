package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataCancelLeaseId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseCancelGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private DAppCall dAppCall;
    private String dAppFunctionName;
    private Account caller;
    private String callerAddress;
    private String callerPK;
    private long callerBalanceWavesBeforeTx;
    private long callerBalanceWavesAfterTx;
    private Account dAppAccount;
    private String dAppAddress;
    private String dAppPK;
    private String dAppPKHash;
    private long dAppBalanceWavesBeforeTx;
    private long dAppBalanceWavesAfterTx;
    private long invokeFee;
    private List<Amount> amounts;
    private long amountValue;
    private String encodeLeaseId;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForLeaseCancelTests(SUM_FEE, ONE_WAVES);

        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppFunctionName = dAppCall.getFunction().name();
                    invokeFee = testData.getInvokeFee();
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = testData.getCallerAddress();
                    callerPK = testData.getCallerPublicKey();
                },
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = testData.getDAppAddress();
                    dAppPK = testData.getDAppPublicKey();
                    dAppPKHash = Base58.encode(dAppAccount.address().publicKeyHash());
                },
                () -> amounts = testData.getPayments(),
                () -> amountValue = testData.getWavesAmount().value(),
                () -> encodeLeaseId = Base58.encode(testData.getLeaseId())
        );

        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(caller.address(), dAppAccount.address(), amounts, testData.getAssetId());

        async(
                () -> {
                    callerBalanceWavesBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
                    callerBalanceWavesAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
                },
                () -> {
                    dAppBalanceWavesBeforeTx = calcBalances.getDAppBalanceWavesBeforeTransaction();
                    dAppBalanceWavesAfterTx = calcBalances.getDAppBalanceWavesAfterTransaction();
                }
        );
    }

    @Test
    @DisplayName("subscribe invoke with LeaseCancel and WAVES payment")
    void subscribeInvokeWithLeaseCancel() {
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall, amounts);
        txSender.invokeSenderWithPayment(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        assertionsCheck(getTxIndex(), txId);
    }

    private void assertionsCheck(int txIndex, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex, dAppPKHash),
                () -> checkMainMetadata(txIndex, dAppAddress, dAppFunctionName),
                () -> checkPaymentsSubscribe(txIndex, 0, amountValue, WAVES_STRING_ID),
                () -> checkPaymentMetadata(txIndex, 0, null, amountValue),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, encodeLeaseId),
                () -> assertThat(getInvokeMetadataCancelLeaseId(txIndex, 0)).isEqualTo(testData.getLeaseId()),

                () -> checkStateUpdateBalance(txIndex, 0, callerAddress, WAVES_STRING_ID, callerBalanceWavesBeforeTx, callerBalanceWavesAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, dAppAddress, null, dAppBalanceWavesBeforeTx, dAppBalanceWavesAfterTx),

                () -> checkStateUpdateBeforeLeasing(txIndex, 0, callerAddress, amountValue, 0),
                () -> checkStateUpdateBeforeLeasing(txIndex, 1, dAppAddress, 0, amountValue),

                () -> checkStateUpdateAfterLeasing(txIndex, 0, callerAddress, 0, 0),
                () -> checkStateUpdateAfterLeasing(txIndex, 1, dAppAddress, 0, 0),

                () -> checkStateUpdateIndividualLeases(txIndex, 0, amountValue, dAppPK, callerAddress, INACTIVE_STATUS_LEASE)
        );
    }
}
