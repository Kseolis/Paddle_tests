package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.blockchain_updates.transactions_checkers.*;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.*;

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

    @Test
    @DisplayName("Check getBlockUpdate response for Burn transaction")
    void getBlockUpdateBurnTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, burnTxId.toString());
        GrpcBurnCheckers grpcTransferCheckers =
                new GrpcBurnCheckers(getBlockUpdateHandler.getTxIndex(), sender, burnTxSender, issueTx);
        grpcTransferCheckers.checkBurnSubscribe(assetAmountBeforeBurnTx, assetAmountAfterBurnTx, assetAmountBeforeBurnTx);
    }

    @Test
    @DisplayName("Check getBlockUpdate response for Reissue transaction")
    void getBlockUpdateReissueTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, reissueTxId.toString());
        GrpcReissueCheckers grpcReissueCheckers =
                new GrpcReissueCheckers(getBlockUpdateHandler.getTxIndex(), sender, reissueTxSender, issueTx);
        grpcReissueCheckers.checkReissueSubscribe(
                assetAmountBeforeReissueTx,
                assetAmountAfterReissueTx,
                assetAmountAfterReissueTx,
                issueTx.quantity()
        );
    }

    @Test
    @DisplayName("Check getBlockUpdate response for Exchange transaction")
    void getBlockUpdateExchangeTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, exchangeTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcExchangeCheckers grpcReissueCheckers = new GrpcExchangeCheckers(index, buyer, recipient, exchangeTx);
        grpcReissueCheckers.checkExchangeSubscribe(MIN_FEE_FOR_EXCHANGE, "");
        grpcReissueCheckers.checkBalancesForExchangeWithWaves(amountBeforeExchangeTx, assetIdExchangeQuantity);
    }
}
