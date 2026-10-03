package com.wepay.backend.upi.repository;

import com.wepay.backend.upi.entity.UpiAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UpiAccountRepository
        extends JpaRepository<UpiAccount, Long> {

    List<UpiAccount> findByUserId(Long userId);

    Optional<UpiAccount> findByUpiId(String upiId);

    boolean existsByUpiId(String upiId);

    Optional<UpiAccount> findByIdAndUserId(
            Long id,
            Long userId
    );
}