package com.innim.okkycopy.common.annotation;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(
    factory = WithMockCustomUserSecurityContextFactory.class
)
public @interface WithMockCustomUser {

}
