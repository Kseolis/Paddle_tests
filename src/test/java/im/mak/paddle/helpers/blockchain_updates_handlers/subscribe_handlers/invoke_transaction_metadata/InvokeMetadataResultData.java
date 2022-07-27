package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.google.protobuf.ByteString;

public class InvokeMetadataResultData extends BaseInvokeMetadata {
    public static String getInvokeMetadataResultDataKey(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getKey();
    }

    public static long getInvokeMetadataResultDataIntegerValue(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getIntValue();
    }

    public static String getInvokeMetadataResultDataStringValue(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getStringValue();
    }

    public static boolean getInvokeMetadataResultDataBoolValue(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getBoolValue();
    }
    public static ByteString getInvokeMetadataResultDataBinaryValue(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getBinaryValue();
    }

    public static ByteString getInvokeMetadataResultDataStringValueBytes(int metadataIndex, int dataIndex) {
        return getInvokeMetadata(metadataIndex).getResult().getData(dataIndex).getStringValueBytes();
    }
}
