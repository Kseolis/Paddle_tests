package im.mak.paddle.helpers.dapps;

import im.mak.paddle.dapp.DApp;
import im.mak.paddle.util.ScriptUtil;

import static im.mak.paddle.util.ScriptUtil.fromFile;

public class DefaultDApp420Complexity extends DApp {
    public static final String INITIAL_SCRIPT = ScriptUtil.fromFile("ride_scripts/scriptWith420Complexity.ride");

    public DefaultDApp420Complexity(long initialBalance) {
        super(initialBalance, INITIAL_SCRIPT);
    }
}
