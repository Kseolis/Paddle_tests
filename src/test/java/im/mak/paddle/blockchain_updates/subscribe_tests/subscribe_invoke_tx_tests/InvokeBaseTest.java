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

/* assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(SUM_FEE),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId()),
                () -> assertThat(getInvokeTransactionAmount(0, 0)).isEqualTo(amount),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(accWithDAppPublicKeyHash),


                () -> assertThat(getInvokeMetadataArgStringValue(0, 0)).isEqualTo(dAppValue),
                // check waves account balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(getAccountWavesBalance()),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(getBalanceAfterTransaction()),
                // check waves dAppAccount balance
                () -> assertThat(getAddress(0, 1)).isEqualTo(accWithDAppAddress),
                () -> assertThat(getAmountBefore(0, 1)).isEqualTo(getDAppAccountBalance()),
                () -> assertThat(getAmountAfter(0, 1)).isEqualTo(getDAppAccountBalanceAfterTransaction()),
                // data entries
                () -> assertThat(getSenderAddress(0, 0)).isEqualTo(accWithDAppAddress),
                () -> assertThat(getTxKeyForStateUpdates(0, 0)).isEqualTo(dAppKey),
                () -> assertThat(getTxStringValueForStateUpdates(0, 0)).isEqualTo(dAppValue),
                () -> assertThat(getBeforeDataEntriesKey(0, 0)).isEqualTo(dAppKey),
                () -> assertThat(getBeforeDataEntriesStringValue(0, 0)).isEqualTo(dAppBeforeValue)
        );*/