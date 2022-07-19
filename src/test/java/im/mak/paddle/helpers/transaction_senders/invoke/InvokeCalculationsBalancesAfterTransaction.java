package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;

import java.util.List;

public class InvokeCalculationsBalancesAfterTransaction extends InvokeScriptTransactionSender {
    private static long callerBalanceWavesAfterTransaction;
    private static long callerBalanceIssuedAssetsAfterTransaction;
    private static long dAppBalanceWavesAfterTransaction;
    private static long dAppBalanceIssuedAssetsAfterTransaction;
    private static long dApp2BalanceWavesAfterTransaction;
    private static long dApp2BalanceIssuedAssetsAfterTransaction;

    public static void balancesAfterPaymentInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
        callerBalanceWavesAfterTransaction = caller.getWavesBalance() - fee - extraFee;
        callerBalanceIssuedAssetsAfterTransaction = caller.getBalance(id);
        dAppBalanceWavesAfterTransaction = dApp.getWavesBalance();
        dAppBalanceIssuedAssetsAfterTransaction = dApp.getBalance(id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction += a.value();
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                        }
                    }
            );
        }
    }

    public static void balancesAfterBurnAssetInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
        callerBalanceWavesAfterTransaction = caller.getWavesBalance() - fee - extraFee;
        callerBalanceIssuedAssetsAfterTransaction = caller.getBalance(id);
        dAppBalanceWavesAfterTransaction = dApp.getWavesBalance();
        dAppBalanceIssuedAssetsAfterTransaction = dApp.getBalance(id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                        }
                    }
            );
        }
    }

    public static void balancesAfterReissueAssetInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
        callerBalanceWavesAfterTransaction = caller.getWavesBalance() - fee - extraFee;
        callerBalanceIssuedAssetsAfterTransaction = caller.getBalance(id);
        dAppBalanceWavesAfterTransaction = dApp.getWavesBalance();
        dAppBalanceIssuedAssetsAfterTransaction = dApp.getBalance(id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public static void balancesAfterCallerInvokeAsset(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
        callerBalanceWavesAfterTransaction = caller.getWavesBalance() - fee - extraFee;
        callerBalanceIssuedAssetsAfterTransaction = caller.getBalance(id);
        dAppBalanceWavesAfterTransaction = dApp.getWavesBalance();
        dAppBalanceIssuedAssetsAfterTransaction = dApp.getBalance(id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            callerBalanceWavesAfterTransaction -= a.value();
                            dAppBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            dAppBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public static void balancesAfterDAppToDApp(Account caller, Account dApp, Account dApp2, List<Amount> amounts, AssetId id) {
        callerBalanceWavesAfterTransaction = caller.getWavesBalance() - fee - extraFee;
        dAppBalanceWavesAfterTransaction = dApp.getWavesBalance();
        dApp2BalanceWavesAfterTransaction = dApp2.getWavesBalance();

        callerBalanceIssuedAssetsAfterTransaction = caller.getBalance(id);
        dAppBalanceIssuedAssetsAfterTransaction = dApp.getBalance(id);
        dApp2BalanceIssuedAssetsAfterTransaction = dApp2.getBalance(id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction += a.value();
                            dApp2BalanceWavesAfterTransaction -= a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                            dApp2BalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public static long getCallerBalanceWavesAfterTransaction() {
        return callerBalanceWavesAfterTransaction;
    }

    public static long getCallerBalanceIssuedAssetsAfterTransaction() {
        return callerBalanceIssuedAssetsAfterTransaction;
    }

    public static long getDAppBalanceWavesAfterTransaction() {
        return dAppBalanceWavesAfterTransaction;
    }

    public static long getDAppBalanceIssuedAssetsAfterTransaction() {
        return dAppBalanceIssuedAssetsAfterTransaction;
    }

    public static long getDApp2BalanceWavesAfterTransaction() {
        return dApp2BalanceWavesAfterTransaction;
    }

    public static long getDApp2BalanceIssuedAssetsAfterTransaction() {
        return dApp2BalanceIssuedAssetsAfterTransaction;
    }
}
