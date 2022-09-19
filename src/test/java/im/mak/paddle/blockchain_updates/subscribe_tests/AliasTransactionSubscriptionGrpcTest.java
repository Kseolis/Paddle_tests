package im.mak.paddle.blockchain_updates.subscribe_tests;

import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcAliasCheckers;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.CreateAliasTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.CreateAliasTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class AliasTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static Account account;
    private static String accountAddress;
    private static String accountPublicKey;

    private static DefaultDApp420Complexity dAppAccount;
    private static String dAppAccountAddress;
    private static String dAppAccountPublicKey;
    protected String newAlias;

    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    dAppAccountAddress = dAppAccount.address().toString();
                    dAppAccountPublicKey = dAppAccount.publicKey().toString();
                },
                () -> {
                    account = new Account(DEFAULT_FAUCET);
                    accountAddress = account.address().toString();
                    accountPublicKey = account.publicKey().toString();
                }
        );
    }

    @Test
    @DisplayName("Check subscription on alias transaction")
    void subscribeTestForCreateAlias() {
        long amountBefore = account.getWavesBalance();
        long amountAfter = amountBefore - MIN_FEE;
        newAlias = randomNumAndLetterString(15);

        CreateAliasTransactionSender txSender =
                new CreateAliasTransactionSender(account, newAlias, MIN_FEE, LATEST_VERSION);
        txSender.createAliasTransactionSender();

        String txId = txSender.getCreateAliasTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcAliasCheckers grpcAliasCheckers = new GrpcAliasCheckers(0, accountAddress, accountPublicKey, txId);
        grpcAliasCheckers.checkAliasGrpc(newAlias, amountBefore, amountAfter, MIN_FEE);
    }

    @Test
    @DisplayName("Check subscription on alias transaction from DApp account")
    void subscribeTestForCreateAliasDAppAcc() {
        long amountBefore = dAppAccount.getWavesBalance();
        long amountAfter = amountBefore - SUM_FEE;
        newAlias = randomNumAndLetterString(4);

        CreateAliasTransactionSender txSender =
                new CreateAliasTransactionSender(dAppAccount, newAlias, SUM_FEE, LATEST_VERSION);
        txSender.createAliasTransactionSender();

        String txId = txSender.getCreateAliasTx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcAliasCheckers grpcAliasCheckers = new GrpcAliasCheckers(0, dAppAccountAddress, dAppAccountPublicKey, txId);
        grpcAliasCheckers.checkAliasGrpc(newAlias, amountBefore, amountAfter, SUM_FEE);
    }
}
