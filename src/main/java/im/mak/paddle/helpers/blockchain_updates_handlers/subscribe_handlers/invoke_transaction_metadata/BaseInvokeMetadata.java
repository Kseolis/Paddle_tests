package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events;
import com.wavesplatform.protobuf.transaction.InvokeScriptResultOuterClass;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.TransactionMetadataHandler.getElementTransactionMetadata;

public class BaseInvokeMetadata {

    public static Events.TransactionMetadata.InvokeScriptMetadata getInvokeMetadata(int metadataIndex) {
        return getElementTransactionMetadata(metadataIndex).getInvokeScript();
    }

    public static String getInvokeMetadataDAppAddress(int metadataIndex) {
        return Base58.encode(getInvokeMetadata(metadataIndex).getDAppAddress().toByteArray());
    }

    public static String getInvokeMetadataFunctionName(int metadataIndex) {
        return getInvokeMetadata(metadataIndex).getFunctionName();
    }

    public static InvokeScriptResultOuterClass.InvokeScriptResult getInvokeScriptResult(int metadataIndex) {
        return getInvokeMetadata(metadataIndex)
                .getResult();
    }
}
