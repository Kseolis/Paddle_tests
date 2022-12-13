package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getElementTransactionEthereumInvokeMetadata;

public class EthereumInvokeMetadataArgs {
    public static String getIntegerArgumentEthereumMetadata(int metadataIndex, int argIndex) {
        return String.valueOf(getElementTransactionEthereumInvokeMetadata(metadataIndex).getArguments(argIndex).getIntegerValue());
    }

    public static String getStringArgumentEthereumMetadata(int metadataIndex, int argIndex) {
        return getElementTransactionEthereumInvokeMetadata(metadataIndex).getArguments(argIndex).getStringValue();
    }

    public static String getBooleanArgumentEthereumMetadata(int metadataIndex, int argIndex) {
        return String.valueOf(getElementTransactionEthereumInvokeMetadata(metadataIndex).getArguments(argIndex).getBooleanValue());
    }

    public static String getBinaryValueBase58ArgumentEthereumMetadata(int metadataIndex, int argIndex) {
        return Base58.encode(getElementTransactionEthereumInvokeMetadata(metadataIndex).getArguments(argIndex).getBinaryValue().toByteArray());
    }

    public static String getBinaryValueBase64ArgumentEthereumMetadata(int metadataIndex, int argIndex) {
        return Base64.encode(getElementTransactionEthereumInvokeMetadata(metadataIndex).getArguments(argIndex).getBinaryValue().toByteArray());
    }
}
