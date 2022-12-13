package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.TransactionMetadata.InvokeScriptMetadata;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Invocation;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult.Issue;

import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata.EthereumTransactionMetadata.getEthereumTransactionMetadata;

public class EthereumInvokeTransactionMetadata {
    public static InvokeScriptMetadata getElementTransactionEthereumInvokeMetadata(int metadataIndex) {
        return getAppend().getTransactionsMetadata(metadataIndex).getEthereum().getInvoke();
    }

    public static String getEthereumInvokeDAppAddress(int metadataIndex) {
        return Base58.encode(getEthereumInvokeTransactionMetadata(metadataIndex).getDAppAddress().toByteArray());
    }

    public static String getEthereumInvokeFunctionName(int metadataIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getFunctionName();
    }

    static InvokeScriptMetadata getEthereumInvokeTransactionMetadata(int metadataIndex) {
        return getEthereumTransactionMetadata(metadataIndex).getInvoke();
    }

    static InvokeScriptResult getEthereumInvokeResult(int metadataIndex) {
        return getEthereumInvokeTransactionMetadata(metadataIndex).getResult();
    }

    static Invocation getEthereumInvokeResultInvokes(int metadataIndex, int invokeIndex) {
        return getEthereumInvokeResult(metadataIndex).getInvokes(invokeIndex);
    }

    static Issue getEthereumInvokeResultIssues(int metadataIndex, int issueIndex) {
        return getEthereumInvokeResult(metadataIndex).getIssues(issueIndex);
    }
}
