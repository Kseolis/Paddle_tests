package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcAliasCheckers;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcIssueCheckers;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcTransferCheckers;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.MIN_FEE;

public class GetBlockUpdateTest extends BaseGetBlockUpdateTest {
    @Test
    @DisplayName("Check getBlockUpdate response for Alias transaction")
    void getBlockUpdateAliasTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, aliasTxId.toString());
        GrpcAliasCheckers grpcAliasCheckers = new GrpcAliasCheckers(
                getBlockUpdateHandler.getTxIndex(),
                senderAddress.toString(),
                senderPublicKey.toString(),
                aliasTxId.toString()
        );
        grpcAliasCheckers.checkAliasGrpc(newAlias, amountBeforeAliasTx, amountAfterAliasTx, MIN_FEE);
    }

    @Test
    @DisplayName("Check getBlockUpdate response for Issue transaction")
    void getBlockUpdateIssueTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, issueTxId.toString());
        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(
                getBlockUpdateHandler.getTxIndex(),
                senderAddress.toString(),
                senderPublicKey.toString(),
                issueTx
        );
        getIssueCheckers.checkIssueGrpc(amountBeforeIssueTx, amountAfterIssueTx);
    }

    @Test
    @DisplayName("Check getBlockUpdate response for Transfer transaction")
    void getBlockUpdateTransferTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, transferTxId.toString());
        GrpcTransferCheckers grpcTransferCheckers =
                new GrpcTransferCheckers(getBlockUpdateHandler.getTxIndex(), sender, recipient, transferSender);
        grpcTransferCheckers.checkTransferSubscribe(
                wavesBalanceBeforeTransfer,
                transferSender.getSenderWavesBalanceAfterTransaction(),
                assetBalanceBeforeTransfer,
                transferSender.getSenderBalanceAfterTransaction()
        );
    }

}
