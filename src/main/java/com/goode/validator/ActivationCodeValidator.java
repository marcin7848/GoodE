package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.ActivationCode;
import com.goode.repository.ActivationCodeRepository;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivationCodeValidator extends BaseValidator {

  @Autowired
  ActivationCodeRepository activationCodeRepository;

  public ActivationCode validateCode(String code, int activatonCodeType, ErrorCode errorCode){
    ActivationCode activationCode = new ActivationCode();
    activationCode.setCode(code);

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();
    Set<ConstraintViolation<ActivationCode>> errors = validator.validateProperty(activationCode, "code");

    if(!errors.isEmpty()) {
      errorCode.rejectValue("code", "error.activationCode.incorrect");
      return null;
    }

    activationCode = activationCodeRepository.getActivationCodeByCodeAndType(code, activatonCodeType);

    if(activationCode == null) {
      errorCode.rejectValue("code", "error.activationCode.incorrect");
      return null;
    }

    return activationCode;
  }
}
