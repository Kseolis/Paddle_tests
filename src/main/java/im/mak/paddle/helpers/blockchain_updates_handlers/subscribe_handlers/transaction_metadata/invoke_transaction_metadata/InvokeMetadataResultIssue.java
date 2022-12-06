package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;

public class InvokeMetadataResultIssue extends BaseInvokeMetadata {

    public static String getInvokeMetadataResultIssueAssetId(int metadataIndex, int dataIndex) {
        return Base58.encode(getInvokeScriptResult(metadataIndex)
                        .getIssues(dataIndex)
                        .getAssetId()
                        .toByteArray());
    }

    public static String getInvokeMetadataResultIssueName(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getName();
    }

    public static String getInvokeMetadataResultIssueDescription(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getDescription();
    }

    public static long getInvokeMetadataResultIssueAmount(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getAmount();
    }

    public static long getInvokeMetadataResultIssueDecimals(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getDecimals();
    }

    public static boolean getInvokeMetadataResultIssueReissuable(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getReissuable();
    }

    public static long getInvokeMetadataResultIssueNonce(int metadataIndex, int dataIndex) {
        return getInvokeScriptResult(metadataIndex).getIssues(dataIndex).getNonce();
    }
}
