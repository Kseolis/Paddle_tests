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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateDataEntries;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getSenderAddressMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataArgs.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeDAppToDAppGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
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
    @DisplayName("subscribe invoke dApp to dApp")
    void subscribeInvokeWithDAppToDApp() throws NodeException, IOException {
        calcBalances.balancesAfterDAppToDApp(senderAddress, dAppAddress, assetDAppAddress, payments, assetId);

        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(dAppAddress, payments, testData.getInvokeFee());
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        prepareInvoke(dAppAccount, testData);

        System.out.println(getAppend());

        assertionsCheckDAppToDAppInvoke(testData, txSender, getTxIndex(), txId);
    }

    public void assertionsCheckDAppToDAppInvoke(PrepareInvokeTestsData data, EthereumInvokeTransactionSender txSender, int txIndex, String txId) {
        String key1 = data.getKeyForDAppEqualBar();
        String key2 = data.getKey2ForDAppEqualBalance();
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> assertThat(getSenderAddressMetadata(txIndex)).isEqualTo(senderAddressString),
                () -> assertThat(getEthereumTransactionTimestampMetadata(txIndex)).isEqualTo(txSender.getEthTx().timestamp()),
                () -> assertThat(getEthereumTransactionFeeMetadata(txIndex)).isEqualTo(txSender.getEthInvokeFee()),
                () -> assertThat(getEthereumTransactionSenderPublicKeyMetadata(txIndex)).isEqualTo(txSender.getEthTx().sender().toString()),
                () -> assertThat(getEthereumInvokeDAppAddress(txIndex)).isEqualTo(dAppAddressString),
                () -> assertThat(getEthereumInvokeFunctionName(txIndex)).isEqualTo(dAppCallFunction.name()),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetDAppAddressString),
                () -> checkArgumentsEthereumMetadata(txIndex, 1, INTEGER, String.valueOf(data.getIntArg())),
                () -> checkArgumentsEthereumMetadata(txIndex, 2, STRING, key1),
                () -> checkArgumentsEthereumMetadata(txIndex, 3, STRING, key2),
                () -> checkArgumentsEthereumMetadata(txIndex, 4, BINARY_BASE58, assetIdStr),

                () -> checkEthereumDataMetadata(txIndex, 0,
                        INTEGER,
                        key1,
                        calcBalances.getInvokeResultData()),

                () -> checkEthereumDataMetadata(txIndex, 1,
                        INTEGER,
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction())),


                () -> checkEthereumResultInvokesMetadata(txIndex, 0, data.getAssetDAppAddress(), key1),

                () -> checkEthereumResultInvokesMetadataPayments(txIndex, 0, 0, assetIdStr, data.getAssetAmount().value()),

                () -> checkStateChangesTransfers(txIndex, 0, 0,
                        WAVES_STRING_ID,
                        data.getWavesAmount().value(),
                        data.getDAppAddress()
                )/*,
                () -> checkStateChangesBurn(txIndex, 0, 0, data.getAssetAmount()),
                () -> checkStateChangesReissue(txIndex, 0, 0, data),
                () -> checkStateChangesData(txIndex, 0, 0, data),
                () -> checkStateChangesSponsorFee(txIndex, 0, 0, data),
                () -> checkStateChangesLease(txIndex, 0, 0, data),
                () -> checkStateChangesLeaseCancel(txIndex, 0, 0),
*/


/*                () -> checkStateUpdateBalance(txIndex,
                        0,
                        data.getDAppAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getDAppBalanceWavesBeforeTransaction(),
                        calcBalances.getDAppBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        1,
                        data.getDAppAddress(),
                        assetIdStr,
                        calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        2,
                        data.getAssetDAppAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getAccBalanceWavesBeforeTransaction(),
                        calcBalances.getAccBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex,
                        3,
                        data.getAssetDAppAddress(),
                        assetIdStr,
                        calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                        calcBalances.getAccBalanceIssuedAssetsAfterTransaction()),

                () -> checkStateUpdateBalance(txIndex,
                        4,
                        data.getCallerAddress(),
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),


                () -> checkStateUpdateDataEntries(txIndex, 0,
                        data.getDAppAddress(),
                        key1,
                        calcBalances.getInvokeResultData()),
                () -> checkStateUpdateDataEntries(txIndex, 1,
                        data.getDAppAddress(),
                        key2,
                        String.valueOf(calcBalances.getAccBalanceWavesAfterTransaction()))*/
        );
    }
}
