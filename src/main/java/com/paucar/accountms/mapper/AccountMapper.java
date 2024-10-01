package com.paucar.accountms.mapper;

import com.paucar.accountms.dto.AccountDTO;
import com.paucar.accountms.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDto(Account account);
    Account toEntity(AccountDTO accountDTO);

}
