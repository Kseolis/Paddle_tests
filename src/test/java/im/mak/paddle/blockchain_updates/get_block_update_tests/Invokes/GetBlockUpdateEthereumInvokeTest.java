package im.mak.paddle.blockchain_updates.get_block_update_tests.Invokes;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.ethereum_invoke_transaction_checkers.AssertionsCheckEthereumInvokeBurn;
import im.mak.paddle.helpers.EthereumTestAccounts;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;

public class GetBlockUpdateEthereumInvokeTest extends BaseGrpcTest {
    private PrepareInvokeTestsData testData;
    private EthereumTestAccounts ethereumTestUsers;
    private Address senderAddress;
    private InvokeCalculationsBalancesAfterTx calcBalances;
    private AssetId assetId;
    private Function dAppCallFunction;
    private Account assetDAppAccount;
    private Address assetDAppAddress;
    private List<Amount> payments;
    private final List<Integer> heightsList = new ArrayList<>();
    private long invokeFee;

    @BeforeEach
    void before() {
        testData = new PrepareInvokeTestsData();
        calcBalances = new InvokeCalculationsBalancesAfterTx(testData);
        heightsList.add(node().getHeight());
        testData.prepareDataForBurnTests();
        async(
                () -> dAppCallFunction = testData.getDAppCall().getFunction(),
                () -> assetId = testData.getAssetId(),
                () -> {
                    assetDAppAccount = testData.getAssetDAppAccount();
                    assetDAppAddress = assetDAppAccount.address();
                },
                () -> payments = testData.getOtherAmounts(),
                () -> invokeFee = testData.getInvokeFee(),
                () -> {
                    try {
                        ethereumTestUsers = new EthereumTestAccounts();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethereumTestUsers.getSenderAddress();
                    node().faucet().transfer(senderAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                }
        );
        assetDAppAccount.transfer(senderAddress, testData.getAssetAmount());
    }

    @Test
    @DisplayName("GetBlockUpdate Ethereum invoke with Burn")
    void subscribeInvokeWithBurn() throws NodeException, IOException {
        calcBalances.balancesAfterBurnAssetInvoke(senderAddress, assetDAppAddress, payments, assetId);
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(assetDAppAddress, payments, invokeFee, ethereumTestUsers);
        txSender.sendingAnEthereumInvokeTransaction(dAppCallFunction);
        String txId = txSender.getEthTxId().toString();
        heightsList.add(node().getHeight());

        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, txId);
        int txIndex = getBlockUpdateHandler.getTxIndex();
        AssertionsCheckEthereumInvokeBurn checks = new AssertionsCheckEthereumInvokeBurn(testData, txSender, txIndex);
        checks.assertionsCheckEthereumInvokeBurn(calcBalances);
    }
}
