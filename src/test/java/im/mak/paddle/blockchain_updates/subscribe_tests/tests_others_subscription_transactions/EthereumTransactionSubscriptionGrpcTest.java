package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.Hash;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.ECKeyPair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class EthereumTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Address senderAddress;
    private final String senderEthAddress = "0x2A0eaaDD531CcC84Fc56Fb44645274A96583DFA7";
    private final String senderEthPrivateKey = "0f1477865b6b251e1d400b0efc4b4d9e2fdd7c32fa8d3de3bcac3c3abfe7c07c";
    private final String ethAddressWithoutTheFirstBytes = "2A0eaaDD531CcC84Fc56Fb44645274A96583DFA7";
    private ECKeyPair ecKeyPair;

    private Account recipient;
    private Address recipientAddress;
    private Amount amountTransfer;
    private BigInteger gasPrice;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    try {
                        senderAddress = Address.as(getWavesAddressFromETH(ethAddressWithoutTheFirstBytes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                    ecKeyPair = ECKeyPair.create(Hex.decode(senderEthPrivateKey));
                },
                () -> {
                    recipient = new Account();
                    recipientAddress = recipient.address();
                },
                () -> amountTransfer = Amount.of(1),
                () -> gasPrice = new BigInteger(String.valueOf(10_000_000_000L))
        );
    }

    @Test
    @DisplayName("Check subscription on Ethereum transfer transaction")
    void subscribeTestForTransferTransaction() throws NodeException, IOException {
        EthereumTransaction ethTxId = EthereumTransaction.transfer(
                recipientAddress,
                amountTransfer,
                gasPrice,
                node().chainId(),
                MIN_FEE,
                System.currentTimeMillis(),
                ecKeyPair
        );
        System.out.println(ethTxId);
        node().broadcastEthTransaction(ethTxId);
        node().waitForTransaction(ethTxId.id());
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, 1, height, ethTxId.toString());
    }

    private static String getWavesAddressFromETH(String wavesAddressFormat) throws IOException {
        byte[] ethAddressBytes = Hex.decode(wavesAddressFormat);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write((byte) 1);
        outputStream.write(node().chainId());
        outputStream.write(ethAddressBytes);

        byte[] checkSumPrefix = Bytes.chunk(Hash.secureHash(outputStream.toByteArray()), 4)[0];
        outputStream.write(checkSumPrefix);

        byte[] address = outputStream.toByteArray();

        return Base58.encode(address);
    }
}
