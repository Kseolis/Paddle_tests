package im.mak.paddle.blockchain_updates.get_block_update_tests;

import im.mak.paddle.blockchain_updates.transactions_checkers.*;
import im.mak.paddle.helpers.blockchain_updates_handlers.GetBlockUpdateHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static im.mak.paddle.util.Constants.*;

public class GetBlockUpdateTest extends BaseGetBlockUpdateTest {
    @Test
    @DisplayName("Checking getBlockUpdate response for Issue transaction")
    void getBlockUpdateIssueTransactionTest() {
        String address = senderAddress.toString();
        String pk = senderPublicKey.toString();
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, issueTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcIssueCheckers getIssueCheckers = new GrpcIssueCheckers(index, address, pk, issueTx);
        getIssueCheckers.checkIssueGrpc(amountBeforeIssueTx, amountAfterIssueTx);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Transfer transaction")
    void getBlockUpdateTransferTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, transferTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcTransferCheckers grpcTransferCheckers = new GrpcTransferCheckers(index, sender, recipient, transferSender);
        grpcTransferCheckers.checkTransferSubscribe(
                wavesBalanceBeforeTransfer,
                transferSender.getSenderWavesBalanceAfterTransaction(),
                assetBalanceBeforeTransfer,
                transferSender.getSenderBalanceAfterTransaction()
        );
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Burn transaction")
    void getBlockUpdateBurnTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, burnTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcBurnCheckers grpcTransferCheckers = new GrpcBurnCheckers(index, sender, burnTxSender, issueTx);
        grpcTransferCheckers.checkBurnSubscribe(assetAmountBeforeBurnTx, assetAmountAfterBurnTx, assetAmountBeforeBurnTx);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Reissue transaction")
    void getBlockUpdateReissueTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, reissueTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcReissueCheckers grpcReissueCheckers = new GrpcReissueCheckers(index, sender, reissueTxSender, issueTx);
        grpcReissueCheckers.checkReissueSubscribe(
                assetAmountBeforeReissueTx,
                assetAmountAfterReissueTx,
                assetAmountAfterReissueTx,
                issueTx.quantity()
        );
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Exchange transaction")
    void getBlockUpdateExchangeTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, exchangeTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcExchangeCheckers grpcReissueCheckers = new GrpcExchangeCheckers(index, buyer, recipient, exchangeTx);
        grpcReissueCheckers.checkExchangeSubscribe(MIN_FEE_FOR_EXCHANGE, "");
        grpcReissueCheckers.checkBalancesForExchangeWithWaves(amountBeforeExchangeTx, assetIdExchangeQuantity);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Lease transaction")
    void getBlockUpdateLeaseTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, leaseTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcLeaseCheckers grpcLeaseCheckers = new GrpcLeaseCheckers(index, sender, recipient, leaseTx);
        grpcLeaseCheckers.checkLeaseGrpc(MIN_FEE, leaseTx.getAmountBefore(), leaseTx.getAmountAfter());
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for LeaseCancel transaction")
    void getBlockUpdateLeaseCancelTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, leaseCancelTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcLeaseCancelCheckers leaseCancelCheckers = new GrpcLeaseCancelCheckers(index, sender, recipient, leaseCancelTx);
        leaseCancelCheckers.checkLeaseCancelGrpc(MIN_TRANSACTION_SUM);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Alias transaction")
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
    @DisplayName("Checking getBlockUpdate response for MassTransfer transaction")
    void getBlockUpdateMassTransferTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, massTransferTxId.toString());
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcMassTransferCheckers massTransferCheckers = new GrpcMassTransferCheckers(index, sender, massTransferTx);
        massTransferCheckers.checkMassTransferGrpc(assetAmount.value(), balanceBeforeMassTx);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Data transaction")
    void getBlockUpdateDataTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, dataTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcDataCheckers dataCheckers = new GrpcDataCheckers(index, sender, dataTxSender);
        dataCheckers.checkDataTransactionGrpc();
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for SetScript transaction")
    void getBlockUpdateSetScriptTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, setScriptTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcSetScriptCheckers setScriptCheckers = new GrpcSetScriptCheckers(index, setScriptTx);
        setScriptCheckers.checkSetScriptGrpc();
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for SponsorFee transaction")
    void getBlockUpdateSponsorFeeTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, sponsorFeeTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcSponsorFeeCheckers sponsorFeeCheckers = new GrpcSponsorFeeCheckers(index, sponsorFeeTx, sponsorFeeIssueAsset);
        sponsorFeeCheckers.checkSponsorFeeGrpc();
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for SetAssetScript transaction")
    void getBlockUpdateSetAssetScriptTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, setAssetScriptTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcSetAssetScriptCheckers assetScriptCheckers = new GrpcSetAssetScriptCheckers(index, setAssetScriptTx, issueTx);
        assetScriptCheckers.checkSetAssetGrpc(0, 0);
    }

    @Test
    @DisplayName("Checking for an getBlockUpdate response for an update asset info transaction")
    void getBlockUpdateForUpdateAssetInfoTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, updateAssetInfoTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcUpdateAssetInfoCheckers updateAssetInfoCheckers = new GrpcUpdateAssetInfoCheckers(updateAssetInfoTx, issueTx);
        updateAssetInfoCheckers.checkUpdateAssetInfo(index, wavesBalanceBeforeUpdateAssetInfoTx, wavesBalanceAfterUpdateAssetInfoTx);
    }

    @Test
    @DisplayName("Checking getBlockUpdate response for Ethereum transaction")
    void getBlockUpdateEthereumTransactionTest() {
        GetBlockUpdateHandler getBlockUpdateHandler = new GetBlockUpdateHandler();
        getBlockUpdateHandler.getBlockUpdateResponseHandler(CHANNEL, heightsList, ethTxId);
        int index = getBlockUpdateHandler.getTxIndex();
        GrpcEthereumTransferCheckers ethereumTransferCheckers = new GrpcEthereumTransferCheckers(index, ethTx, wavesAmount);
        ethereumTransferCheckers.checkEthereumTransfer();
    }
}
