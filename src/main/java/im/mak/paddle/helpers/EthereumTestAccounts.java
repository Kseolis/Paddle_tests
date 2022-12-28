package im.mak.paddle.helpers;

import com.wavesplatform.transactions.account.Address;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

import static im.mak.paddle.helpers.Convert.wavesAddressFromETH;

public class EthereumTestAccounts {
    private final String senderEthPrivateKey = "0f1477865b6b251e1d400b0efc4b4d9e2fdd7c32fa8d3de3bcac3c3abfe7c07c";
    private final String ethAddressWithoutTheFirstBytes = "2A0eaaDD531CcC84Fc56Fb44645274A96583DFA7";
    private final Address senderAddress = Address.as(wavesAddressFromETH(ethAddressWithoutTheFirstBytes));
    private final ECKeyPair ecKeyPair = ECKeyPair.create(Hex.decode(senderEthPrivateKey));


    private final String transferSenderEthPrivateKey = "ca0f31f878af61e63323ec7aa6195643db74d45f87bc6e16b111028e63a2e161";
    private final String ethTransferAddressWithoutTheFirstBytes = "73f7B15eC0b85950d425b27cE5B8f6D637A4aEad";
    private final Address transferSenderAddress = Address.as(wavesAddressFromETH(ethTransferAddressWithoutTheFirstBytes));
    private final ECKeyPair transferEcKeyPair = ECKeyPair.create(Hex.decode(transferSenderEthPrivateKey));

    public EthereumTestAccounts() throws IOException {
    }

    public ECKeyPair getEcKeyPair() {
        return ecKeyPair;
    }

    public Address getSenderAddress() {
        return senderAddress;
    }

    public Address getTransferSenderAddress() {
        return transferSenderAddress;
    }

    public ECKeyPair getTransferEcKeyPair() {
        return transferEcKeyPair;
    }

}
