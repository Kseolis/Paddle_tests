package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import org.junit.jupiter.api.BeforeEach;

import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;

public class InvokeBaseTest extends BaseTest {
    private static String dAppAccountPublicKey;
    private static String dAppAccountPublicKeyHash;
    private static String dAppAccountAddress;
    private static String dAppFunctionName;

    void prepareInvoke(Account dAppAccount) {
        dAppAccountPublicKey = dAppAccount.publicKey().toString();
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = getDAppCall().getFunction().name();
    }

    public static String getDAppAccountPublicKey() {
        return dAppAccountPublicKey;
    }

    public static String getDAppAccountPublicKeyHash() {
        return dAppAccountPublicKeyHash;
    }

    public static String getDAppAccountAddress() {
        return dAppAccountAddress;
    }

    public static String getDAppFunctionName() {
        return dAppFunctionName;
    }
}
