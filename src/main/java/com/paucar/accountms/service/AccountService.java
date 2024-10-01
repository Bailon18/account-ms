package com.paucar.accountms.service;


import com.paucar.accountms.dto.AccountDTO;


import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<AccountDTO> getAllAccounts();
    Optional<AccountDTO> getAccountById(Long id);
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO updateAccount(Long id, AccountDTO accountDTO);
    void deleteAccount(Long id);
    AccountDTO deposit(Long id, Double amount);
    AccountDTO withdraw(Long id, Double amount);
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
}
