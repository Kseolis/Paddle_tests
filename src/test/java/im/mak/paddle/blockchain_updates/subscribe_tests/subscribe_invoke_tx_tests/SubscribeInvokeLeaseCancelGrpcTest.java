package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.checkMainMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.checkPaymentMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata.InvokeMetadataResultLease.getInvokeMetadataCancelLeaseId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeLeaseCancelGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private DAppCall dAppCall;
    private Account caller;
    private String callerAddress;
    private String callerPK;
    private long callerBalanceWavesBeforeTx;
    private long callerBalanceWavesAfterTx;
    private Account dAppAccount;
    private String dAppAddress;
    private String dAppPK;
    private long dAppBalanceWavesBeforeTx;
    private long dAppBalanceWavesAfterTx;
    private long invokeFee;
    private List<Amount> amounts;
    private long amountValue;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForLeaseCancelTests(SUM_FEE, ONE_WAVES);
        setVersion(LATEST_VERSION);

        async(
                () -> {
                    dAppCall = testData.getDAppCall();
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
                },
                () -> amounts = testData.getPayments(),
                () -> amountValue = testData.getWavesAmount().value()
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
        txSender.invokeSenderWithPayment();
        String txId = txSender.getInvokeScriptId();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(dAppAccount, testData);

        assertionsCheck(getTxIndex(), txId);
    }

    private void assertionsCheck(int txIndex, String txId) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex),
                () -> checkMainMetadata(txIndex),
                () -> checkPaymentsSubscribe(txIndex, 0, amountValue, WAVES_STRING_ID),
                () -> checkPaymentMetadata(txIndex, 0, null, amountValue),
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
