package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.blockchain_updates.GrpcTransactionsCheckers;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.MIN_FEE;

public class GetBlockUpdateTest extends BaseGetBlockUpdateTest {
    @Test
    void getBlockUpdateAliasTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, aliasTxId.toString());
        GrpcTransactionsCheckers grpcTransactionsCheckers = new GrpcTransactionsCheckers(
                getBlockUpdateHandler.getTxIndex(),
                senderAddress.toString(),
                senderPublicKey.toString(),
                aliasTxId.toString()
        );
        grpcTransactionsCheckers.checkAliasGrpc(newAlias, amountBeforeAliasTx, amountAfterAliasTx, MIN_FEE);
    }
}
