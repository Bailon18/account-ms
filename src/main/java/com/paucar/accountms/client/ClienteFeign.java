package com.paucar.accountms.client;

import com.paucar.accountms.model.Cliente;
import com.paucar.accountms.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Quitar el parámetro 'url' al manejarse localmente
@FeignClient(name = "CUSTOMER-MS", url = "https://customer-ms-production.up.railway.app")
public interface ClienteFeign {

    @GetMapping("/cliente/{id}")
    ResponseEntity<ApiResponse<Cliente>> obtenerCliente(@PathVariable("id") Long id);
}