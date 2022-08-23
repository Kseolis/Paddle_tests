package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Invocation;

public class InvokeMetadataResultInvokes extends BaseInvokeMetadata {
    public static String getInvokeMetadataResultInvokesDApp(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getDApp()
                .toByteArray());
    }

    public static String getInvokeMetadataResultInvokesCallFunc(int metadataIndex, int dataIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getCall()
                .getFunction();
    }

    public static String getInvokeMetadataResultInvokesPaymentAssetId(int metadataIndex, int dataIndex, int payIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getPayments(payIndex).getAssetId().toByteArray());
    }

    public static long getInvokeMetadataResultInvokesPaymentAmount(int metadataIndex, int dataIndex, int payIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getPayments(payIndex)
                .getAmount();
    }

    public static byte[] getInvokeMetadataResultInvokesStateChangesTransferAddress
            (int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAddress()
                .toByteArray();
    }

    public static long getInvokeMetadataResultInvokesStateChangesTransferAmount
            (int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAmount().getAmount();
    }

    public static String getInvokeMetadataResultInvokesStateChangesTransferAssetId
            (int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAmount()
                .getAssetId()
                .toByteArray());
    }

    private static Invocation getInvokeMetadataResultInvokes(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex)
                .getInvokes(dataIndex);
    }
}
