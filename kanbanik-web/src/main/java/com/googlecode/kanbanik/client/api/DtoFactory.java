package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

public class DtoFactory {

    interface BeanFactory extends AutoBeanFactory {
        AutoBean<Dtos.LoginDto> loginDto();

        AutoBean<Dtos.ErrorDto> errorDto();

        AutoBean<Dtos.UserDto> userDto();
    }

    private static final BeanFactory factory = GWT.create(BeanFactory.class);

    public static Dtos.LoginDto loginDto() {
        return factory.loginDto().as();
    }

    public static Dtos.ErrorDto errorDto() {
        return factory.errorDto().as();
    }

    public static <T> T asDto(Class<T> clazz, String json) {
        AutoBean<T> bean = AutoBeanCodex.decode(factory, clazz, json);
        return bean.as();
    }

    public static <T> String asJson(T dto) {
        AutoBean<T> bean = AutoBeanUtils.getAutoBean(dto);
        return "command="+ AutoBeanCodex.encode(bean).getPayload();
    }

}
