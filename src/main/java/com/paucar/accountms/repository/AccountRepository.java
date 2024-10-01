package com.paucar.accountms.repository;

import com.paucar.accountms.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);
    List<Account> findByClienteId(Long clienteId);

}
