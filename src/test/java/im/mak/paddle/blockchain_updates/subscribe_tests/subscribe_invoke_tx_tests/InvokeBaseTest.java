package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import org.junit.jupiter.api.BeforeAll;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getCallerAccount;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getDAppCall;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptTx;
import static im.mak.paddle.util.Constants.DEVNET_CHAIN_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeBaseTest extends BaseTest {
    static PrepareInvokeTestsData testsData;
    static String dAppAccountPublicKey;
    static String dAppAccountPublicKeyHash;
    static String dAppAccountAddress;
    static String dAppFunctionName;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    void prepareInvoke(Account dAppAccount) {
        dAppAccountPublicKey = dAppAccount.publicKey().toString();
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = getDAppCall().getFunction().name();
    }

    void checkInvokeSubscribe(long amount, String dAppKey, String dAppValue, long fee) {
        assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID);
        assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(getCallerAccount().publicKey().toString());
        assertThat(getTransactionFeeAmount(0)).isEqualTo(fee);
        assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION);
        assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(dAppAccountPublicKeyHash);
     //   assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
        assertThat(getInvokeTransactionPaymentAmount(0, 0)).isEqualTo(amount);
        assertThat(getTransactionId()).isEqualTo(getInvokeScriptId());
    }
}
