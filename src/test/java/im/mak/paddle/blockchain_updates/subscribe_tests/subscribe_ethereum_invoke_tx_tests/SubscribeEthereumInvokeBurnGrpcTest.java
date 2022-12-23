package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_ethereum_invoke_tx_tests;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.AssertionsCheckEthereumInvokeBurn;
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
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class SubscribeEthereumInvokeBurnGrpcTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private EthereumTestUser ethInstance;
    private Address senderAddress;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private Function dAppCallFunction;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private List<Amount> payments;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        testData.prepareDataForBurnTests();
        async(
                () -> dAppCallFunction = testData.getDAppCall().getFunction(),
                () -> assetId = testData.getAssetId(),
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                },
                () -> payments = testData.getOtherAmounts(),
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
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
        AssertionsCheckEthereumInvokeBurn checks = new AssertionsCheckEthereumInvokeBurn(testData, txSender, getTxIndex());
        checks.assertionsCheckEthereumInvokeBurn(calcBalances);
    }
}
