package im.mak.paddle.transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.EthereumTestUser;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.EthereumInvokeTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class EthereumInvokeTransactionTest {
    private static PrepareInvokeTestsData testData;
    private static EthereumTestUser ethInstance;
    private static Address senderAddress;
    private static Account recipient;
    private static Address recipientAddress;
    private static List<Amount> amounts = new ArrayList<>();
    private DAppCall dAppCall;


    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    testData = new PrepareInvokeTestsData();
                    recipient = testData.getDAppAccount();
                    recipientAddress = recipient.address();
                    amounts = testData.getAmounts();
                }

        );
    }

    @Test
    @DisplayName("Test of transferring a minimum amount using an Ethereum transaction")
    void subscribeTestForTransferMinimumAmountTransaction() throws NodeException, IOException {
        testData.prepareDataForDataDAppTests();
        dAppCall = testData.getDAppCall();
        EthereumInvokeTransactionSender txSender = new EthereumInvokeTransactionSender(recipientAddress, amounts, FEE_FOR_DAPP_ACC);
        txSender.sendingAnEthereumInvokeTransaction(dAppCall.getFunction());
        System.out.println(txSender.getEthTx().payload());
    }
}
