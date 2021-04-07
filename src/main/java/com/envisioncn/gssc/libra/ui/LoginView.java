package com.envisioncn.gssc.libra.ui;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author jonnas
 * @date 2021-04-07
 */
@Route(value = LoginView.ROUTE)
@PageTitle("Libra - Login")
@Slf4j
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<String> {
    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();
    @Value("${libra.version}")
    private String buildNumber;

    public LoginView() {
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("Libra");
        login.setForgotPasswordButtonVisible(false);
        getElement().appendChild(login.getElement());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.debug("beforeEnter: {}", event);
        login.setDescription("Build " + buildNumber);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (StringUtils.equals(parameter, "no_roles")) {
            LoginI18n i18n = LoginI18n.createDefault();
            LoginI18n.ErrorMessage em = new LoginI18n.ErrorMessage();
            em.setMessage("You don't have access to this view");
            em.setTitle("No access");
            i18n.setErrorMessage(em);
            login.setError(true);
            login.setI18n(i18n);
        } else if (StringUtils.contains(parameter, "error") || event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setI18n(LoginI18n.createDefault());
            login.setError(true);
        } else {
            login.setError(false);
        }
    }
}

