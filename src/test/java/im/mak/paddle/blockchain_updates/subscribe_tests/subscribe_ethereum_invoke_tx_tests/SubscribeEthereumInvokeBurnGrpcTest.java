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

import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.checkEthereumInvokeBurnMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.checkEthereumInvokeIssueAssetMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateAssets;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.checkStateUpdateBalance;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeMetadataArgs.getBinaryValueBase58ArgumentEthereumMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getSenderAddressMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeBurnGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private static EthereumTestUser ethInstance;
    private static Address senderAddress;
    private static String senderAddressString;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private String assetIdStr;
    private DAppCall dAppCall;
    private Function dAppCallFunction;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressString;
    private List<Amount> payments;

    @BeforeEach
    void before() {
        async(
                () -> {
                    testData = new PrepareInvokeTestsData();
                    calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
                    testData.prepareDataForBurnTests();
                    dAppCall = testData.getDAppCall();
                    dAppCallFunction = dAppCall.getFunction();
                    assetId = testData.getAssetId();
                    assetIdStr = assetId.toString();
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                    assetDAppAddressString = assetDAppAddress.toString();
                    payments = testData.getOtherAmounts();
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
    }

    @Test
    @DisplayName("Subscribe Ethereum invoke with Burn")
    void subscribeInvokeWithBurn() throws NodeException, IOException {
        calcBalances.balancesAfterBurnAssetInvoke(senderAddress, assetDAppAddress, payments, assetId);
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, testData.getInvokeFee());
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
                () -> assertThat(getSenderAddressMetadata(txIndex)).isEqualTo(senderAddressString),
                () -> assertThat(getEthereumTransactionTimestampMetadata(txIndex)).isEqualTo(txSender.getEthTx().timestamp()),
                () -> assertThat(getEthereumTransactionFeeMetadata(txIndex)).isEqualTo(txSender.getEthInvokeFee()),
                () -> assertThat(getEthereumTransactionSenderPublicKeyMetadata(txIndex)).isEqualTo(txSender.getEthTx().sender().toString()),
                () -> assertThat(getEthereumInvokeDAppAddress(txIndex)).isEqualTo(assetDAppAddressString),
                () -> assertThat(getEthereumInvokeFunctionName(txIndex)).isEqualTo(dAppCallFunction.name()),
                () -> assertThat(getBinaryValueBase58ArgumentEthereumMetadata(txIndex, 0)).isEqualTo(assetIdStr),

                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, getIssueAssetData()),
                () -> checkEthereumInvokeBurnMetadata(txIndex, 0, testData.getAssetAmount()),
                () -> checkStateUpdateBalance(txIndex, 0,
                        senderAddressString,
                        WAVES_STRING_ID,
                        calcBalances.getCallerBalanceWavesBeforeTransaction(),
                        calcBalances.getCallerBalanceWavesAfterTransaction()),
                () -> checkStateUpdateBalance(txIndex, 1,
                        senderAddressString,
                        assetIdStr,
                        calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(), 0),
                () -> checkStateUpdateBalance(txIndex, 2,
                        assetDAppAddressString,
                        null,
                        0,
                        testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(txIndex, 0, getIssueAssetData(), testData.getAmountAfterInvokeIssuedAsset()),
                () -> checkStateUpdateAssets(txIndex, 1, testData.getAssetData(), testData.getAmountAfterInvokeDAppIssuedAsset())
        );
    }
}
