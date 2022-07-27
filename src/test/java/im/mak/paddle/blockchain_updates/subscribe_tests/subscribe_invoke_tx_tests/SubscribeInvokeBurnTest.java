package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.getInvokeScriptResult;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.getInvokeMetadataResultIssueAssetId;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.getTxInfo;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.balancesAfterBurnAssetInvoke;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptTx;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSender;

public class SubscribeInvokeBurnTest extends InvokeBaseTest {
    @Test
    @DisplayName("subscribe invoke with Burn")
    void subscribeInvokeWithBurn() {
        testsData.prepareDataForBurnTests();

        setVersion(LATEST_VERSION);
        balancesAfterBurnAssetInvoke(getCallerAccount(), getAssetDAppAccount(), getAmounts(), getAssetId());
        invokeSender(getCallerAccount(), getAssetDAppAccount(), getDAppCall());
        height = getTxInfo().height();
        subscribeResponseHandler(channel, getAssetDAppAccount(), height, height);
        prepareInvoke(getAssetDAppAccount());

        System.out.println("\n\n\n" + getInvokeScriptTx().function());
        assertionsCheck();
    }

    private void assertionsCheck() {
        checkInvokeSubscribe(getAssetAmount().value(), getFee());
        checkMainMetadata(0);
        checkIssueAssetMetadata(0,0, getIssuedAssetName(), getIssuedAssetDescription(),
                getQuantity(), getDecimals(), true, getNonce());
    }
}
