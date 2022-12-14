package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getElementTransactionEthereumInvokeMetadata;

public class EthereumInvokeMetadataPayment {
    public static long getEthereumInvokeMetadataPaymentsAmount(int metadataIndex, int paymentIndex) {
        return getElementTransactionEthereumInvokeMetadata(metadataIndex).getPayments(paymentIndex).getAmount();
    }

    public static String getEthereumInvokeMetadataPaymentsAssetId(int metadataIndex, int paymentIndex) {
        return Base58.encode(getElementTransactionEthereumInvokeMetadata(metadataIndex).getPayments(paymentIndex).getAssetId().toByteArray());
    }
}
