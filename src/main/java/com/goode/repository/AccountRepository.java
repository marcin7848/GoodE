package com.goode.repository;

import com.goode.business.Account;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

  Account findAccountById(int id);
  List<Account> findByUsernameOrEmail(String username, String email);

}
