package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.SetAssetScriptTransaction;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.ONE_WAVES;

public class SetAssetScriptTransactionSender extends BaseTransactionSender {
    private SetAssetScriptTransaction setAssetScriptTx;
    private final Account account;
    private final Base64String script;
    private final AssetId assetId;
    private final long wavesAmountBeforeSetAssetScript;
    private final long wavesAmountAfterSetAssetScript;

    public SetAssetScriptTransactionSender(Account account, Base64String script, AssetId assetId) {
        this.account = account;
        this.script = script;
        this.assetId = assetId;
        this.wavesAmountBeforeSetAssetScript = account.getWavesBalance();
        this.wavesAmountAfterSetAssetScript = wavesAmountBeforeSetAssetScript - ONE_WAVES;
    }

    public void setAssetScriptSender(int version) {
        balanceAfterTransaction = account.getWavesBalance() - ONE_WAVES;
        setAssetScriptTx = SetAssetScriptTransaction
                .builder(assetId, script)
                .version(version)
                .getSignedWith(account.privateKey());
        node().waitForTransaction(node().broadcast(setAssetScriptTx).id());
        txInfo = node().getTransactionInfo(setAssetScriptTx.id());
    }

    public SetAssetScriptTransaction getSetAssetScriptTx() {
        return setAssetScriptTx;
    }

    public Account getAccount() {
        return account;
    }

    public Base64String getScript() {
        return script;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public long getWavesAmountBeforeSetAssetScript() {
        return wavesAmountBeforeSetAssetScript;
    }

    public long getWavesAmountAfterSetAssetScript() {
        return wavesAmountAfterSetAssetScript;
    }
}
