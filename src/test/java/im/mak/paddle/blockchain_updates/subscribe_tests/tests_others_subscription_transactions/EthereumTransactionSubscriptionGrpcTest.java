package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.Hash;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.account.PrivateKey;
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
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class EthereumTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account sender;
    private Address senderAddress;
    private PrivateKey senderPrivateKey;

    private final String ethAddress = "0x8c7D940a36408D378B4A6df4ce041983af78110d";
    private final String ethAddressWithoutTheFirstBytes = "8c7D940a36408D378B4A6df4ce041983af78110d";


    private ECKeyPair ecKeyPair;

    private Account recipient;
    private Address recipientAddress;
    private Amount amountTransfer;
    private BigInteger gasPrice;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    sender = new Account(DEFAULT_FAUCET);
                    senderAddress = sender.address();
                    senderPrivateKey = sender.privateKey();
                    ecKeyPair = ECKeyPair.create(senderPrivateKey.bytes());
                },
                () -> {
                    try {
                        recipientAddress = Address.as(getWavesAddressFromETH(ethAddressWithoutTheFirstBytes));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    node().faucet().transfer(recipientAddress, DEFAULT_FAUCET, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> amountTransfer = Amount.of(1),
                () -> gasPrice = new BigInteger(String.valueOf(10_000_000_000L))
        );
    }

    @Test
    @DisplayName("Check subscription on transfer transaction")
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

        node().broadcastEthTransaction(ethTxId);

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, 1, height, ethTxId.toString());
        System.out.println(getAppend());
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
