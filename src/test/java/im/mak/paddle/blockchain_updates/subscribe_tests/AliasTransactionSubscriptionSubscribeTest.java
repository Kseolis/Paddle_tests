package im.mak.paddle.blockchain_updates.subscribe_tests;

import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseSubscribeTest;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.CreateAliasTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.CreateAliasTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.AliasTransactionHandler.getAliasFromAliasTransaction;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AliasTransactionSubscriptionSubscribeTest extends BaseSubscribeTest {
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
        subscribeResponseHandler(CHANNEL, account, height, height, txId);
        checkAliasSubscribe(amountBefore, amountAfter, accountAddress, accountPublicKey, MIN_FEE);
    }

    @Test
    @DisplayName("Check subscription on alias transaction from DApp account")
    void subscribeTestForCreateAliasDAppAcc() {
        long amountBefore = dAppAccount.getWavesBalance();
        long amountAfter = amountBefore - SUM_FEE;
        newAlias = randomNumAndLetterString(4);

        CreateAliasTransactionSender txSender =
                new CreateAliasTransactionSender(dAppAccount, newAlias, SUM_FEE, LATEST_VERSION);

        String txId = txSender.getCreateAliasTx().id().toString();

        txSender.createAliasTransactionSender();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, account, height, height, txId);
        checkAliasSubscribe(amountBefore, amountAfter, dAppAccountAddress, dAppAccountPublicKey, SUM_FEE);
    }

    private void checkAliasSubscribe(long amountBefore, long amountAfter, String address, String publicKey, long fee) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getAliasFromAliasTransaction(0)).isEqualTo(newAlias),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(fee),
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(amountAfter),
                () -> assertThat(getTransactionId()).isEqualTo(getTransactionId())
        );
    }
}
