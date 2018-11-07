package com.goode.repository;

import com.goode.business.Account;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:4200")
public interface AccountRepository extends CrudRepository<Account, Long> {

  Account findAccountById(int id);
  List<Account> findAccountByUsernameOrEmail(String username, String email);
  Account findAccountByEmail(String email);
  Account findAccountByUsername(String username);

}
