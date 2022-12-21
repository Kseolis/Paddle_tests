package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.ethereum_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass.InvokeScriptResult;

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

    public static String getEthereumNestedStateChangesTransferAddress(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getEthereumNestedStateChange(metadataIndex, dataIndex).getTransfers(transferIndex).getAddress().toByteArray());
    }

    public static long getEthereumNestedStateChangesTransferAmount(int metadataIndex, int dataIndex, int transferIndex) {
        return getEthereumNestedStateChange(metadataIndex, dataIndex).getTransfers(transferIndex).getAmount().getAmount();
    }

    public static String getEthereumNestedStateChangesTransferAssetId(int metadataIndex, int dataIndex, int transferIndex) {
        return Base58.encode(getEthereumNestedStateChange(metadataIndex, dataIndex)
                .getTransfers(transferIndex)
                .getAmount()
                .getAssetId()
                .toByteArray()
        );
    }

    public static String getEthereumNestedStateChangesInvokesDApp(int metadataIndex, int dataIndex, int nestedInvokesIndex) {
        return Base58.encode(getEthereumNestedStateChange(metadataIndex, dataIndex).getInvokes(nestedInvokesIndex).getDApp().toByteArray());
    }

    public static String getEthereumNestedStateChangesTransferInvokesCallFunction(int metadataIndex, int dataIndex, int nestedInvokesIndex) {
        return getEthereumNestedStateChange(metadataIndex, dataIndex).getInvokes(nestedInvokesIndex).getCall().getFunction();
    }

    public static String getEthereumNestedStateChangesTransferInvokesCallBinArg(int metadataIndex, int dataIndex, int nestedInvokesIndex, int args) {
        return Base58.encode(getEthereumNestedStateChange(metadataIndex, dataIndex)
                .getInvokes(nestedInvokesIndex)
                .getCall()
                .getArgs(args)
                .getBinaryValue()
                .toByteArray()
        );
    }

    public static String getEthereumNestedStateChangesTransferInvokesCallIntArg(int metadataIndex, int dataIndex, int nestedInvokesIndex, int args) {
        return String.valueOf(getEthereumNestedStateChange(metadataIndex, dataIndex)
                .getInvokes(nestedInvokesIndex)
                .getCall()
                .getArgs(args)
                .getIntegerValue()
        );
    }

    public static String getEthereumNestedStateChangesTransferInvokesCallStringArg(int metadataIndex, int dataIndex, int nestedInvokesIndex, int args) {
        return getEthereumNestedStateChange(metadataIndex, dataIndex)
                .getInvokes(nestedInvokesIndex)
                .getCall()
                .getArgs(args)
                .getStringValue();
    }

    public static String getEthereumNestedStateChangesTransferInvokesCallBooleanArg(int metadataIndex, int dataIndex, int nestedInvokesIndex, int args) {
        return String.valueOf(getEthereumNestedStateChange(metadataIndex, dataIndex)
                .getInvokes(nestedInvokesIndex)
                .getCall()
                .getArgs(args)
                .getBooleanValue()
        );
    }

    public static String getEthereumDoubleNestedStateChangesTransferAssetId(int metadataIndex, int dataIndex, int invokesIndex, int transferIndex) {
        return Base58.encode(getEthereumDoubleNestedStateChange(metadataIndex, dataIndex, invokesIndex)
                .getTransfers(transferIndex)
                .getAmount()
                .getAssetId()
                .toByteArray()
        );
    }

    public static long getEthereumDoubleNestedStateChangesTransferAmount(int metadataIndex, int dataIndex, int invokesIndex, int transferIndex) {
        return getEthereumDoubleNestedStateChange(metadataIndex, dataIndex, invokesIndex).getTransfers(transferIndex).getAmount().getAmount();
    }

    public static String getEthereumDoubleNestedStateChangesTransferAddress(int metadataIndex, int dataIndex, int invokesIndex, int transferIndex) {
        return Base58.encode(getEthereumDoubleNestedStateChange(metadataIndex, dataIndex, invokesIndex)
                .getTransfers(transferIndex)
                .getAddress()
                .toByteArray()
        );
    }

    private static InvokeScriptResult getEthereumNestedStateChange(int metadataIndex, int dataIndex) {
        return getEthereumInvokeResultInvokes(metadataIndex, dataIndex).getStateChanges();
    }

    private static InvokeScriptResult getEthereumDoubleNestedStateChange(int metadataIndex, int dataIndex, int invokesIndex) {
        return getEthereumNestedStateChange(metadataIndex, dataIndex).getInvokes(invokesIndex).getStateChanges();
    }
}
