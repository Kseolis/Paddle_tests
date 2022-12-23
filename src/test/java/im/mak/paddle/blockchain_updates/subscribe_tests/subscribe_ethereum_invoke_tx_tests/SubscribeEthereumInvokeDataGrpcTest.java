package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_ethereum_invoke_tx_tests;

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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeDataGrpcTest extends BaseGrpcTest {
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private String senderAddressString;
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressString;
    private List<Amount> payments;
    private long payment;
    private String intVal;
    private String binVal;
    private String boolArg;
    private String strVal;
    private long invokeFee;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    testData.prepareDataForDataDAppTests(SUM_FEE, ONE_WAVES);
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
                    binVal = String.valueOf(testData.getBase64String());
                    boolArg = String.valueOf(testData.getBoolArg());
                    strVal = testData.getStringArg();

                    setVersion(LATEST_VERSION);
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
                }
        );
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterPaymentInvoke(senderAddress, dAppAddress, payments, assetId);
    }

    @Test
    @DisplayName("subscribe ethereum invoke with DataDApp")
    void subscribeInvokeWithDataDApp() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, invokeFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        checkersAssertionsDataInvoke(txSender, getTxIndex());
    }

    private void checkersAssertionsDataInvoke(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, dAppAddressString, dAppCallFunction),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, INTEGER, intVal),
                () -> checkArgumentsEthereumMetadata(txIndex, 1, BINARY_BASE64, binVal),
                () -> checkArgumentsEthereumMetadata(txIndex, 2, BOOLEAN, boolArg),
                () -> checkArgumentsEthereumMetadata(txIndex, 3, STRING, strVal),

                () -> checkEthereumPaymentMetadata(txIndex, 0, WAVES_STRING_ID, payment),

                () -> checkEthereumDataMetadata(txIndex, 0, INTEGER, DATA_ENTRY_INT, intVal),
                () -> checkEthereumDataMetadata(txIndex, 1, BINARY_BASE64, DATA_ENTRY_BYTE, binVal),
                () -> checkEthereumDataMetadata(txIndex, 2, BOOLEAN, DATA_ENTRY_BOOL, boolArg),
                () -> checkEthereumDataMetadata(txIndex, 3, STRING, DATA_ENTRY_STR, strVal),

                () -> checkStateUpdateBalance(txIndex,
                        0,
                        senderAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        1,
                        dAppAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressString, DATA_ENTRY_INT, intVal),
                () -> checkStateUpdateDataEntries(txIndex, 1, dAppAddressString, DATA_ENTRY_BYTE, binVal),
                () -> checkStateUpdateDataEntries(txIndex, 2, dAppAddressString, DATA_ENTRY_BOOL, boolArg),
                () -> checkStateUpdateDataEntries(txIndex, 3, dAppAddressString, DATA_ENTRY_STR, strVal)
        );
    }
}
