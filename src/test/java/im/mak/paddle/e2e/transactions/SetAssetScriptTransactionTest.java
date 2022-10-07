package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.SetAssetScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SetAssetScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SetAssetScriptTransactionTest {
    private static Account sender;
    private static AssetId issuedAssetId;

    @BeforeAll
    static void before() {
        sender = new Account(DEFAULT_FAUCET);
        issuedAssetId = sender.issue(i -> i.name("Test_Asset")
                .script(SCRIPT_PERMITTING_OPERATIONS)
                .quantity(1000_00000000L)).tx().assetId();
    }

    @Test
    @DisplayName("set asset script 'ban on updating key values'")
    void setAssetScriptTransactionTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            Base64String script = node()
                    .compileScript(fromFile("ride_scripts/permissionOnUpdatingKeyValues.ride")).script();

            SetAssetScriptTransactionSender txSender = new SetAssetScriptTransactionSender(sender, script, issuedAssetId);
            txSender.setAssetScriptSender(v);

            checkSetAssetScriptTransaction(v, txSender);
        }
    }

    private void checkSetAssetScriptTransaction(int version, SetAssetScriptTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSetAssetScriptTx().fee().value()).isEqualTo(ONE_WAVES),
                () -> assertThat(txSender.getSetAssetScriptTx().sender()).isEqualTo(txSender.getAccount().publicKey()),
                () -> assertThat(txSender.getSetAssetScriptTx().script()).isEqualTo(txSender.getScript()),
                () -> assertThat(txSender.getSetAssetScriptTx().assetId()).isEqualTo(txSender.getAssetId()),
                () -> assertThat(txSender.getSetAssetScriptTx().version()).isEqualTo(version),
                () -> assertThat(txSender.getSetAssetScriptTx().type()).isEqualTo(15),
                () -> assertThat(txSender.getAccount().getWavesBalance()).isEqualTo(txSender.getBalanceAfterTransaction())
        );
    }
}
