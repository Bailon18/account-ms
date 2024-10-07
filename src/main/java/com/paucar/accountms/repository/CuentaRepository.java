package com.paucar.accountms.repository;

import com.paucar.accountms.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    boolean existsByNumeroCuenta(String numeroCuenta);
    List<Cuenta> findByClienteId(Long clienteId);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

}
