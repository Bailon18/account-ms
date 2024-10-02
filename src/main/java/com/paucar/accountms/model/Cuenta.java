package com.paucar.accountms.model;

import com.paucar.accountms.util.EstadoCuenta;
import com.paucar.accountms.util.TipoCuenta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Table(name = "cuentas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull(message = "El número de cuenta no puede ser nulo.")
    @Pattern(regexp = "\\d{10,12}", message = "El número de cuenta debe tener entre 10 y 12 dígitos.")
    private String numeroCuenta;

    @NotNull(message = "El saldo no puede ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial debe ser mayor o igual a 0.")
    private Double saldo;

    @NotNull(message = "El tipo de cuenta es obligatorio.")
    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El ID del cliente es obligatorio.")
    @Positive(message = "El ID del cliente debe ser un número positivo.")
    @Column(name = "cliente_id")
    private Long clienteId;

    @NotNull(message = "El estado de la cuenta es obligatorio.")
    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;
}
