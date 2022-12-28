package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_ethereum_invoke_tx_tests;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.EthereumTestAccounts;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeDeleteEntryGrpcTest extends BaseGrpcTest {
    private EthereumTestAccounts ethereumTestUsers;
    private Address senderAddress;
    private String senderAddressString;
    private long senderWavesBalanceBeforeTx;
    private long senderWavesBalanceAfterTx;
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressString;
    private long dAppAccountWavesBalanceBeforeTx;
    private long dAppAccountWavesBalanceAfterTx;
    private List<Amount> payments;
    private long payment;
    private long invokeFee;
    private String intVal;
    private String valAfter;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    testData.prepareDataForDeleteEntryTests();
                    assetId = testData.getAssetId();
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressString = dAppAddress.toString();
                    payments = testData.getPayments();
                    payment = testData.getWavesAmount().value();
                    invokeFee = testData.getInvokeFee();
                    intVal = String.valueOf(testData.getIntArg());

                    calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
                    calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);
                    senderWavesBalanceBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
                    senderWavesBalanceAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
                    dAppAccountWavesBalanceBeforeTx = calcBalances.getDAppBalanceWavesBeforeTransaction();
                    dAppAccountWavesBalanceAfterTx = calcBalances.getDAppBalanceWavesAfterTransaction();
                },
                () -> {
                    try {
                        ethereumTestUsers = new EthereumTestAccounts();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethereumTestUsers.getSenderAddress();
                    senderAddressString = senderAddress.toString();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> valAfter = String.valueOf(0)

        );
        fromHeight = node().getHeight();
    }

    @Test
    @DisplayName("subscribe ethereum invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, invokeFee, ethereumTestUsers);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheck(txSender, getTxIndex());
    }

    private void assertionsCheck(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, dAppAddressString, dAppCallFunction),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, INTEGER, intVal),
                () -> checkEthereumPaymentMetadata(txIndex, 0, null, payment),

                () -> checkEthereumDataMetadata(txIndex, 0, INTEGER, DATA_ENTRY_INT, intVal),
                () -> checkEthereumDataMetadata(txIndex, 1, INTEGER, DATA_ENTRY_INT, valAfter),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID, senderWavesBalanceBeforeTx, senderWavesBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, dAppAddressString, WAVES_STRING_ID, dAppAccountWavesBalanceBeforeTx, dAppAccountWavesBalanceAfterTx),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressString, DATA_ENTRY_INT, valAfter)
        );
    }
}
