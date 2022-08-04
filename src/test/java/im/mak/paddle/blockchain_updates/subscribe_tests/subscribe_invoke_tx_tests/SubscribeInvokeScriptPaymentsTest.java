package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.InvokeTransactionAssertions.checkInvokeSubscribe;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.balancesAfterCallerInvokeAsset;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.invokeSenderWithPayment;
import static im.mak.paddle.util.Constants.SUM_FEE;

public class SubscribeInvokeScriptPaymentsTest extends InvokeBaseTest{
    @Test
    @DisplayName("subscribe invoke with payments")
    void subscribeInvokeWithScriptPayments() {
        testsData.prepareDataForPaymentsTests();

        setVersion(LATEST_VERSION);
        balancesAfterCallerInvokeAsset(getCallerAccount(), getDAppAccount(), getAmounts(), getAssetId());
        invokeSenderWithPayment(getCallerAccount(), getDAppAccount(), getDAppCall(), getAmounts());

        height = node().getHeight();
        subscribeResponseHandler(channel, getDAppAccount(), height, height);
        prepareInvoke(getDAppAccount());

        System.out.println(getAppend());

        checkInvokeSubscribe(SUM_FEE);
    }
}
