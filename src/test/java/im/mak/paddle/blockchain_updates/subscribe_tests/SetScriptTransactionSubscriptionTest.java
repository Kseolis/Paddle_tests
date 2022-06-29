package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetScriptTransactionHandler.getScriptFromSetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.helpers.transaction_senders.SetScriptTransactionSender.*;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;
import static im.mak.paddle.util.ScriptUtil.fromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SetScriptTransactionSubscriptionTest extends BaseTest {

    private Account account;
    private String address;
    private String publicKey;

    private long wavesAmountAfterSetAssetScript;

    private final Base64String script = node()
            .compileScript(fromFile("ride_scripts/permissionOnUpdatingKeyValues.ride")).script();

    @BeforeEach
    void setUp() {
        account = new Account(DEFAULT_FAUCET);
        address = account.address().toString();
        publicKey = account.publicKey().toString();
    }

    @Test
    @DisplayName("Check subscription on setScript transaction")
    void subscribeTestForSetScriptTransaction() {
        setScriptTransactionSender(account, script, MIN_FEE, SetScriptTransaction.LATEST_VERSION);
        wavesAmountAfterSetAssetScript = DEFAULT_FAUCET - getFee();
        height = node().getHeight();
        subscribeResponseHandler(channel, account, height, height);

        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(getFee()),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(SetScriptTransaction.LATEST_VERSION),
                () -> assertThat(getScriptFromSetScript(0)).isEqualTo(script.bytes()),
                () -> assertThat(getTransactionId()).isEqualTo(getSetScriptTx().id().toString()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(DEFAULT_FAUCET),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(wavesAmountAfterSetAssetScript)
        );
    }
}