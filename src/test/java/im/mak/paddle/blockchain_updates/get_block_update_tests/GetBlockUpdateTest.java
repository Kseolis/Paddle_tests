package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.helpers.blockchain_updates_handlers.get_block_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.Test;

public class GetBlockUpdateTest extends BaseGetBlockUpdateTest {
    @Test
    void getBlockUpdateBaseTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, sender, height, aliasTxId.toString());
    }
}
