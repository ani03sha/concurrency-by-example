package org.redquark.concurrency.problems.moneytransfer.logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferLogger {

    public void log(String fromId, String toId, BigDecimal amount, boolean success, String reason) {
        System.out.printf("[%s] Transfer %s from %s to %s | Amount: %s | %s\n",
                LocalDateTime.now(),
                success ? "SUCCESS" : "FAILED",
                fromId,
                toId,
                amount,
                reason);
    }
}
