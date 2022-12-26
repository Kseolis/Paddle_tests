package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeMetadataAssertions.checkIssueAssetMetadata;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeStateUpdateAssertions.*;
import static im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers.InvokeTransactionAssertions.checkInvokeSubscribeTransaction;
import static im.mak.paddle.helpers.ConstructorRideFunctions.getIssueAssetData;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubscribeInvokeScriptTransferGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private DAppCall dAppCall;
    private String dAppFunctionName;
    private Account caller;
    private Address callerAddress;
    private String callerAddressStr;
    private String callerPK;
    private long callerBalanceWavesBeforeTx;
    private long callerBalanceWavesAfterTx;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private String assetDAppAddressStr;
    private String assetDAppPKHash;
    private long wavesAccDAppBalanceBeforeTx;
    private long wavesAccDAppBalanceAfterTx;
    private long assetAccDAppBalanceBeforeTx;
    private long assetAccDAppBalanceAfterTx;
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
    private long issueAssetDataVolume;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForScriptTransferTests();
        async(
                () -> {
                    dAppCall = testData.getDAppCall();
                    dAppFunctionName = dAppCall.getFunction().name();
                    invokeFee = testData.getInvokeFee();
                },
                () -> {
                    caller = testData.getCallerAccount();
                    callerAddress = caller.address();
                    callerAddressStr = testData.getCallerAddress();
                    callerPK = testData.getCallerPublicKey();
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
                    assetDAppPKHash = Base58.encode(assetDAppAccount.address().publicKeyHash());
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
                    issueAssetDataVolume = Long.parseLong(issueAssetData.get(VOLUME));
                }
        );
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterCallerScriptTransfer(callerAddress, assetDAppAddress, dAppAddress, payments, assetId);
        async(
                () -> callerBalanceWavesBeforeTx = calcBalances.getCallerBalanceWavesBeforeTransaction(),
                () -> callerBalanceWavesAfterTx = calcBalances.getCallerBalanceWavesAfterTransaction(),

                () -> wavesAccDAppBalanceBeforeTx = calcBalances.getDAppBalanceWavesBeforeTransaction(),
                () -> wavesAccDAppBalanceAfterTx = calcBalances.getDAppBalanceWavesAfterTransaction(),
                () -> assetAccDAppBalanceBeforeTx = calcBalances.getDAppBalanceIssuedAssetsBeforeTransaction(),
                () -> assetAccDAppBalanceAfterTx = calcBalances.getDAppBalanceIssuedAssetsAfterTransaction(),

                () -> wavesDAppBalanceBeforeTx = calcBalances.getAccBalanceWavesBeforeTransaction(),
                () -> wavesDAppBalanceAfterTx = calcBalances.getAccBalanceWavesAfterTransaction(),
                () -> dAppAssetBalanceBeforeTx = calcBalances.getAccBalanceIssuedAssetsBeforeTransaction(),
                () -> dAppAssetBalanceAfterTx = calcBalances.getAccBalanceIssuedAssetsAfterTransaction()

        );
    }

    @Test
    @DisplayName("subscribe invoke with ScriptTransfer")
    void subscribeInvokeWithScriptTransfer() {
        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, assetDAppAccount, dAppCall);
        txSender.invokeSender(LATEST_VERSION);
        String txId = txSender.getInvokeScriptId();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        assertionsCheck(txId, getTxIndex());
    }

    private void assertionsCheck(String txId, int txIndex) {
        assertAll(
                () -> checkInvokeSubscribeTransaction(invokeFee, callerPK, txId, txIndex, assetDAppPKHash),

                () -> checkMainMetadata(txIndex, assetDAppAddressStr, dAppFunctionName),
                () -> checkArgumentsMetadata(txIndex, 0, BINARY_BASE58, assetIdStr),
                () -> checkArgumentsMetadata(txIndex, 1, BINARY_BASE58, dAppAddressStr),
                () -> checkIssueAssetMetadata(txIndex, 0, issueAssetData),

                () -> checkTransfersMetadata(txIndex, 0, dAppAddressStr, assetIdStr, assetPayment),
                () -> checkTransfersMetadata(txIndex, 1, dAppAddressStr, null, assetPayment),
                () -> checkTransfersMetadata(txIndex, 2, dAppAddressStr, WAVES_STRING_ID, wavesPayment),
                () -> checkTransfersMetadata(txIndex, 3, callerAddressStr, WAVES_STRING_ID, wavesPayment),

                () -> checkStateUpdateBalance(txIndex, 0, callerAddressStr, WAVES_STRING_ID, callerBalanceWavesBeforeTx, callerBalanceWavesAfterTx),

                () -> checkStateUpdateBalance(txIndex, 1, assetDAppAddressStr, WAVES_STRING_ID, wavesAccDAppBalanceBeforeTx, wavesAccDAppBalanceAfterTx), // here 2
                () -> checkStateUpdateBalance(txIndex, 2, assetDAppAddressStr, null, 0, dAppAssetAmountAfter),
                () -> checkStateUpdateBalance(txIndex, 3, assetDAppAddressStr, assetIdStr, assetAccDAppBalanceBeforeTx, assetAccDAppBalanceAfterTx),

                () -> checkStateUpdateBalance(txIndex, 4, dAppAddressStr, WAVES_STRING_ID, wavesDAppBalanceBeforeTx, wavesDAppBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 5, dAppAddressStr, assetIdStr, dAppAssetBalanceBeforeTx, dAppAssetBalanceAfterTx),
                () -> checkStateUpdateBalance(txIndex, 6, dAppAddressStr, null, 0, assetPayment),

                () -> checkStateUpdateAssets(txIndex, 0, issueAssetData, issueAssetDataVolume)
        );
    }
}
