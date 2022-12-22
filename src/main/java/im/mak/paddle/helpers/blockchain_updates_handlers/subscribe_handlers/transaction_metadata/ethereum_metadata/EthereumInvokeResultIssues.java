package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumInvokeTransactionMetadata.getEthereumInvokeResultIssues;

public class EthereumInvokeResultIssues {

    public static String getEthereumInvokeIssuesAssetId(int metadataIndex, int issueIndex) {
        return Base58.encode(getEthereumInvokeResultIssues(metadataIndex, issueIndex).getAssetId().toByteArray());
    }

    public static String getEthereumInvokeIssuesName(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getName();
    }

    public static String getEthereumInvokeIssuesDescription(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getDescription();
    }

    public static long getEthereumInvokeIssuesAmount(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getAmount();
    }

    public static long getEthereumInvokeIssuesDecimals(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getDecimals();
    }

    public static boolean getEthereumInvokeIssuesReissuable(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getReissuable();
    }

    public static long getEthereumInvokeIssuesNonce(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResultIssues(metadataIndex, issueIndex).getNonce();
    }
}
