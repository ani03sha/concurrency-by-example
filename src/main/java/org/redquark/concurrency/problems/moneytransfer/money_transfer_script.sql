CREATE TABLE accounts (
    account_id INTEGER PRIMARY KEY,
    account_holder_name VARCHAR(255) NOT NULL,
    balance DECIMAL(18, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    source_account_id INTEGER NOT NULL,
    destination_account_id INTEGER NOT NULL,
    amount DECIMAL(18, 2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (destination_account_id) REFERENCES accounts(account_id)
);

-- Money transfer logic

BEGIN;

-- Check source account balance
SELECT balance INTO @source_balance FROM accounts WHERE account_id = 1 FOR UPDATE;

-- Ensure enough funds
IF @source_balance < 500 THEN
    ROLLBACK;
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient Funds';
END IF;


-- Deduct from source
UPDATE accounts SET balance = balance - 500 WHERE account_id = 1;

-- Add to destination
UPDATE accounts SET balance = balance + 500 WHERE account_id = 2;

-- Record transaction
INSERT INTO transactions (source_account_id, destination_account_id, amount, status) VALUES (1, 2, 500, 'SUCCESS');

COMMIT;