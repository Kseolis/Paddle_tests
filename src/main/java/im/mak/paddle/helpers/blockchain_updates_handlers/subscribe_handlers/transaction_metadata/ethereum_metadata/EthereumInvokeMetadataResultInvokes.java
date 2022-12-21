package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResultInvokes;

public class EthereumInvokeMetadataResultInvokes {
    public static String getEthereumInvokeMetadataResultInvokesCallBinArgs(int metadataIndex, int invokeIndex, int argIndex) {
        return Base58.encode(getEthereumInvokeResultInvokes(metadataIndex, invokeIndex)
                .getCall()
                .getArgs(argIndex)
                .getBinaryValue()
                .toByteArray()
        );
    }

    public static String getEthereumInvokeMetadataResultInvokesCallIntArgs(int metadataIndex, int invokeIndex, int argIndex) {
        return String.valueOf(getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getCall().getArgs(argIndex).getIntegerValue());
    }

    public static String getEthereumInvokeMetadataResultInvokesCallStringArgs(int metadataIndex, int invokeIndex, int argIndex) {
        return getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getCall().getArgs(argIndex).getStringValue();
    }

    public static String getEthereumInvokeMetadataResultInvokesCallBoolArgs(int metadataIndex, int invokeIndex, int argIndex) {
        return String.valueOf(getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getCall().getArgs(argIndex).getBooleanValue());
    }
}
