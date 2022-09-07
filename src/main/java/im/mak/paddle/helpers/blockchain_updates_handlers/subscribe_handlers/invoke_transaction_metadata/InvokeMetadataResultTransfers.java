package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultTransfers extends BaseInvokeMetadata {

    public static String getInvokeMetadataResultTransfersAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex)
                .getTransfers(dataIndex)
                .getAmount()
                .getAssetId()
                .toByteArray());
    }

    public static long getInvokeMetadataResultTransfersAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getTransfers(dataIndex).getAmount().getAmount();
    }

    public static byte[] getInvokeMetadataResultTransfersAddress(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex)
                .getTransfers(dataIndex)
                .getAddress()
                .toByteArray();
    }
}
/*
*         ByteBuffer buf = ByteBuffer.allocate(26);

        byte[] hash = getInvokeScriptResult(metadataIndex)
                .getTransfers(dataIndex)
                .getAddress()
                .toByteArray();

        buf.put((byte) 1).put(node().chainId()).put(hash, 0, 20);

        byte[] checksum = Hash.secureHash(buf.array());

        buf.put(checksum, 0, 4);

        return Base58.encode(buf.array());
        * */