package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;

public class InvokeMetadataArgs extends BaseInvokeMetadata {
    public static String getInvokeMetadataArgIntegerValue(int metadataIndex, int argIndex) {
        return String.valueOf(getInvokeMetadata(metadataIndex).getArguments(argIndex).getIntegerValue());
    }

    public static String getInvokeMetadataArgBinaryValueBase58(int metadataIndex, int argIndex) {
        return Base58.encode(getInvokeMetadata(metadataIndex).getArguments(argIndex).getBinaryValue().toByteArray());
    }

    public static String getInvokeMetadataArgBinaryValueBase64(int metadataIndex, int argIndex) {
        return Base64.encode(getInvokeMetadata(metadataIndex).getArguments(argIndex).getBinaryValue().toByteArray());
    }

    public static String getInvokeMetadataArgStringValue(int metadataIndex, int argIndex) {
        return getInvokeMetadata(metadataIndex).getArguments(argIndex).getStringValue();
    }

    public static String getInvokeMetadataArgBooleanValue(int metadataIndex, int argIndex) {
        return String.valueOf(getInvokeMetadata(metadataIndex).getArguments(argIndex).getBooleanValue());
    }

    public static String getInvokeMetadataArgStringValueBytes(int metadataIndex, int argIndex) {
        return String.valueOf(getInvokeMetadata(metadataIndex).getArguments(argIndex).getStringValueBytes());
    }
}
