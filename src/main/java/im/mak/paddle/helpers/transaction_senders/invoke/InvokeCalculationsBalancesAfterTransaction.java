package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;

import java.util.List;

import static im.mak.paddle.helpers.PrepareInvokeTestsData.getInvokeFee;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.getIntArg;

public class InvokeCalculationsBalancesAfterTransaction {
    private static long callerBalanceWavesBeforeTransaction;
    private static long callerBalanceIssuedAssetsBeforeTransaction;
    private static long callerBalanceWavesAfterTransaction;
    private static long callerBalanceIssuedAssetsAfterTransaction;

    private static long dAppBalanceWavesBeforeTransaction;
    private static long dAppBalanceIssuedAssetsBeforeTransaction;
    private static long dAppBalanceWavesAfterTransaction;
    private static long dAppBalanceIssuedAssetsAfterTransaction;

    private static long accBalanceWavesBeforeTransaction;
    private static long accBalanceIssuedAssetsBeforeTransaction;
    private static long accBalanceWavesAfterTransaction;
    private static long accBalanceIssuedAssetsAfterTransaction;

    private static String invokeResultData;

    public static void balancesAfterPaymentInvoke(Account caller, Account dApp, List<Amount> amounts, AssetId id) {
        prepareBalances(caller, dApp, id);

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
        prepareBalances(caller, dApp, id);

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
        prepareBalances(caller, dApp, id);

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
        prepareBalances(caller, dApp, id);

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


    public static void balancesAfterCallerScriptTransfer
            (Account caller, Account dApp, Account acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction -= a.value();
                            accBalanceWavesAfterTransaction += a.value();
                        } else if (a.assetId().equals(id)) {
                            callerBalanceIssuedAssetsAfterTransaction -= a.value();
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public static void balancesAfterDAppToDApp(Account caller, Account dApp, Account acc, List<Amount> amounts, AssetId id) {
        prepareThreeAccBalances(caller, dApp, acc, id);
        invokeResultData = String.valueOf(getIntArg() * 2);

        if (!amounts.isEmpty()) {
            amounts.forEach(
                    a -> {
                        if (a.assetId().isWaves()) {
                            dAppBalanceWavesAfterTransaction += a.value();
                            accBalanceWavesAfterTransaction -= a.value();
                        } else if (a.assetId().equals(id)) {
                            dAppBalanceIssuedAssetsAfterTransaction -= a.value();
                            accBalanceIssuedAssetsAfterTransaction += a.value();
                        }
                    }
            );
        }
    }

    public static long getCallerBalanceWavesBeforeTransaction() {
        return callerBalanceWavesBeforeTransaction;
    }

    public static long getCallerBalanceIssuedAssetsBeforeTransaction() {
        return callerBalanceIssuedAssetsBeforeTransaction;
    }

    public static long getDAppBalanceWavesBeforeTransaction() {
        return dAppBalanceWavesBeforeTransaction;
    }

    public static long getDAppBalanceIssuedAssetsBeforeTransaction() {
        return dAppBalanceIssuedAssetsBeforeTransaction;
    }

    public static long getAccBalanceWavesBeforeTransaction() {
        return accBalanceWavesBeforeTransaction;
    }

    public static long getAccBalanceIssuedAssetsBeforeTransaction() {
        return accBalanceIssuedAssetsBeforeTransaction;
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

    public static long getAccBalanceWavesAfterTransaction() {
        return accBalanceWavesAfterTransaction;
    }

    public static long getAccBalanceIssuedAssetsAfterTransaction() {
        return accBalanceIssuedAssetsAfterTransaction;
    }

    public static String getInvokeResultData() {
        return invokeResultData;
    }

    private static void prepareBalances(Account caller, Account dApp, AssetId id) {
        callerBalanceWavesBeforeTransaction = caller.getWavesBalance();
        dAppBalanceWavesBeforeTransaction = dApp.getWavesBalance();
        callerBalanceIssuedAssetsBeforeTransaction = caller.getBalance(id);
        dAppBalanceIssuedAssetsBeforeTransaction = dApp.getBalance(id);

        callerBalanceWavesAfterTransaction = callerBalanceWavesBeforeTransaction - getInvokeFee();
        callerBalanceIssuedAssetsAfterTransaction = callerBalanceIssuedAssetsBeforeTransaction;
        dAppBalanceWavesAfterTransaction = dAppBalanceWavesBeforeTransaction;
        dAppBalanceIssuedAssetsAfterTransaction = dAppBalanceIssuedAssetsBeforeTransaction;
    }

    private static void prepareThreeAccBalances(Account caller, Account dApp, Account acc, AssetId id) {
        prepareBalances(caller, dApp, id);

        accBalanceWavesBeforeTransaction = acc.getWavesBalance();
        accBalanceIssuedAssetsBeforeTransaction = acc.getBalance(id);
        accBalanceWavesAfterTransaction = accBalanceWavesBeforeTransaction;
        accBalanceIssuedAssetsAfterTransaction = accBalanceIssuedAssetsBeforeTransaction;
    }
}
