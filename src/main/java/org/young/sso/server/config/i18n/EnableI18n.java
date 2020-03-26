package org.young.sso.server.config.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/***
 * 启用国际化功能
 * @author pengy
 * @date 2018年11月10日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(I18nAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableI18n {

}
