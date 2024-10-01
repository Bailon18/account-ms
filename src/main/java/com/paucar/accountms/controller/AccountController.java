package com.paucar.accountms.controller;

import com.paucar.accountms.dto.AccountDTO;
import com.paucar.accountms.service.AccountService;
import com.paucar.accountms.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAllAccounts() {
        List<AccountDTO> accounts = accountService.getAllAccounts();
        ApiResponse<List<AccountDTO>> response = ApiResponse.<List<AccountDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lista de cuentas recuperada exitosamente")
                .data(accounts)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(@PathVariable Long id) {
        Optional<AccountDTO> account = accountService.getAccountById(id);
        return account.map(value -> {
            ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                    .status(HttpStatus.OK.value())
                    .message("Cuenta encontrada con éxito")
                    .data(value)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }).orElseGet(() -> {
            ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Cuenta no encontrada con el ID: " + id)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO newAccount = accountService.createAccount(accountDTO);
        ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("Cuenta creada exitosamente")
                .data(newAccount)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO updatedAccount = accountService.updateAccount(id, accountDTO);
        ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Cuenta actualizada exitosamente")
                .data(updatedAccount)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<AccountDTO> accounts = accountService.getAccountsByCustomerId(customerId);
        ApiResponse<List<AccountDTO>> response = ApiResponse.<List<AccountDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lista de cuentas del cliente recuperada exitosamente")
                .data(accounts)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Cuenta eliminada exitosamente")
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/depositar/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> deposit(@PathVariable Long id, @RequestParam Double amount) {
        AccountDTO updatedAccount = accountService.deposit(id, amount);
        ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Depósito realizado con éxito")
                .data(updatedAccount)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/retirar/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> withdraw(@PathVariable Long id, @RequestParam Double amount) {
        AccountDTO updatedAccount = accountService.withdraw(id, amount);
        ApiResponse<AccountDTO> response = ApiResponse.<AccountDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Retiro realizado con éxito")
                .data(updatedAccount)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
