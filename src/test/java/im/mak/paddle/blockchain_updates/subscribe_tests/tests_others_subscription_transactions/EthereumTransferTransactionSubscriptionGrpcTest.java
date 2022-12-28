package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcEthereumTransferCheckers;
import im.mak.paddle.helpers.EthereumTestAccounts;
import im.mak.paddle.helpers.transaction_senders.EthereumTransferTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.getTxIndex;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class EthereumTransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private EthereumTestAccounts ethereumTestAccounts;
    private Address senderAddress;
    private Account recipient;
    private Address recipientAddress;
    private Amount amountTransfer;
    private static AssetId issuedSmartAssetId;
    private static Amount transferAmountSmartIssuedAsset;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    try {
                        ethereumTestAccounts = new EthereumTestAccounts();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethereumTestAccounts.getTransferSenderAddress();
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    recipient = new Account(3_0000_0000L);
                    recipientAddress = recipient.address();

                    issuedSmartAssetId = recipient.issue(i -> i.name("T_smart").quantity(1000).decimals(8).script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
                    recipient.transfer(senderAddress, Amount.of(recipient.getBalance(issuedSmartAssetId), issuedSmartAssetId), i -> i.additionalFee(0));

                    transferAmountSmartIssuedAsset = Amount.of(getRandomInt(1, 100), issuedSmartAssetId);
                },
                () -> amountTransfer = Amount.of(getRandomInt(100, 100_000))
        );
    }

    @Test
    @DisplayName("Check subscription on Ethereum transfer transaction")
    void subscribeTestForWavesTransferTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(ethereumTestAccounts, recipientAddress, amountTransfer, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txSender.getEthTxId().toString());
        GrpcEthereumTransferCheckers checkers = new GrpcEthereumTransferCheckers(getTxIndex(), txSender, amountTransfer);
        checkers.checkEthereumTransfer();
        checkers.checkEthereumTransferBalances();
    }

    @Test
    @DisplayName("Check subscription on Ethereum transfer smart asset transaction")
    void subscribeTestForSmartAssetTransferTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(ethereumTestAccounts, recipientAddress, transferAmountSmartIssuedAsset, SUM_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txSender.getEthTxId().toString());
        GrpcEthereumTransferCheckers checkers = new GrpcEthereumTransferCheckers(getTxIndex(), txSender, transferAmountSmartIssuedAsset);
        checkers.checkEthereumTransfer();
        checkers.checkEthereumTransferBalances();
    }
}
