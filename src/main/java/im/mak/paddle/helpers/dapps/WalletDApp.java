package im.mak.paddle.helpers.dapps;

import com.wavesplatform.transactions.invocation.Function;
import com.wavesplatform.transactions.invocation.IntegerArg;
import im.mak.paddle.dapp.DApp;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.util.ScriptUtil;

public class WalletDApp extends DApp {

    public static final String INITIAL_SCRIPT = ScriptUtil.fromFile("ride_scripts/wallet.ride");

    public WalletDApp(long initialBalance) {
        super(initialBalance, INITIAL_SCRIPT);
    }

    public DAppCall deposit() {
        return new DAppCall(address(), Function.as("deposit"));
    }

    public DAppCall withdraw(long amount) {
        return new DAppCall(address(), Function.as("withdraw", IntegerArg.as(amount)));
    }

}
