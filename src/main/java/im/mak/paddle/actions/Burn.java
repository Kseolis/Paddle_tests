package im.mak.paddle.actions;

import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.waves.transactions.BurnTransaction;
import im.mak.waves.transactions.common.AssetId;

import java.io.IOException;

import static im.mak.paddle.Constants.EXTRA_FEE;
import static im.mak.paddle.Node.node;

public class Burn extends Action<Burn> {

    public AssetId assetId;
    public long amount;

    public Burn(Account sender) {
        super(sender, BurnTransaction.MIN_FEE);

        this.amount = 0;
    }

    public Burn assetId(AssetId assetId) {
        this.assetId = assetId;
        return this;
    }

    public Burn amount(long amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public long calcFee() {
        if (feeAmount > 0)
            return feeAmount;

        long extraFee = node().getAssetDetails(assetId).isScripted() ? EXTRA_FEE : 0;
        return super.calcFee() + extraFee;
    }

}
