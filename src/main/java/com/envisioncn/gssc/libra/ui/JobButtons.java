package com.envisioncn.gssc.libra.ui;

import com.envisioncn.gssc.libra.core.BasicJobInstanceInfo;
import com.envisioncn.gssc.libra.core.LibraManager;
import com.google.common.collect.Maps;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.NoSuchJobException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jonnas
 * @date 2021-04-07
 */
@Slf4j
public class JobButtons extends FlexLayout {
    private static final long serialVersionUID = -1688734384151952795L;
    private LibraManager libraManager;

    private MainView mainView;

    BasicJobInstanceInfo jobInstanceInfo;

    private Button startButton, stopButton, restartButton, infoButton, abandonButton;

    boolean enabled;

    public JobButtons(BasicJobInstanceInfo jobInstanceInfo, MainView mainView, LibraManager libraManager) {
        this.mainView = mainView;
        this.libraManager = libraManager;
        this.jobInstanceInfo = jobInstanceInfo;
        this.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        FlexLayout hl = new FlexLayout();
        startButton = new Button("", clickEvent -> {
            if (this.jobInstanceInfo.hasJobParameters()) {
                Dialog d = createJobParamsDialog(this.jobInstanceInfo);
                d.open();
            }
            else {
                doStartJob(this.jobInstanceInfo, null);
            }
        });
        startButton.setIcon(VaadinIcon.PLAY.create());
        startButton.getElement().setProperty("title", "Start a new job instance");

        hl.add(startButton);

        stopButton = new Button("", clickEvent -> {
            try {
                libraManager.stopJob(this.jobInstanceInfo.getName());
            }
            catch(Exception e) {
                UIUtils.showErrorMessage(e.getMessage());
            }
        });
        stopButton.setIcon(VaadinIcon.STOP.create());
        stopButton.addClassName("margin-left");
        stopButton.getElement().setProperty("title", "Stop a running job");

        hl.add(stopButton);

        restartButton = new Button("", clickEvent -> {
            try {
                libraManager.restartJob(this.jobInstanceInfo.getLatestExecutionId());
            } catch (Exception e) {
                UIUtils.showErrorMessage(e.getMessage());
                log.warn("Failed to restart job", e);
            }
        });
        restartButton.setIcon(VaadinIcon.ROTATE_LEFT.create());
        restartButton.addClassName("margin-left");
        restartButton.getElement().setProperty("title", "Restart a failed or interrupted job. Will pick up where it left off.");

        hl.add(restartButton);

        abandonButton = new Button("", clickEvent -> {
            try {
                libraManager.abandonJob(this.jobInstanceInfo.getLatestExecutionId());
            } catch (Exception e) {
                UIUtils.showErrorMessage(e.getMessage());
                log.warn("Failed to abandon job", e);
            }
        });
        abandonButton.getElement().setProperty("title", "Abandons a job. Useful when the process was killed while a job was running and is now stuck in running state.");
        abandonButton.setIcon(VaadinIcon.TRASH.create());

        abandonButton.addClassName("margin-left");
        hl.add(abandonButton);

        infoButton = new Button("", clickEvent -> {

            Dialog infoDialog = new Dialog();
            infoDialog.add(mainView.createJobHistory(this.jobInstanceInfo.getName()));
            infoDialog.setWidth("800px");
            infoDialog.setHeight("300px");
            infoDialog.open();
        });
        infoButton.setIcon(VaadinIcon.BULLETS.create());
        infoButton.addClassName("margin-left");
        hl.add(infoButton);



        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        restartButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        abandonButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        infoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        this.add(hl);

        if (this.jobInstanceInfo.getOptionalJobParamKeys() != null && this.jobInstanceInfo.getOptionalJobParamKeys().size() > 0) {
            Label l = new Label("Optional params: " + StringUtils.join(this.jobInstanceInfo.getOptionalJobParamKeys(),","));
            l.getStyle().set("font-size", "0.8em");
            this.add(l);
        }
        if (this.jobInstanceInfo.getRequiredJobParamKeys() != null && this.jobInstanceInfo.getRequiredJobParamKeys().size() > 0) {
            Label l = new Label("Required params: " + StringUtils.join(this.jobInstanceInfo.getRequiredJobParamKeys(),","));
            l.getStyle().set("font-size", "0.8em");
            this.add(l);
        }
        updateButtonState();

    }

    public void setJobInstanceInfo(BasicJobInstanceInfo jobInstanceInfo) {
        this.jobInstanceInfo = jobInstanceInfo;
        updateButtonState();
    }

    public void setRunEnabled(boolean enabled) {
        this.enabled = enabled;
        updateButtonState();

    }

    private void updateButtonState() {
        if (enabled) {
            if ("STARTED".equals(this.jobInstanceInfo.getExecutionStatus())) {
                startButton.setEnabled(false);
                restartButton.setEnabled(false);
                stopButton.setEnabled(true);
            } else {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                restartButton.setEnabled(jobInstanceInfo.isRestartable());
            }
        }
        else {
            stopButton.setEnabled(false);
            startButton.setEnabled(false);
            restartButton.setEnabled(false);
            abandonButton.setEnabled(false);
        }
    }
    private void doStartJob(BasicJobInstanceInfo item, Map<String, String> params) {
        try {
            libraManager.startJob(item.getName(), params);
        }
        catch(JobParametersInvalidException | NoSuchJobException e) {
            UIUtils.showErrorMessage(e.getMessage());
        } catch (JobInstanceAlreadyExistsException e) {
            UIUtils.showErrorMessage("This job is already running");
        }
    }

    private Dialog createJobParamsDialog(BasicJobInstanceInfo job) {
        Dialog dialog = new Dialog();
        dialog.setWidth("700");
        HashMap<String, TextField> requiredTextFields = Maps.newHashMap();
        HashMap<String, TextField> optionalTextFields = Maps.newHashMap();

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setWidthFull();

        formLayout.add(new H4("Job Parameters"));
        if (job.getRequiredJobParamKeys() != null) {
            for (String key : job.getRequiredJobParamKeys()) {
                TextField paramField = new TextField();
                paramField.setLabel(key);
                paramField.setRequired(true);
                //paramField.setRequiredIndicatorVisible(true);
                paramField.setWidthFull();
                formLayout.add(paramField);
                requiredTextFields.put(key, paramField);
            }
        }
        if (job.getOptionalJobParamKeys() != null) {
            for (String key : job.getOptionalJobParamKeys()) {
                TextField paramField = new TextField();
                paramField.setRequired(false);
                paramField.setLabel(key);
                paramField.setWidthFull();
                formLayout.add(paramField);
                optionalTextFields.put(key, paramField);
            }
        }

        Button startButton = new Button("Start", clickEvent -> {
            HashMap<String, String> params = new HashMap<>();
            for (Map.Entry<String, TextField> field:requiredTextFields.entrySet()) {
                if (StringUtils.isNotEmpty(field.getValue().getValue())) {
                    params.put(field.getKey(), field.getValue().getValue());
                }
                // TODO: Add early validation of required fields
            }
            for (Map.Entry<String, TextField> field:optionalTextFields.entrySet()) {
                if (StringUtils.isNotEmpty(field.getValue().getValue())) {
                    params.put(field.getKey(), field.getValue().getValue());
                }
            }
            dialog.close();
            doStartJob(job, params);

        });
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", clickEvent -> dialog.close());
        HorizontalLayout hl = new HorizontalLayout();
        hl.add(startButton);
        hl.add(cancelButton);
        formLayout.add(hl);
        dialog.add(formLayout);

        return dialog;
    }
}

