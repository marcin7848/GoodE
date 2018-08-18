package com.goode.repository;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {

  List<ActivationCode> getActivationCodeById(int id);
}
