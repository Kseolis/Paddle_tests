package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import org.junit.jupiter.api.BeforeAll;

import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;

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

}
