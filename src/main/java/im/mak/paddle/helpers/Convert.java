package im.mak.paddle.helpers;

public class Convert {
    public static byte[] convertAddressToHash(byte[] address) {
        byte[] hash = new byte[address.length - 6];
        System.arraycopy(address, 2, hash, 0, 20);
        return hash;
    }
}
