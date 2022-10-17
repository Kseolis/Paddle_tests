package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcMassTransferCheckers;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.MassTransferTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.MassTransferTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class MassTransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static Account sender;

    private static DefaultDApp420Complexity dAppSender;
    private static AssetId smartAssetId;

    private long amountValue;
    private long balanceBefore;

    private static List<Account> accountList;

    @BeforeAll
    static void setUp() {
        async(
                () -> sender = new Account(DEFAULT_FAUCET),
                () -> {
                    dAppSender = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    smartAssetId = dAppSender.issue(i -> i.name("Smart")
                            .quantity(900_000_000_000L)
                            .script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
                }
        );
    }

    @Test
    @DisplayName("Transaction Waves")
    void subscribeTestForMassTransferTransaction() {
        balanceBefore = sender.getWavesBalance();
        amountValue = getRandomInt(10_000, 1_000_000);
        accountList = accountListGenerator(MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER);

        MassTransferTransactionSender txSender =
                new MassTransferTransactionSender(sender, AssetId.WAVES, amountValue, accountList);
        txSender.massTransferTransactionSender(LATEST_VERSION);
        String txId = txSender.getMassTransferTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcMassTransferCheckers massTransferCheckers = new GrpcMassTransferCheckers(0, sender, txSender);
        massTransferCheckers.checkMassTransferGrpc(amountValue, balanceBefore);
    }

    @Test
    @DisplayName("DApp account & smart asset")
    void subscribeTestForMassTransferIssueSmartAssetTransaction() {
        balanceBefore = dAppSender.getWavesBalance();
        amountValue = getRandomInt(10_000, 1_000_000);
        accountList = accountListGenerator(MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER);

        MassTransferTransactionSender txSender =
                new MassTransferTransactionSender(dAppSender, smartAssetId, amountValue, accountList);
        txSender.massTransferTransactionSender(LATEST_VERSION);
        String txId = txSender.getMassTransferTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcMassTransferCheckers massTransferCheckers = new GrpcMassTransferCheckers(0, dAppSender, txSender);
        massTransferCheckers.checkMassTransferGrpc(amountValue, balanceBefore);
    }
}
