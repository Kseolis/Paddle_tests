package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.google.protobuf.ByteString;

public class InvokeMetadataArgs extends BaseInvokeMetadata {
    public static long getInvokeMetadataArgIntegerValue(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getIntegerValue();
    }

    public static ByteString getInvokeMetadataArgBinaryValue(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getBinaryValue();
    }

    public static String getInvokeMetadataArgStringValue(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getStringValue();
    }

    public static boolean getInvokeMetadataArgBooleanValue(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getBooleanValue();
    }

    public static ByteString getInvokeMetadataArgStringValueBytes(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getStringValueBytes();
    }
}
