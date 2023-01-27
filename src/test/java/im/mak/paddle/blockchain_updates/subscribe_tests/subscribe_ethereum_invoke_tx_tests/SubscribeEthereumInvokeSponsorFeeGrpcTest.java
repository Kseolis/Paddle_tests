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
import java.util.Map;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.EthereumInvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeEthereumInvokeSponsorFeeGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private EthereumTestAccounts ethereumTestAccounts;
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
    private long dAppAddressAssetBalanceBeforeTx;
    private long dAppAddressAssetBalanceAfterTx;
    private AssetId assetId;
    private String assetIdStr;
    private long invokeFee;
    private List<Amount> payments;
    private long payment;
    private Map<String, String> assetData;
    private long assetDataVolume;
    private Map<String, String> issueAssetData;
    private long issueAssetDataVolume;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForSponsorFeeTests();
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
                    senderAddressStr = senderAddress.toString();
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
                () -> payments = testData.getOtherAmounts(),
                () -> payment = testData.getAssetAmount().value(),
                () -> {
                    issueAssetData = getIssueAssetData();
                    issueAssetDataVolume = Long.parseLong(issueAssetData.get(VOLUME));
                },
                () -> {
                    assetData = testData.getAssetData();
                    assetDataVolume = Long.parseLong(assetData.get(VOLUME));
                }

        );
        assetDAppAccount.transfer(senderAddress, Amount.of(payment, assetId));
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterEthereumSponsorFeeInvoke(senderAddress, assetDAppAddress, payments, assetId);
        async(
                () -> senderBalanceWavesBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction(),
                () -> senderBalanceWavesAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction(),
                () -> senderAssetBalanceBeforeTx = calcBalances.getCallerBalanceIssuedAssetsBeforeTransaction(),
                () -> senderAssetBalanceAfterTx = calcBalances.getCallerBalanceIssuedAssetsAfterTransaction(),
                () -> dAppAddressAssetBalanceBeforeTx = calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                () -> dAppAddressAssetBalanceAfterTx = calcBalances.getDAppBalanceIssuedAssetsAfterTransaction()
        );
    }

    @Test
    @DisplayName("subscribe ethereum invoke with SponsorFee")
    void subscribeInvokeWithSponsorFee() throws NodeException, IOException {
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
                () -> checkEthereumMainMetadata(txSender, txIndex, senderAddressStr),
                () -> checkEthereumInvokeMainInfo(txIndex, assetDAppAddressStr, dAppCallFunction),
                () -> checkArgumentsEthereumMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkEthereumPaymentMetadata(txIndex, 0, assetIdStr, payment),
                () -> checkEthereumInvokeIssueAssetMetadata(txIndex, 0, issueAssetData),
                () -> checkEthereumSponsorFeeMetadata(txIndex, 0, assetIdStr, payment),
                () -> checkEthereumSponsorFeeMetadata(txIndex, 1, null, payment),
                () -> checkStateUpdateBalance(txIndex, 0, senderAddressStr, WAVES_STRING_ID, senderBalanceWavesBeforeTx, senderBalanceWavesAfterTx),
                () -> checkStateUpdateBalance(txIndex, 1, senderAddressStr, assetIdStr, senderAssetBalanceBeforeTx, senderAssetBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddressStr, assetIdStr, dAppAddressAssetBalanceBeforeTx, dAppAddressAssetBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddressStr, null, 0, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetDataVolume),
                () -> checkStateUpdateAssets(txIndex, 1, assetData, assetDataVolume),
                () -> checkStateUpdateAssetsSponsorship(txIndex, 0, payment),
                () -> checkStateUpdateAssetsSponsorship(txIndex, 1, payment)
        );
    }
}
