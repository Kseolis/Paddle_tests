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
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeScriptTransferGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private String senderAddressStr;
    private long senderBalanceWavesBeforeTx;
    private long senderBalanceWavesAfterTx;
    private long senderAssetBalanceBeforeTx;
    private long senderAssetBalanceAfterTx;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressStr;
    private long wavesAccDAppBalanceBeforeTx;
    private long wavesAccDAppBalanceAfterTx;
    private Account dAppAccount;
    private Address dAppAddress;
    private String dAppAddressStr;
    private long wavesDAppBalanceBeforeTx;
    private long wavesDAppBalanceAfterTx;
    private long dAppAssetBalanceBeforeTx;
    private long dAppAssetBalanceAfterTx;
    private AssetId assetId;
    private String assetIdStr;
    private long invokeFee;
    private List<Amount> payments;
    private long assetPayment;
    private long wavesPayment;
    private long dAppAssetAmountAfter;
    private Map<String, String> issueAssetData;
    private long issueAssetVolume;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForScriptTransferTests();
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
                    senderAddressStr = senderAddress.toString();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    dAppAccount = testData.getDAppAccount();
                    dAppAddress = dAppAccount.address();
                    dAppAddressStr = testData.getDAppAddress();
                },
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                    assetDAppAddressStr = testData.getAssetDAppAddress();
                },
                () -> {
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                },
                () -> {
                    payments = testData.getOtherAmounts();
                    assetPayment = testData.getAssetAmount().value();
                    wavesPayment = testData.getWavesAmount().value();
                    dAppAssetAmountAfter = Long.parseLong(getIssueAssetData().get(VOLUME)) - assetPayment;
                },
                () -> {
                    issueAssetData = getIssueAssetData();
                    issueAssetVolume = Long.parseLong(issueAssetData.get(VOLUME));
                }
        );
        assetDAppAccount.transfer(senderAddress, Amount.of(assetPayment, testData.getAssetId()));
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesEthereumAfterCallerScriptTransfer(senderAddress, assetDAppAddress, dAppAddress, payments, assetId);
        async(
                () -> senderBalanceWavesBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction(),
                () -> senderBalanceWavesAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction(),
                () -> senderAssetBalanceBeforeTx = calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(),
                () -> senderAssetBalanceAfterTx = calcBalances.getCallerBalanceIssuedAssetsAfterTransaction(),

                () -> wavesAccDAppBalanceBeforeTx = calcBalances.getDAppBalanceWavesBeforeTransaction(),
                () -> wavesAccDAppBalanceAfterTx = calcBalances.getDAppBalanceWavesAfterTransaction(),

                () -> wavesDAppBalanceBeforeTx = calcBalances.getAccBalanceWavesBeforeTransaction(),
                () -> wavesDAppBalanceAfterTx = calcBalances.getAccBalanceWavesAfterTransaction(),
                () -> dAppAssetBalanceBeforeTx = calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                () -> dAppAssetBalanceAfterTx = calcBalances.getAccBalanceIssuedAssetsAfterTransaction()

        );
    }

    @Test
    @DisplayName("subscribe ethereum invoke with ScriptTransfer")
    void subscribeInvokeWithScriptTransfer() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeFee);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        prepareInvoke(assetDAppAccount, testData);
        assertionsCheck(txSender, getTxIndex());
    }

    private void assertionsCheck(EthereumInvokeTransactionSender txSender, int txIndex) {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getEthTx().id().toString()),
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressStr),
                () -> checkEthereumInvokeMainInfo(txIndex, assetDAppAddressStr, dAppCallFunction),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkArgumentsEthereumMetadata(txIndex, 1, BINARY_BASE58, dAppAddressStr),

                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, issueAssetData),

                () -> checkEthereumTransfersMetadata(txIndex, 0, dAppAddressStr, assetIdStr, assetPayment),
                () -> checkEthereumTransfersMetadata(txIndex, 1, dAppAddressStr, null, assetPayment),
                () -> checkEthereumTransfersMetadata(txIndex, 2, dAppAddressStr, WAVES_STRING_ID, wavesPayment),
                () -> checkEthereumTransfersMetadata(txIndex, 3, senderAddressStr, WAVES_STRING_ID, wavesPayment),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressStr, WAVES_STRING_ID, senderBalanceWavesBeforeTx, senderBalanceWavesAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, senderAddressStr, assetIdStr,  senderAssetBalanceBeforeTx, senderAssetBalanceAfterTx),

                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddressStr, WAVES_STRING_ID, wavesAccDAppBalanceBeforeTx, wavesAccDAppBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddressStr, null, 0, dAppAssetAmountAfter),
                () -> checkStateUpdateBalance(txIndex, 4, dAppAddressStr, WAVES_STRING_ID, wavesDAppBalanceBeforeTx, wavesDAppBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 5, dAppAddressStr, assetIdStr, dAppAssetBalanceBeforeTx, dAppAssetBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 6, dAppAddressStr, null, 0, assetPayment),

                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetVolume)
        );
    }
}
