package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResult;

public class EthereumInvokeMetadataResultData {
    public static String getEthereumInvokeMetadataResultDataKey(int metadataIndex, int dataIndex) {
        return getEthereumInvokeResult(metadataIndex).getData(dataIndex).getKey();
    }

    public static String getEthereumInvokeMetadataResultDataIntegerValue(int metadataIndex, int dataIndex) {
        return String.valueOf(getEthereumInvokeResult(metadataIndex).getData(dataIndex).getIntValue());
    }

    public static String getEthereumInvokeMetadataResultDataStringValue(int metadataIndex, int dataIndex) {
        return getEthereumInvokeResult(metadataIndex).getData(dataIndex).getStringValue();
    }

    public static String getEthereumInvokeMetadataResultDataBoolValue(int metadataIndex, int dataIndex) {
        return String.valueOf(getEthereumInvokeResult(metadataIndex).getData(dataIndex).getBoolValue());
    }

    public static String getEthereumInvokeMetadataResultDataBinaryBase58Value(int metadataIndex, int dataIndex) {
        return Base58.encode(getEthereumInvokeResult(metadataIndex).getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static String getEthereumInvokeMetadataResultDataBinaryBase64Value(int metadataIndex, int dataIndex) {
        return Base64.encode(getEthereumInvokeResult(metadataIndex).getData(dataIndex).getBinaryValue().toByteArray());
    }

    public static ByteString getEthereumInvokeMetadataResultDataStringValueBytes(int metadataIndex, int dataIndex) {
        return getEthereumInvokeResult(metadataIndex).getData(dataIndex).getStringValueBytes();
    }
}
