package com.googlecode.kanbanik.client.api;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.googlecode.kanbanik.dto.CommandNames;

public class DtoFactory {

    interface BeanFactory extends AutoBeanFactory {
        AutoBean<Dtos.LoginDto> loginDto();

        AutoBean<Dtos.SessionDto> sessionDto();

        AutoBean<Dtos.ErrorDto> errorDto();

        AutoBean<Dtos.UserDto> userDto();

        AutoBean<Dtos.StatusDto> statusDto();

    }

    private static final BeanFactory factory = GWT.create(BeanFactory.class);

    public static Dtos.LoginDto loginDto(String name, String password) {
        Dtos.LoginDto dto = factory.loginDto().as();
        dto.setCommandName(CommandNames.LOGIN.name);
        dto.setUserName(name);
        dto.setPassword(password);
        return dto;
    }

    public static Dtos.ErrorDto errorDto() {
        return factory.errorDto().as();
    }

    public static Dtos.SessionDto sessionDto(String sessionId) {
        Dtos.SessionDto dto = factory.sessionDto().as();
        dto.setSessionId(sessionId);
        return dto;
    }

    public static Dtos.StatusDto statusDto(String sessionId) {
        return factory.statusDto().as();
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
