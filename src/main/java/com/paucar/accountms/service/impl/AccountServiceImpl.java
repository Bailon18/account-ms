package com.paucar.accountms.service.impl;

import com.paucar.accountms.client.CustomerClient;
import com.paucar.accountms.dto.AccountDTO;
import com.paucar.accountms.exception.AccountAlreadyExistsException;
import com.paucar.accountms.exception.AccountNotFoundException;
import com.paucar.accountms.exception.CustomerNotFoundException;
import com.paucar.accountms.exception.InsufficientBalanceException;
import com.paucar.accountms.mapper.AccountMapper;
import com.paucar.accountms.model.Account;
import com.paucar.accountms.model.Customer;
import com.paucar.accountms.repository.AccountRepository;
import com.paucar.accountms.service.AccountService;
import com.paucar.accountms.util.ApiResponse;
import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerClient customerClient;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository,
                              CustomerClient customerClient,
                              AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.customerClient = customerClient;
        this.accountMapper = accountMapper;
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)  // Convertir cada entidad Account a AccountDTO
                .sorted(Comparator.comparing(AccountDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(accountMapper::toDto);  // Convertir la entidad a DTO si existe
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {

        // Validar la existencia del cliente
        ResponseEntity<ApiResponse<Customer>> response = customerClient.getCustomer(accountDTO.getClienteId());

        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new CustomerNotFoundException("El cliente con ID: " + accountDTO.getClienteId() + " no existe.");
        }

        // Validar si la cuenta ya existe
        if (accountRepository.existsByNumeroCuenta(accountDTO.getNumeroCuenta())) {
            throw new AccountAlreadyExistsException("La cuenta con número " + accountDTO.getNumeroCuenta() + " ya existe.");
        }

        // Convertir DTO a entidad para realizar la operación
        Account account = accountMapper.toEntity(accountDTO);
        account.setEstado(EstadoCuenta.ACTIVO);

        // Generar número de cuenta si no se especificó
        if (account.getNumeroCuenta() == null || account.getNumeroCuenta().isEmpty()) {
            account.setNumeroCuenta(generateAccountNumber());
        }

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);  // Convertir la entidad guardada a DTO
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        // Buscar la entidad existente
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con el ID: " + id));

        // Actualizar los detalles de la cuenta desde el DTO
        existingAccount.setSaldo(accountDTO.getSaldo());
        existingAccount.setTipoCuenta(accountDTO.getTipoCuenta());
        existingAccount.setEstado(accountDTO.getEstado());

        // Guardar la entidad actualizada
        Account savedAccount = accountRepository.save(existingAccount);
        return accountMapper.toDto(savedAccount);  // Convertir la entidad guardada a DTO
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Cuenta no encontrada con el ID: " + id);
        }
        accountRepository.deleteById(id);
    }
    

    @Override
    public AccountDTO deposit(Long id, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor que 0.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con el ID: " + id));

        if (account.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.");
        }

        account.setSaldo(account.getSaldo() + amount);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);  // Convertir a DTO
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByClienteId(customerId).stream()
                .map(accountMapper::toDto)  // Convertir cada entidad a DTO
                .collect(Collectors.toList());
    }

    @Override
    public AccountDTO withdraw(Long id, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor que 0.");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada con el ID: " + id));

        if (account.getEstado() != EstadoCuenta.ACTIVO) {
            throw new IllegalStateException("No se pueden realizar transacciones en cuentas que no estén ACTIVAS.");
        }

        if (account.getTipoCuenta() == TipoCuenta.AHORROS && account.getSaldo() < amount) {
            throw new InsufficientBalanceException("Saldo insuficiente en la cuenta de ahorros.");
        }
        if (account.getTipoCuenta() == TipoCuenta.CORRIENTE && account.getSaldo() - amount < -500) {
            throw new InsufficientBalanceException("Límite de sobregiro alcanzado en la cuenta corriente.");
        }

        account.setSaldo(account.getSaldo() - amount);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);  // Convertir a DTO
    }

    private String generateAccountNumber() {
        return String.valueOf((long) (Math.random() * 10000000000L));
    }
}
