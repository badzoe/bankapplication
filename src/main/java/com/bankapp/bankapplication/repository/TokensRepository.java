package com.bankapp.bankapplication.repository;

import com.bankapp.bankapplication.entity.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokensRepository extends JpaRepository<Tokens,Long> {
    Optional<Tokens> findFirstByUser_IdOrderByIdDesc(long id);
}
