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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.checkEthereumInvokeMainInfo;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeStateUpdateAssertions.checkEthereumStateChangesTransfers;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;

import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeDoubleNestedCallerTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private EthereumTestAccounts ethereumTestUsers;
    private Function dAppCallFunction;
    private Address senderAddress;
    private String senderAddressString;
    private static final String callerForScript = "i.caller";
    private static final String originCallerForScript = "i.originCaller";
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressStr;
    private Account otherDAppAccount;
    private String otherDAppAddress;
    private Account assetDAppAccount;
    private String assetDAppAddress;
    private AssetId assetId;
    private List<Amount> amounts;
    private EthereumInvokeTransactionSender txSender;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private String key1;
    private String key2;
    private String assetIdStr;
    private String intArg;
    private String doubleIntArg;
    private long invokeFee;
    private long assetAmountValue;
    private long wavesAmountValue;
    private long secondWavesAmountValue;

    @Test
    @DisplayName("subscribe invoke double nested: " + callerForScript)
    void subscribeInvokeWithDoubleNestedCaller() throws NodeException, IOException {
        prepareDoubleNestedTest(callerForScript);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheckDoubleNestedInvoke(callerForScript, getTxIndex());
    }

    @Test
    @DisplayName("subscribe invoke double nested: " + originCallerForScript)
    void subscribeInvokeWithDoubleNestedOriginCaller() throws NodeException, IOException {
        prepareDoubleNestedTest(originCallerForScript);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        assertionsCheckDoubleNestedInvoke(originCallerForScript, getTxIndex());
    }

    private void prepareDoubleNestedTest(String callerType) throws IOException {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDoubleNestedTest(SUM_FEE, callerType, callerType);
        async(
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
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressStr = testData.getDAppAddress();
                },
                () -> {
                    otherDAppAccount = testData.getOtherDAppAccount();
                    otherDAppAddress = testData.getOtherDAppAddress();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = testData.getAssetDAppAddress();
                },
                () -> {
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                },
                () -> fromHeight = node().getHeight(),
                () -> amounts = testData.getOtherAmounts(),
                () -> key1 = testData.getKeyForDAppEqualBar(),
                () -> key2 = testData.getKey2ForDAppEqualBalance(),
                () -> invokeFee = testData.getInvokeFee(),
                () -> assetAmountValue = testData.getAssetAmount().value(),
                () -> wavesAmountValue = testData.getWavesAmount().value(),
                () -> secondWavesAmountValue = testData.getSecondWavesAmount().value(),
                () -> {
                    intArg = String.valueOf(testData.getIntArg());
                    doubleIntArg = String.valueOf(testData.getIntArg() * 2);
                },
                () -> {
                    DAppCall dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                }
        );
        assetDAppAccount.transfer(senderAddress, Amount.of(300_000_000L, assetId));
        txSender = new EthereumInvokeTransactionSender(dAppAddress, amounts, invokeFee, ethereumTestUsers);
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calculateBalancesForTest(callerType);
    }

    private void calculateBalancesForTest(String callerType) {
        if (callerType.equals(callerForScript)) {
            calcBalances.balancesEthereumAfterDoubleNestedForCaller(
                    senderAddress,
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    amounts,
                    assetId
            );
        } else if (callerType.equals(originCallerForScript)) {
            calcBalances.balancesEthereumAfterDoubleNestedForOriginCaller(
                    senderAddress,
                    dAppAccount.address(),
                    otherDAppAccount.address(),
                    assetDAppAccount.address(),
                    amounts,
                    assetId
            );
        }
    }

    private void assertionsCheckDoubleNestedInvoke(String callerType, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, dAppAddressStr, dAppCallFunction),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, otherDAppAddress),
                () -> checkArgumentsEthereumMetadata(txIndex, 1, BINARY_BASE58, assetDAppAddress),
                () -> checkArgumentsEthereumMetadata(txIndex, 2, INTEGER, intArg),
                () -> checkArgumentsEthereumMetadata(txIndex, 3, STRING, key1),
                () -> checkArgumentsEthereumMetadata(txIndex, 4, STRING, key2),
                () -> checkArgumentsEthereumMetadata(txIndex, 5, BINARY_BASE58, assetIdStr),

                () -> checkEthereumPaymentMetadata(txIndex, 0, WAVES_STRING_ID, wavesAmountValue),
                () -> checkEthereumPaymentMetadata(txIndex, 1, WAVES_STRING_ID, secondWavesAmountValue),
                () -> checkEthereumPaymentMetadata(txIndex, 2, assetIdStr, assetAmountValue),

                () -> checkEthereumDataMetadata(txIndex, 0, INTEGER, key1, doubleIntArg),
                () -> checkEthereumDataMetadata(txIndex, 1, INTEGER, key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkEthereumResultInvokesMetadata(txIndex, 0, assetDAppAddress, key1),

                () -> checkEthereumResultInvokesMetadataPayments(txIndex, 0, 0, assetIdStr, assetAmountValue),
                () -> checkEthereumInvokesMetadataCallArgs(txIndex, 0, 0, INTEGER, intArg),
                () -> checkEthereumInvokesMetadataCallArgs(txIndex, 0, 1, BINARY_VALUE, assetIdStr),
                () -> checkEthereumInvokesMetadataCallArgs(txIndex, 0, 2, BINARY_VALUE, otherDAppAddress)
        );

        if (callerType.equals(callerForScript)) {
            checkCallerForScript(txIndex);
        } else if (callerType.equals(originCallerForScript)) {
            checkOriginCallerForScript(txIndex);
        }
    }

    private void checkCallerForScript(int txIndex) {
        assertAll(
                () -> checkEthereumStateChangesTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, dAppAddressStr),
                () -> checkEthereumStateChangesNestedTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, dAppAddressStr),
                () -> checkEthereumResultNestedInvokes(txIndex, 0, 0, otherDAppAddress, testData.getKeyForDAppEqualBaz()),
                () -> checkEthereumNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0, INTEGER, intArg),
                () -> checkEthereumStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0, WAVES_STRING_ID, secondWavesAmountValue, assetDAppAddress),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 1, senderAddressString, assetIdStr,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getCallerBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 2, dAppAddressStr, WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddress, WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 4, assetDAppAddress, assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 5, otherDAppAddress, WAVES_STRING_ID,
                        calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressStr, key1, calcBalances.getInvokeResultData()),

                () -> checkStateUpdateDataEntries(txIndex, 1, dAppAddressStr, key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }

    private void checkOriginCallerForScript(int txIndex) {
        assertAll(
                () -> checkEthereumStateChangesTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, senderAddressString),
                () -> checkEthereumStateChangesNestedTransfers(txIndex, 0, 0, WAVES_STRING_ID, wavesAmountValue, senderAddressString),
                () -> checkEthereumResultNestedInvokes(txIndex, 0, 0, otherDAppAddress, testData.getKeyForDAppEqualBaz()),
                () -> checkEthereumNestedInvokesMetadataCallArgs(txIndex, 0, 0, 0, INTEGER, intArg),
                () -> checkEthereumStateChangesDoubleNestedTransfers(txIndex, 0, 0, 0, WAVES_STRING_ID, secondWavesAmountValue, senderAddressString),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 1, senderAddressString, assetIdStr,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getCallerBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 2, dAppAddressStr, WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddress, WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 4, assetDAppAddress, assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex, 5, otherDAppAddress, WAVES_STRING_ID,
                        calcBalances.getOtherDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getOtherDAppBalanceWavesAfterTransaction()),

                () -> checkStateUpdateDataEntries(txIndex, 0, dAppAddressStr, key1, calcBalances.getInvokeResultData()),
                () -> checkStateUpdateDataEntries(txIndex, 1, dAppAddressStr, key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }
}
