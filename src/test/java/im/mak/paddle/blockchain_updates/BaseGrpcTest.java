package im.mak.paddle.blockchain_updates;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static im.mak.paddle.Node.getDockerGRPCPort;

@Execution(ExecutionMode.SAME_THREAD)
public class BaseGrpcTest {
    protected int height;
    protected int fromHeight;
    protected int toHeight;
    public static final long CHAIN_ID = 82;
    private static String dAppAccountPublicKeyHash;
    private static String dAppAccountAddress;
    private static String dAppFunctionName;

    protected final Channel CHANNEL = ManagedChannelBuilder
            .forAddress("0.0.0.0", getDockerGRPCPort())
            .usePlaintext()
            .build();

    public void prepareInvoke(Account dAppAccount, PrepareInvokeTestsData testData) {
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = testData.getDAppCall().getFunction().name();
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
