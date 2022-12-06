package im.mak.paddle.helpers;

import com.wavesplatform.transactions.account.Address;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

import static im.mak.paddle.helpers.Convert.wavesAddressFromETH;

public class EthereumTestUser {
    private static EthereumTestUser instance;
    private final String senderEthPrivateKey = "0f1477865b6b251e1d400b0efc4b4d9e2fdd7c32fa8d3de3bcac3c3abfe7c07c";

    private final String ethAddressWithoutTheFirstBytes = "2A0eaaDD531CcC84Fc56Fb44645274A96583DFA7";
    private final Address senderAddress = Address.as(wavesAddressFromETH(ethAddressWithoutTheFirstBytes));
    private final ECKeyPair ecKeyPair = ECKeyPair.create(Hex.decode(senderEthPrivateKey));

    private EthereumTestUser() throws IOException {}

    public static EthereumTestUser getEthInstance() throws IOException {
        if (instance == null) {
            instance = new EthereumTestUser();
        }
        return instance;
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public Address getSenderAddress() {
        return senderAddress;
    }
}
