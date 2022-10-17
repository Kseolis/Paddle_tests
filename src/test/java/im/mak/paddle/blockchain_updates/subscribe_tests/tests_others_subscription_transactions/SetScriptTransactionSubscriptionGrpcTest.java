package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcSetScriptCheckers;
import im.mak.paddle.helpers.transaction_senders.SetScriptTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SetScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;
import static im.mak.paddle.util.ScriptUtil.fromFile;

public class SetScriptTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account account;

    private Base64String script = node().compileScript(
            fromFile("ride_scripts/permissionOnUpdatingKeyValues.ride")
    ).script();

    @BeforeEach
    void setUp() {
        account = new Account(DEFAULT_FAUCET);
    }

    @Test
    @DisplayName("Check subscription on setScript transaction")
    void subscribeTestForSetScriptTransaction() {
        SetScriptTransactionSender txSender = new SetScriptTransactionSender(account, script);
        txSender.setScriptTransactionSender(MIN_FEE, LATEST_VERSION);
        String txId = txSender.getSetScriptTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcSetScriptCheckers grpcSetScriptCheckers = new GrpcSetScriptCheckers(0, txSender);
        grpcSetScriptCheckers.checkSetScriptGrpc();
    }

    @Test
    @DisplayName("Check subscription on set 32kb_size Script transaction")
    void subscribeTestForSet32kbScript() {
        fromHeight = node().getHeight();
        long minimalValSetScriptFee = 2200000;
        script = node().compileScript(fromFile("ride_scripts/scriptSize32kb.ride")).script();

        SetScriptTransactionSender txSender = new SetScriptTransactionSender(account, script);
        txSender.setScriptTransactionSender(minimalValSetScriptFee, LATEST_VERSION);
        String txId = txSender.getSetScriptTx().id().toString();

        toHeight = node().getHeight();
        subscribeResponseHandler(CHANNEL, fromHeight, toHeight, txId);
        GrpcSetScriptCheckers grpcSetScriptCheckers = new GrpcSetScriptCheckers(0, txSender);
        grpcSetScriptCheckers.checkSetScriptGrpc();
    }

    @Test
    @DisplayName("Check subscription on set DApp Script transaction")
    void subscribeTestForSetDAppScript() {
        script = node().compileScript("{-# STDLIB_VERSION 4 #-}\n" +
                "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                "{-# CONTENT_TYPE DAPP #-}").script();

        SetScriptTransactionSender txSender = new SetScriptTransactionSender(account, script);
        txSender.setScriptTransactionSender(0, LATEST_VERSION);
        String txId = txSender.getSetScriptTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcSetScriptCheckers grpcSetScriptCheckers = new GrpcSetScriptCheckers(0, txSender);
        grpcSetScriptCheckers.checkSetScriptGrpc();
    }

    @Test
    @DisplayName("Check subscription on set LIBRARY Script transaction")
    void subscribeTestForSetLibraryScript() {
        script = node().compileScript("{-# STDLIB_VERSION 6 #-}\n" +
                "{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                "{-# CONTENT_TYPE LIBRARY #-}").script();

        SetScriptTransactionSender txSender = new SetScriptTransactionSender(account, script);
        txSender.setScriptTransactionSender(0, LATEST_VERSION);
        String txId = txSender.getSetScriptTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        GrpcSetScriptCheckers grpcSetScriptCheckers = new GrpcSetScriptCheckers(0, txSender);
        grpcSetScriptCheckers.checkSetScriptGrpc();
    }
}
