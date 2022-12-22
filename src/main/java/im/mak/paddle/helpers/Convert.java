package im.mak.paddle.helpers;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.Hash;
import com.wavesplatform.crypto.base.Base58;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static im.mak.paddle.Node.node;

public class Convert {
    public static byte[] convertAddressToHash(byte[] address) {
        byte[] hash = new byte[address.length - 6];
        System.arraycopy(address, 2, hash, 0, 20);
        return hash;
    }

    public static String wavesAddressFromETH(String wavesAddressFormat) throws IOException {
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
