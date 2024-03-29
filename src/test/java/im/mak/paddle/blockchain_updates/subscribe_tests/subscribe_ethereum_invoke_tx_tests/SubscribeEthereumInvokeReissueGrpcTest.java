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
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeReissueGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private EthereumTestAccounts ethereumTestAccounts;
    private Address senderAddress;
    private String senderAddressString;
    private long senderWavesBalanceBeforeTx;
    private long senderWavesBalanceAfterTx;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressStr;
    private long assetDAppBalanceBeforeTx;
    private long assetDAppBalanceAfterTx;
    private AssetId assetId;
    private String assetIdStr;
    private long invokeFee;
    private List<Amount> payments;
    private long payment;
    private long amountAfterInvokeIssuedAsset;
    private long amountAfterInvokeDAppIssuedAsset;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForReissueTests();
        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    invokeFee = testData.getInvokeFee();
                },
                () -> {
                    try {
                        ethereumTestAccounts = new EthereumTestAccounts();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethereumTestAccounts.getSenderAddress();
                    senderAddressString = senderAddress.toString();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
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
                    payment = testData.getAssetAmount().value();
                },
                () -> amountAfterInvokeIssuedAsset = testData.getAmountAfterInvokeIssuedAsset(),
                () -> amountAfterInvokeDAppIssuedAsset = testData.getAmountAfterInvokeDAppIssuedAsset()
        );

        assetDAppAccount.transfer(senderAddress, Amount.of(payment, assetId));
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterEthereumReissueAssetInvoke(senderAddress, assetDAppAddress, payments, assetId, 2);

        async(
                () -> {
                    senderWavesBalanceBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction();
                    senderWavesBalanceAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction();
                },
                () -> {
                    assetDAppBalanceBeforeTx = calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction();
                    assetDAppBalanceAfterTx = calcBalances.getDAppBalanceIssuedAssetsAfterTransaction();
                }
        );
    }

    @Test
    @DisplayName("subscribe ethereum invoke with Reissue")
    void subscribeInvokeWithReissue() throws NodeException, IOException {
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeFee, ethereumTestAccounts);
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
                () -> checkEthereumInvokeMainInfo(txIndex, assetDAppAddressStr, dAppCallFunction),

                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, getIssueAssetData()),
                () -> checkEthereumInvokeReissueMetadata(txIndex, 0, assetIdStr, payment, true),
                () -> checkEthereumInvokeReissueMetadata(txIndex, 1, null, payment, true),

                () -> checkStateUpdateBalance(txIndex, 0, senderAddressString, WAVES_STRING_ID, senderWavesBalanceBeforeTx, senderWavesBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, senderAddressString, null, payment, 0),
                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddressStr, assetIdStr, assetDAppBalanceBeforeTx, assetDAppBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddressStr, null, 0, amountAfterInvokeIssuedAsset),

                () -> checkStateUpdateAssets(txIndex, 0, getIssueAssetData(), amountAfterInvokeIssuedAsset),
                () -> checkStateUpdateAssets(txIndex, 1, testData.getAssetData(), amountAfterInvokeDAppIssuedAsset)
        );
    }
}
