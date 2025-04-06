package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;

import com.telerikacademy.web.cryptocurrency_trading_platform.enums.Status;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
import com.telerikacademy.web.cryptocurrency_trading_platform.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionDaoRepositoryImpl implements TransactionDaoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Transaction> rowMapper;
    private UserDaoRepository userDaoRepository;

    @Autowired
    public TransactionDaoRepositoryImpl(JdbcTemplate jdbcTemplate,
                                        UserDaoRepository userDaoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setCurrency(rs.getString("currency"));
            transaction.setAmount(rs.getDouble("amount"));
            transaction.setStatus(Status.valueOf(rs.getString("status")));
            transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            transaction.setPrice(rs.getDouble("price"));
            transaction.setShares(rs.getDouble("shares"));

            this.userDaoRepository = userDaoRepository;
            User user = userDaoRepository.findUserById(rs.getLong("user_id"));
            transaction.setUser(user);
            return transaction;
        };
    }

    @Override
    public Transaction findTransactionById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions (currency, amount, status, created_at, price, shares, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                transaction.getCurrency(),
                transaction.getAmount(),
                transaction.getStatus().name(),
                transaction.getCreatedAt(),
                transaction.getPrice(),
                transaction.getShares(),
                transaction.getUser().getId());
    }

    @Override
    public void delete(Transaction transaction) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        jdbcTemplate.update(sql, transaction.getId());
    }

    @Override
    public void deleteAllByUser(User user) {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getId());
    }
}
