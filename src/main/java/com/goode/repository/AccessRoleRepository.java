package com.goode.repository;

import com.goode.business.AccessRole;
import org.springframework.data.repository.CrudRepository;

public interface AccessRoleRepository extends CrudRepository<AccessRole, Long> {
  AccessRole getAccessRoleById(int id);
  AccessRole getAccessRoleByRole(String role);
}
