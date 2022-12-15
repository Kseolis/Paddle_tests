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
import java.util.Map;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeIssueGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private String senderAddressString;
    private long senderWavesBalanceBeforeTx;
    private long senderWavesBalanceAfterTx;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressString;

    private Map<String, String> issueAssetData;
    private long issueAssetDataVolume;
    private Map<String, String> assetDataForIssue;
    private AssetId assetId;
    private List<Amount> payments;
    private long assetDataForIssueVolume;
    private long invokeFee;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    testData.prepareDataForIssueTests();
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                    assetDAppAddressString = testData.getAssetDAppAddress();
                    issueAssetData = getIssueAssetData();
                    issueAssetDataVolume = Long.parseLong(issueAssetData.get(VOLUME));
                    assetDataForIssue = testData.getAssetDataForIssue();
                    assetDataForIssueVolume = Long.parseLong(assetDataForIssue.get(VOLUME));
                    assetId = testData.getAssetId();
                    payments = testData.getPayments();
                    invokeFee = testData.getInvokeFee();
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
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterReissueAssetInvoke(senderAddress, assetDAppAccount.address(), payments, assetId);
        senderWavesBalanceBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
        senderWavesBalanceAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
    }

    @Test
    @DisplayName("subscribe invoke with Issue")
    void prepareDataForIssueTests() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);
        System.out.println(getAppend());
        assertionsCheck(txSender, getTxIndex());
    }

    private void assertionsCheck(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressString),
                () -> checkEthereumInvokeMainInfo(txIndex, assetDAppAddressString, dAppCallFunction),

                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, issueAssetData),
                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 1, assetDataForIssue),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID, senderWavesBalanceBeforeTx, senderWavesBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, assetDAppAddressString, null, 0, issueAssetDataVolume),
                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddressString, null, 0, assetDataForIssueVolume),

                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 1, assetDataForIssue, assetDataForIssueVolume)
        );
    }
}
