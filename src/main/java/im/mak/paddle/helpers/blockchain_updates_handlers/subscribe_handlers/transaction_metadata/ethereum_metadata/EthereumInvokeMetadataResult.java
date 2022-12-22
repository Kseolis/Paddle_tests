package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResultInvokes;

public class EthereumInvokeMetadataResult {
    public static String getEthereumInvokeMetadataResultInvokesDApp(int metadataIndex, int invokeIndex) {
        return Base58.encode(getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getDApp().toByteArray());
    }

    public static String getEthereumInvokeMetadataResultInvokesDAppCallFunc(int metadataIndex, int invokeIndex) {
        return getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getCall().getFunction();
    }

    public static String getEthereumInvokeMetadataResultInvokesPaymentAssetId(int metadataIndex, int invokeIndex, int paymentIndex) {
        return Base58.encode(getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getPayments(paymentIndex).getAssetId().toByteArray());
    }

    public static long getEthereumInvokeMetadataResultInvokesPaymentAmount(int metadataIndex, int invokeIndex, int paymentIndex) {
        return getEthereumInvokeResultInvokes(metadataIndex, invokeIndex).getPayments(paymentIndex).getAmount();
    }
}
