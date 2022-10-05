package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.blockchain_updates.transactions_checkers.*;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.*;

public class GetBlockUpdateTest extends BaseGetBlockUpdateTest {
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
        long fee = MIN_FEE_FOR_EXCHANGE + EXTRA_FEE;
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, exchangeTxId.toString());
        GrpcExchangeCheckers grpcReissueCheckers =
                new GrpcExchangeCheckers(getBlockUpdateHandler.getTxIndex(), sender, recipient, exchangeTx);
        grpcReissueCheckers.checkExchangeSubscribe(fee, "");
        grpcReissueCheckers.checkBalancesForExchangeWithWaves(
                exchangeTx.getWavesSellerAmountBefore(),
                exchangeTx.getWavesBuyerAmountBefore()
        );
    }

    @Test
    @DisplayName("Check getBlockUpdate response for Lease transaction")
    void getBlockUpdateLeaseTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, leaseTxId.toString());
        GrpcLeaseCheckers grpcLeaseCheckers =
                new GrpcLeaseCheckers(getBlockUpdateHandler.getTxIndex(), sender, recipient, leaseTx);
        grpcLeaseCheckers.checkLeaseGrpc(MIN_FEE, leaseTx.getAmountBefore(), leaseTx.getAmountAfter());
    }

    @Test
    @DisplayName("Check getBlockUpdate response for LeaseCancel transaction")
    void getBlockUpdateLeaseCancelTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, leaseCancelTxId.toString());
        GrpcLeaseCancelCheckers grpcLeaseCancelCheckers =
                new GrpcLeaseCancelCheckers(getBlockUpdateHandler.getTxIndex(), sender, recipient, leaseCancelTx);
        grpcLeaseCancelCheckers.checkLeaseCancelGrpc(MIN_TRANSACTION_SUM);
    }

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
}
