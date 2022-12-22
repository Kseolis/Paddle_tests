package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.events.protobuf.Events;
import com.wavesplatform.protobuf.block.BlockOuterClass;

public class BlockInfo {
    public static BlockOuterClass.Block blockInfo;
    public static BlockOuterClass.MicroBlock microBlockInfo;
    public static void setBlockInfo(Events.BlockchainUpdated.Append append) {
        blockInfo = append.getBlock().getBlock();
        if (blockInfo.toString().isBlank()) {
            blockInfo = null;
        }
        microBlockInfo = append.getMicroBlock().getMicroBlock().getMicroBlock();
    }
}
