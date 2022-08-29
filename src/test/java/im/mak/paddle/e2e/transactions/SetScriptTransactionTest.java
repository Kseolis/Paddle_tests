package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.SetScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SetScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SetScriptTransactionTest {
    private static Account stan;
    private static Account eric;
    private static Account kenny;
    private static Account kyle;

    @BeforeAll
    static void before() {
        async(
                () -> stan = new Account(DEFAULT_FAUCET),
                () -> eric = new Account(DEFAULT_FAUCET),
                () -> kenny = new Account(DEFAULT_FAUCET),
                () -> kyle = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("set script transaction Account STDLIB V3")
    void setLibScriptTransaction() {
        Base64String setScript = node().compileScript("{-# STDLIB_VERSION 3 #-}\n" +
                "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                "{-# CONTENT_TYPE LIBRARY #-}").script();

        SetScriptTransactionSender txSender = new SetScriptTransactionSender(stan, setScript);
        txSender.setScriptTransactionSender(0, LATEST_VERSION);
        checkSetScriptTransaction(txSender);
    }

    @Test
    @DisplayName("set script transaction dApp STDLIB V4")
    void setDAppScriptTransaction() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            Base64String setScript = node().compileScript("{-# STDLIB_VERSION 4 #-}\n" +
                    "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                    "{-# CONTENT_TYPE DAPP #-}").script();

            SetScriptTransactionSender txSender = new SetScriptTransactionSender(eric, setScript);
            txSender.setScriptTransactionSender(0, v);
            checkSetScriptTransaction(txSender);
        }
    }

    @Test
    @DisplayName("set script transaction SNDLIB V5")
    void setScriptTransactionLibV5() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            Base64String setScript = node().compileScript(SCRIPT_PERMITTING_OPERATIONS).script();

            SetScriptTransactionSender txSender = new SetScriptTransactionSender(kenny, setScript);
            txSender.setScriptTransactionSender(0, v);
            checkSetScriptTransaction(txSender);
        }
    }

    @Test
    @DisplayName("set 32kb script")
    void set32KbScript() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long minimalValSetScriptFee = 2200000;
            Base64String setScript = node().compileScript(fromFile("ride_scripts/scriptSize32kb.ride")).script();

            SetScriptTransactionSender txSender = new SetScriptTransactionSender(kyle, setScript);
            txSender.setScriptTransactionSender(minimalValSetScriptFee, v);
            checkSetScriptTransaction(txSender);
        }
    }

    private void checkSetScriptTransaction(SetScriptTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getAccount().getWavesBalance())
                        .isEqualTo(txSender.getBalanceAfterTransaction()),
                () -> assertThat(txSender.getSetScriptTx().sender()).isEqualTo(txSender.getAccount().publicKey()),
                () -> assertThat(txSender.getSetScriptTx().script()).isEqualTo(txSender.getScript()),
                () -> assertThat(txSender.getSetScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getSetScriptTx().fee().value()).isEqualTo(txSender.getFee()),
                () -> assertThat(txSender.getSetScriptTx().type()).isEqualTo(13)
        );
    }
}
