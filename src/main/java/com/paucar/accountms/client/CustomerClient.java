package com.paucar.accountms.client;

import com.paucar.accountms.model.Customer;
import com.paucar.accountms.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "customer-ms")
public interface CustomerClient {

    @GetMapping("/customer/{id}")
    ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable("id") Long id);
}
