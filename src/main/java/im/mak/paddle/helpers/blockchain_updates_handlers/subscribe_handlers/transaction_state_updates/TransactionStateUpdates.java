package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates;

import com.wavesplatform.events.protobuf.Events.StateUpdate;

import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;

public class TransactionStateUpdates {
    public static StateUpdate getTransactionStateUpdate(int index) {
        return getAppend().getTransactionStateUpdates(index);
    }
}
