import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    String accountNumber;

    OperationsQueue operationsQueue;

    int balance = 0;

    private final Lock balanceLock = new ReentrantLock();

    public Bank(String accountNumber, OperationsQueue operationsQueue) {
        this.accountNumber = accountNumber;
        this.operationsQueue = operationsQueue;
    }

    // A deposit function that will run in parallel on a separate thread. It will be a loop where in each iteration, it read the amount from the operationQueue and deposit the amount.
    public void deposit() {
        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
        // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
        while (true) {
            int amount = operationsQueue.getNextItem();
            if (amount == -9999) {
                break;
            }
            if (amount > 0) {
                balanceLock.lock();
                try {
                    balance = balance + amount;
                    System.out.println("Deposited: " + amount + " Balance: " + balance);
                } finally {
                    balanceLock.unlock();
                }
            } else {
                operationsQueue.add(amount);
                System.out.println("operation added back " + amount);
            }

        }
    }

    // A withdraw function that will run in parallel on a separate thread. It will be a loop where in each iteration, it read the amount from the operationQueue and withdraw the amount.
    public void withdraw() {
        while (true) {
            int amount = operationsQueue.getNextItem();
            if (amount == -9999) {
                break;
            }

            if (balance + amount < 0) {
                System.out.println("Not enough balance to withdraw " + amount);
                continue;
            }

            if (amount < 0) {
                balanceLock.lock();
                try {
                    balance = balance + amount;
                    System.out.println("Withdrawn: " + amount + " Balance: " + balance);
                } finally {
                    balanceLock.unlock();
                }
            } else {
                operationsQueue.add(amount);
                System.out.println("operation added back " + amount);
            }

        }
    }
}
