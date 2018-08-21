package com.goode.repository;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {

  List<ActivationCode> getActivationCodeById(int id);
  List<ActivationCode> getActivationCodeByCode(String code);

  @Query(value = "select * from activation_codes a where a.type = :type and a.id_account = :id_account and a.creation_time <= NOW() - :hoursAgo * INTERVAL '1 hour'", nativeQuery = true)
  List<ActivationCode> getAllAddedAtLeastXHoursAgo(@Param("id_account") int id_account, @Param("type") int type, @Param("hoursAgo") int hoursAgo);

  List<ActivationCode> getActivationCodesByAccountAndType(Account account, int type);

}
