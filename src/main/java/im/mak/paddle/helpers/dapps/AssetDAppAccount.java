package im.mak.paddle.helpers.dapps;

import com.wavesplatform.transactions.invocation.BinaryArg;
import com.wavesplatform.transactions.invocation.Function;
import im.mak.paddle.dapp.DApp;
import im.mak.paddle.dapp.DAppCall;

public class AssetDAppAccount extends DApp {

    public AssetDAppAccount(long initialBalance, String script) {
        super(initialBalance, script);
    }

    public DAppCall setDataAssetId(byte[] arg) {
        return new DAppCall(address(), Function.as("setData", BinaryArg.as(arg)));
    }

    public DAppCall setDataAssetId() {
        return new DAppCall(address(), Function.as("setData", Function.asDefault().args()));
    }

    public DAppCall setDataAssetAndAddress(byte[] asset, byte[] address) {
        return new DAppCall(address(), Function.as(
                "setData",
                BinaryArg.as(asset), BinaryArg.as(address))
        );
    }
}
