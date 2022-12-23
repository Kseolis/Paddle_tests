package im.mak.paddle.blockchain_updates.get_block_update_tests.Invokes;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdatesRangeHandler;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.SubscribeInvokeDAppToDAppGrpcTest.assertionsCheckDAppToDAppInvoke;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Constants.SUM_FEE;

public class GetBlockUpdatesRangeInvokeTest extends BaseGrpcTest {
    private static PrepareInvokeTestsData testData;
    private static AssetId assetId;
    private static DAppCall dAppCall;
    private static Account caller;
    private static Account dAppAccount;
    private static Account assetDAppAccount;
    private static List<Amount> amounts;

    @BeforeAll
    static void before() {
        testData = new PrepareInvokeTestsData();
        testData.prepareDataForDAppToDAppTests(SUM_FEE);
        assetId = testData.getAssetId();
        dAppCall = testData.getDAppCall();
        caller = testData.getCallerAccount();
        dAppAccount = testData.getDAppAccount();
        assetDAppAccount = testData.getAssetDAppAccount();
        amounts = testData.getOtherAmounts();
    }

    @Test
    @DisplayName("GetBlockUpdatesRange invoke dApp to dApp")
    void getBlockUpdateInvokeWithDAppToDApp() {
        fromHeight = node().getHeight();
        InvokeCalculationsBalancesAfterTx calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        calcBalances.balancesAfterDAppToDApp(caller.address(), dAppAccount.address(), assetDAppAccount.address(), amounts, assetId);

        InvokeScriptTransactionSender txSender = new InvokeScriptTransactionSender(caller, dAppAccount, dAppCall);
        setVersion(LATEST_VERSION);
        txSender.invokeSender();
        String txId = txSender.getInvokeScriptId();
        toHeight = node().getHeight();

        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        int txIndex = handler.getTxIndex();
        assertionsCheckDAppToDAppInvoke(testData, calcBalances, txId, txIndex);
    }
}
