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
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeStateUpdateAssertions.*;
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

public class SubscribeEthereumInvokeDAppToDAppGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private String key1;
    private String key2;
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private String senderAddressString;
    private AssetId assetId;
    private String assetIdStr;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressString;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressString;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private List<Amount> payments;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    testData.prepareDataForDAppToDAppTests(SUM_FEE);
                    key1 = testData.getKeyForDAppEqualBar();
                    key2 = testData.getKey2ForDAppEqualBalance();
                    calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressString = dAppAddress.toString();
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                    assetDAppAddressString = assetDAppAddress.toString();
                    payments = testData.getOtherAmounts();
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
        assetDAppAccount.transfer(senderAddress, testData.getAssetAmount());

        fromHeight = node().getHeight();
    }

    @Test
    @DisplayName("subscribe ethereum invoke dApp to dApp")
    void subscribeInvokeWithDAppToDApp() throws NodeException, IOException {
        calcBalances.balancesAfterEthereumDAppToDApp(senderAddress, dAppAddress, assetDAppAddress, payments, assetId);
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, testData.getInvokeFee());
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        prepareInvoke(dAppAccount, testData);
        assertionsCheckDAppToDAppInvoke(txSender, getTxIndex());
    }

    private void assertionsCheckDAppToDAppInvoke(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, dAppAddressString, dAppCallFunction),
                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetDAppAddressString),
                () -> checkArgumentsEthereumMetadata(txIndex, 1, INTEGER, String.valueOf(testData.getIntArg())),
                () -> checkArgumentsEthereumMetadata(txIndex, 2, STRING, key1),
                () -> checkArgumentsEthereumMetadata(txIndex, 3, STRING, key2),
                () -> checkArgumentsEthereumMetadata(txIndex, 4, BINARY_BASE58, assetIdStr),
                () -> checkEthereumPaymentMetadata(txIndex, 0, WAVES_STRING_ID, testData.getWavesAmount().value()),
                () -> checkEthereumPaymentMetadata(txIndex, 1, assetIdStr, testData.getAssetAmount().value()),

                () -> checkEthereumDataMetadata(txIndex, 0,
                        INTEGER,
                        key1,
                        calcBalances.getInvokeResultData()),

                () -> checkEthereumDataMetadata(txIndex, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),

                () -> checkEthereumResultInvokesMetadata(txIndex, 0, testData.getAssetDAppAddress(), key1),
                () -> checkEthereumResultInvokesMetadataPayments(txIndex, 0, 0, assetIdStr, testData.getAssetAmount().value()),
                () -> checkEthereumStateChangeIntData(txIndex, 0, 0, testData),
                () -> checkEthereumStateChangesTransfers(txIndex, 0, 0, WAVES_STRING_ID, testData.getWavesAmount().value(), testData.getDAppAddress()),
                () -> checkEthereumStateChangesBurn(txIndex, 0, 0, testData.getAssetAmount()),
                () -> checkEthereumStateChangesReissue(txIndex, 0, 0, testData),
                () -> checkEthereumStateChangesSponsorFee(txIndex, 0, 0, testData),
                () -> checkEthereumStateChangesLease(txIndex, 0, 0, testData),
                () -> checkEthereumStateChangesLeaseCancel(txIndex, 0, 0),

                () -> checkStateUpdateBalance(txIndex, 0,
                        senderAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 1,
                        senderAddressString,
                        assetIdStr,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(), 0),

               () -> checkStateUpdateBalance(txIndex,
                        2,
                        dAppAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        3,
                        assetDAppAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        4,
                        assetDAppAddressString,
                        assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),
                () -> checkStateUpdateDataEntries(txIndex, 0, testData.getDAppAddress(), key1, calcBalances.getInvokeResultData()),
                () -> checkStateUpdateDataEntries(txIndex, 1, testData.getDAppAddress(), key2, String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))
        );
    }
}
