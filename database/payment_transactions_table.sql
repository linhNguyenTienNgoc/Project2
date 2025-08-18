-- =====================================================
-- PAYMENT TRANSACTIONS TABLE
-- Table để log các giao dịch thanh toán chi tiết
-- =====================================================

-- Drop table if exists (for development)
-- DROP TABLE IF EXISTS payment_transactions;

-- Create payment_transactions table
CREATE TABLE IF NOT EXISTS payment_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL DEFAULT 'cash',
    amount_received DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    change_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'completed',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_order_id (order_id),
    INDEX idx_payment_method (payment_method),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SAMPLE DATA (Optional)
-- =====================================================

-- Uncomment below to insert sample payment transactions
/*
INSERT INTO payment_transactions (order_id, payment_method, amount_received, final_amount, change_amount, status, notes) VALUES
(1, 'cash', 100000, 95000, 5000, 'completed', 'Thanh toán tiền mặt'),
(2, 'momo', 50000, 50000, 0, 'completed', 'Thanh toán qua MoMo'),
(3, 'vnpay', 75000, 75000, 0, 'completed', 'Thanh toán qua VNPay'),
(4, 'card', 120000, 120000, 0, 'completed', 'Thanh toán thẻ tín dụng');
*/

-- =====================================================
-- VIEWS FOR REPORTING
-- =====================================================

-- View for payment statistics
CREATE OR REPLACE VIEW payment_statistics AS
SELECT 
    payment_method,
    COUNT(*) as transaction_count,
    SUM(final_amount) as total_amount,
    AVG(final_amount) as average_amount,
    MAX(transaction_date) as last_transaction,
    MIN(transaction_date) as first_transaction
FROM payment_transactions 
WHERE status = 'completed'
GROUP BY payment_method;

-- View for daily payment summary
CREATE OR REPLACE VIEW daily_payment_summary AS
SELECT 
    DATE(transaction_date) as payment_date,
    payment_method,
    COUNT(*) as transaction_count,
    SUM(final_amount) as daily_total,
    AVG(final_amount) as daily_average
FROM payment_transactions 
WHERE status = 'completed'
GROUP BY DATE(transaction_date), payment_method
ORDER BY payment_date DESC, payment_method;

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

-- Procedure to get payment statistics for date range
DELIMITER ;;
CREATE PROCEDURE IF NOT EXISTS GetPaymentStats(
    IN start_date DATE,
    IN end_date DATE
)
BEGIN
    SELECT 
        payment_method,
        COUNT(*) as total_transactions,
        SUM(final_amount) as total_amount,
        AVG(final_amount) as average_amount,
        SUM(change_amount) as total_change
    FROM payment_transactions 
    WHERE DATE(transaction_date) BETWEEN start_date AND end_date
    AND status = 'completed'
    GROUP BY payment_method
    ORDER BY total_amount DESC;
END;;
DELIMITER ;

-- =====================================================
-- NOTES
-- =====================================================

/*
This table provides:

1. ✅ Complete payment transaction logging
2. ✅ Support for all payment methods (cash, card, e-wallets)
3. ✅ Change calculation tracking
4. ✅ Performance indexes for fast queries
5. ✅ Foreign key relationship with orders table
6. ✅ Views for easy reporting
7. ✅ Stored procedure for statistics

Usage:
- Every payment processed through PaymentService will be logged here
- Supports audit trail for financial reconciliation
- Enables detailed payment method analysis
- Provides data for business intelligence and reporting

Integration:
- PaymentService.logPaymentTransaction() method will insert into this table
- Payment reports can query this table for detailed statistics
- Admin dashboard can show payment method breakdown
*/
