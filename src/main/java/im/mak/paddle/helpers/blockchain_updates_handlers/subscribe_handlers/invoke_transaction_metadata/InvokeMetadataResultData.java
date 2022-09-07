package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;

public class InvokeMetadataResultData extends BaseInvokeMetadata {
    public static String getInvokeMetadataResultDataKey(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getKey();
    }

    public static String getInvokeMetadataResultDataIntegerValue(int metadataIndex, int dataIndex) {
        return String.valueOf(getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getIntValue());
    }

    public static String getInvokeMetadataResultDataStringValue(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getStringValue();
    }

    public static String getInvokeMetadataResultDataBoolValue(int metadataIndex, int dataIndex) {
        return String.valueOf(getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getBoolValue());
    }

    public static String getInvokeMetadataResultDataBinaryBase58Value(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static String getInvokeMetadataResultDataBinaryBase64Value(int metadataIndex, int dataIndex) {
        return Base64.encode(getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static ByteString getInvokeMetadataResultDataStringValueBytes(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getStringValueBytes();
    }
}
