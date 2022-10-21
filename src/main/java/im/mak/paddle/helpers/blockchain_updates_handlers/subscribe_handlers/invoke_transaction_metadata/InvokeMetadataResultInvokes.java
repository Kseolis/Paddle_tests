package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Payment;
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

    public static String getStateChangesTransferAddress(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAddress()
                .toByteArray());
    }

    public static Payment getStateChangesTransfers(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex);
    }

    public static long getStateChangesTransferAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAmount().getAmount();
    }

    public static String getStateChangesTransferAssetId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getTransfers(transferIndex)
                .getAmount()
                .getAssetId()
                .toByteArray());
    }

    public static String getStateChangesBurnAssetId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getBurns(transferIndex)
                .getAssetId()
                .toByteArray());
    }

    public static long getStateChangesBurnAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getBurns(transferIndex)
                .getAmount();
    }

    public static String getStateChangesDataKey(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getData(transferIndex)
                .getKey();
    }

    public static long getStateChangesDataIntVal(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getData(transferIndex)
                .getIntValue();
    }

    public static String getStateChangesReissueAssetId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getReissues(transferIndex)
                .getAssetId()
                .toByteArray());
    }

    public static long getStateChangesReissueAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getReissues(transferIndex)
                .getAmount();
    }

    public static boolean getStateChangesReissueReissuable(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getReissues(transferIndex)
                .getIsReissuable();
    }

    public static String getStateChangesSponsorFeeAssetId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getSponsorFees(transferIndex)
                .getMinFee()
                .getAssetId()
                .toByteArray());
    }

    public static long getStateChangesSponsorFeeAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getSponsorFees(transferIndex)
                .getMinFee()
                .getAmount();
    }

    public static String getStateChangesLeasesRecipientPkHash(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getLeases(transferIndex)
                .getRecipient()
                .getPublicKeyHash()
                .toByteArray());
    }

    public static long getStateChangesLeasesAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getLeases(transferIndex)
                .getAmount();
    }

    public static String getStateChangesLeasesId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getLeases(transferIndex)
                .getLeaseId().toByteArray());
    }

    public static String getStateChangesLeaseCancelsLeasesId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getInvokeMetadataResultInvokes(metadataIndex, dataIndex)
                .getStateChanges()
                .getLeaseCancels(transferIndex)
                .getLeaseId().toByteArray());
    }

    private static Invocation getInvokeMetadataResultInvokes(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getInvokes(dataIndex);
    }
}
