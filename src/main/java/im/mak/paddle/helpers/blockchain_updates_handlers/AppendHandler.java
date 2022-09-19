package im.mak.paddle.helpers.blockchain_updates_handlers;

import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;

public class AppendHandler {
    private static Append append;

    public static Append getAppend() {
        return append;
    }

    public static void setAppend(Append append) {
        AppendHandler.append = append;
    }
}
