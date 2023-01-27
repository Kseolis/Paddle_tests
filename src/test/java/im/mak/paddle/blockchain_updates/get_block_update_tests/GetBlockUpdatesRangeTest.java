package im.mak.paddle.blockchain_updates.get_block_update_tests;


import im.mak.paddle.blockchain_updates.transactions_checkers.*;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdatesRangeHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.MIN_FEE;

class GetBlockUpdatesRangeTest extends BaseGetBlockUpdateTest {
    @Test
    @DisplayName("Check getBlockUpdateRange response for Issue transaction")
    void getBlockUpdateRangeIssueTransactionTest() {
        heightCalculation(heightsList);
        String address = senderAddress.toString();
        String pk = senderPublicKey.toString();
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, issueTxId.toString());
        int index = handler.getTxIndex();
        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(index, address, pk, issueTx);
        getIssueCheckers.checkIssueGrpc(amountBeforeIssueTx, amountAfterIssueTx);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Transfer transaction")
    void getBlockUpdateRangeTransferTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, transferTxId.toString());
        int index = handler.getTxIndex();
        GrpcTransferCheckers grpcTransferCheckers = new GrpcTransferCheckers(index, sender, recipient, transferSender);
        grpcTransferCheckers.checkTransferSubscribe(
                wavesBalanceBeforeTransfer,
                transferSender.getSenderWavesBalanceAfterTransaction(),
                assetBalanceBeforeTransfer,
                transferSender.getSenderBalanceAfterTransaction()
        );
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Burn transaction")
    void getBlockUpdateRangeBurnTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, burnTxId.toString());
        int index = handler.getTxIndex();
        GrpcBurnCheckers grpcTransferCheckers = new GrpcBurnCheckers(index, sender, burnTxSender, issueTx);
        grpcTransferCheckers.checkBurnSubscribe(assetAmountBeforeBurnTx, assetAmountAfterBurnTx, assetAmountBeforeBurnTx);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Reissue transaction")
    void getBlockUpdateRangeReissueTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, reissueTxId.toString());
        int index = handler.getTxIndex();
        GrpcReissueCheckers grpcReissueCheckers = new GrpcReissueCheckers(index, sender, reissueTxSender, issueTx);
        grpcReissueCheckers.checkReissueSubscribe(assetAmountBeforeReissueTx, assetAmountAfterReissueTx, assetAmountAfterReissueTx, issueTx.quantity());
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Exchange transaction")
    void getBlockUpdateRangeExchangeTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, exchangeTxId.toString());
        int index = handler.getTxIndex();
        GrpcExchangeCheckers grpcReissueCheckers = new GrpcExchangeCheckers(index, buyer, recipient, exchangeTx);
        grpcReissueCheckers.checkExchangeSubscribe(MIN_FEE_FOR_EXCHANGE, "");
        grpcReissueCheckers.checkBalancesForExchangeWithWaves(amountBeforeExchangeTx, assetIdExchangeQuantity);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Lease transaction")
    void getBlockUpdateRangeLeaseTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, leaseTxId.toString());
        int index = handler.getTxIndex();
        GrpcLeaseCheckers grpcLeaseCheckers = new GrpcLeaseCheckers(index, sender, recipient, leaseTx);
        grpcLeaseCheckers.checkLeaseGrpc(MIN_FEE, leaseTx.getAmountBefore(), leaseTx.getAmountAfter());
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for LeaseCancel transaction")
    void getBlockUpdateRangeLeaseCancelTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, leaseCancelTxId.toString());
        int index = handler.getTxIndex();
        GrpcLeaseCancelCheckers leaseCancelCheckers = new GrpcLeaseCancelCheckers(index, sender, recipient, leaseCancelTx);
        leaseCancelCheckers.checkLeaseCancelGrpc(MIN_TRANSACTION_SUM);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Alias transaction")
    void getBlockUpdateRangeAliasTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, aliasTxId.toString());
        GrpcAliasCheckers grpcAliasCheckers = new GrpcAliasCheckers(
                handler.getTxIndex(),
                senderAddress.toString(),
                senderPublicKey.toString(),
                aliasTxId.toString()
        );
        grpcAliasCheckers.checkAliasGrpc(newAlias, amountBeforeAliasTx, amountAfterAliasTx, MIN_FEE);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for MassTransfer transaction")
    void getBlockUpdateRangeMassTransferTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, massTransferTxId.toString());
        int index = handler.getTxIndex();
        GrpcMassTransferCheckers massTransferCheckers = new GrpcMassTransferCheckers(index, sender, massTransferTx);
        massTransferCheckers.checkMassTransferGrpc(assetAmount.value(), balanceBeforeMassTx);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Data transaction")
    void getBlockUpdateRangeDataTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, dataTxId);
        int index = handler.getTxIndex();
        GrpcDataCheckers dataCheckers = new GrpcDataCheckers(index, sender, dataTxSender);
        dataCheckers.checkDataTransactionGrpc();
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for SetScript transaction")
    void getBlockUpdateRangeSetScriptTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, setScriptTxId);
        int index = handler.getTxIndex();
        GrpcSetScriptCheckers setScriptCheckers = new GrpcSetScriptCheckers(index, setScriptTx);
        setScriptCheckers.checkSetScriptGrpc();
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for SponsorFee transaction")
    void getBlockUpdateRangeSponsorFeeTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, sponsorFeeTxId);
        int index = handler.getTxIndex();
        GrpcSponsorFeeCheckers sponsorFeeCheckers = new GrpcSponsorFeeCheckers(index, sponsorFeeTx, sponsorFeeIssueAsset);
        sponsorFeeCheckers.checkSponsorFeeGrpc();
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for SetAssetScript transaction")
    void getBlockUpdateRangeSetAssetScriptTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, setAssetScriptTxId);
        int index = handler.getTxIndex();
        GrpcSetAssetScriptCheckers assetScriptCheckers = new GrpcSetAssetScriptCheckers(index, setAssetScriptTx, issueTx);
        assetScriptCheckers.checkSetAssetGrpc(0, 0);
    }

    @Test
    @DisplayName("Checking for an getBlockUpdateRange response for an update asset info transaction")
    void getBlockUpdateRangeForUpdateAssetInfoTransactionTest() {
        heightCalculation(heightsList);
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, updateAssetInfoTxId);
        int index = handler.getTxIndex();
        GrpcUpdateAssetInfoCheckers updateAssetInfoCheckers = new GrpcUpdateAssetInfoCheckers(updateAssetInfoTx, issueTx);
        updateAssetInfoCheckers.checkUpdateAssetInfo(index, wavesBalanceBeforeUpdateAssetInfoTx, wavesBalanceAfterUpdateAssetInfoTx);
    }

    @Test
    @DisplayName("Check getBlockUpdateRange response for Ethereum transaction")
    void getBlockUpdateEthereumTransactionTest() {
        GetBlockUpdatesRangeHandler handler = new GetBlockUpdatesRangeHandler();
        handler.getBlockUpdateRangeResponseHandler(CHANNEL, fromHeight, toHeight, ethTxId);
        int index = handler.getTxIndex();
        GrpcEthereumTransferCheckers ethereumTransferCheckers = new GrpcEthereumTransferCheckers(index, ethTx, wavesAmount);
        ethereumTransferCheckers.checkEthereumTransfer();
    }
}