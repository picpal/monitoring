-- 트랜잭션 데이터 샘플
-- 이 스크립트는 H2 트랜잭션 DB에 샘플 데이터를 삽입합니다

-- 트랜잭션 테이블 생성 (H2 자동 생성)
-- CREATE TABLE IF NOT EXISTS transactions (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     transaction_id VARCHAR(50) NOT NULL,
--     amount DECIMAL(15,2) NOT NULL,
--     transaction_date TIMESTAMP NOT NULL,
--     status VARCHAR(20) NOT NULL,
--     user_id VARCHAR(50),
--     description TEXT
-- );

-- 샘플 트랜잭션 데이터 삽입
INSERT INTO transactions (transaction_id, amount, transaction_date, status, user_id, description) VALUES
('TXN001', 1000.00, '2024-01-15 10:30:00', 'COMPLETED', 'USER001', '정상 거래'),
('TXN002', 2500.50, '2024-01-15 11:15:00', 'COMPLETED', 'USER002', '정상 거래'),
('TXN003', 500.00, '2024-01-15 12:00:00', 'FAILED', 'USER003', '잔액 부족'),
('TXN004', 3000.00, '2024-01-15 13:45:00', 'COMPLETED', 'USER001', '정상 거래'),
('TXN005', 750.25, '2024-01-15 14:20:00', 'COMPLETED', 'USER004', '정상 거래'),
('TXN006', 15000.00, '2024-01-15 15:10:00', 'SUSPICIOUS', 'USER005', '대금액 거래'),
('TXN007', 200.00, '2024-01-15 16:30:00', 'COMPLETED', 'USER002', '정상 거래'),
('TXN008', 5000.00, '2024-01-15 17:00:00', 'FAILED', 'USER006', '카드 한도 초과'),
('TXN009', 1200.75, '2024-01-15 18:15:00', 'COMPLETED', 'USER003', '정상 거래'),
('TXN010', 8000.00, '2024-01-15 19:00:00', 'SUSPICIOUS', 'USER007', '비정상 패턴'); 