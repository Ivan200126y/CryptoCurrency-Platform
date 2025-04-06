//package com.telerikacademy.web.cryptocurrency_trading_platform.repositories;
//
//import com.telerikacademy.web.cryptocurrency_trading_platform.models.Transaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class TransactionDaoRepositoryImpl {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public TransactionDaoRepositoryImpl(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public Transaction findTransactionById(Long id) {
//        String sql = "select * from transaction where id = ?";
//        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new TransactionRowMapper());
//    }
//}
