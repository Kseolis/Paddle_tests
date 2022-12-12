package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.InvokeScriptMetadata;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Issue;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.getEthereumTransactionMetadata;

public class EthereumInvokeTransactionMetadata {
    public static String getEthereumInvokeDAppAddress(int metadataIndex) {
        return Base58.encode(getEthereumInvokeTransactionMetadata(metadataIndex).getDAppAddress().toByteArray());
    }

    public static String getEthereumInvokeFunctionName(int metadataIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getFunctionName();
    }

    public static String getEthereumInvokeStringArgument(int metadataIndex, int argumentIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getArguments(argumentIndex).getStringValue();
    }

    public static String getEthereumInvokeBinaryArgument(int metadataIndex, int argumentIndex) {
        return Base58.encode(getEthereumInvokeTransactionMetadata(metadataIndex).getArguments(argumentIndex).getBinaryValue().toByteArray());
    }

    public static boolean getEthereumInvokeBooleanArgument(int metadataIndex, int argumentIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getArguments(argumentIndex).getBooleanValue();
    }

    public static long getEthereumInvokeIntegerArgument(int metadataIndex, int argumentIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getArguments(argumentIndex).getIntegerValue();
    }

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

    static InvokeScriptMetadata getEthereumInvokeTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getInvoke();
    }

    static InvokeScriptResult getEthereumInvokeResult(int metadataIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getResult();
    }

    static Issue getEthereumInvokeResultIssues(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResult(metadataIndex).getIssues(issueIndex);
    }
}
