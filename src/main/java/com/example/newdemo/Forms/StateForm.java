package com.example.newdemo.Forms;

import com.example.newdemo.Entity.State;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PhaseService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@CssImport("/generated/locationView.css")
public class StateForm extends FormLayout {

    public TextField name = new TextField("Name");
    public TextField stateId = new TextField("State Id");

    private final CityService cityService;

    Button save = new Button("Save");
    public Button delete = new Button("Delete");
    Button cancel = new Button("Discard Changes");

    Binder<State> stateBinder = new Binder<>(State.class);

    @Autowired
    public StateForm(CityService cityService) {
        this.cityService = cityService;

        stateBinder.bindInstanceFields(this);
        FormLayout stateForm = new FormLayout(name, stateId);
        stateForm.setResponsiveSteps(new ResponsiveStep("0", 2));
        add(stateForm);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(stateForm);
        mainLayout.addClassName("city-main-layout");
        add(mainLayout);
        save.addClassName("phase-save");
    }

    public HorizontalLayout buttonLayout() {
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClassName("custom-save-button");
        delete.addClassName("custom-delete-button");
        cancel.addClassName("custom-discard-button");

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> checkAndDeleteState());
        cancel.addClickListener(clickEvent -> fireEvent(new StateForm.CloseEvent(this)));

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(cancel);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout newHorizontalLayout = new HorizontalLayout(layout, save, delete);
        newHorizontalLayout.setWidthFull();
        newHorizontalLayout.setFlexGrow(2, layout);

        return newHorizontalLayout;
    }

    private void checkAndDeleteState() {
        State state = stateBinder.getBean();
        boolean hasCities = !cityService.getAllCitiesByStateByList(state).isEmpty();
//        boolean hasPhases = !phaseService.getAllPhasesByCity(ci).isEmpty();

        if (hasCities) {
            Notification.show("Cannot delete the state as it has associated cities.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            deleteConfirmationDialog(state);
        }
    }

    private void deleteConfirmationDialog(State state) {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.add(new Span("Are you sure you want to delete this state?"));
        Button confirmButton = new Button("Confirm", event -> {
            fireEvent(new StateForm.DeleteEvent(this, state));
            confirmationDialog.close();
        });
        confirmButton.addClassName("custom-confirm-button");

        Button cancelButton = new Button("Cancel", event -> confirmationDialog.close());
        cancelButton.addClassName("custom-cancel-button");

        HorizontalLayout dialogButtons = new HorizontalLayout(confirmButton, cancelButton);
        dialogButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);


        confirmationDialog.add(dialogButtons);
        confirmationDialog.setCloseOnOutsideClick(false);
        confirmationDialog.setCloseOnEsc(true);

        confirmationDialog.open();
    }


    public void setState(State state) {
        stateBinder.setBean(state);
    }

    private void validateAndSave() {
        if (stateBinder.isValid()) {
            fireEvent(new SaveEvent(this, stateBinder.getBean()));
        }
    }

    @Getter
    public static abstract class StateFormEvent extends ComponentEvent<StateForm> {
        private final State state;

        protected StateFormEvent(StateForm source, State state) {
            super(source, false);
            this.state = state;
        }
    }

    public static class SaveEvent extends StateFormEvent {
        SaveEvent(StateForm source, State state) {
            super(source, state);
        }
    }

    public static class DeleteEvent extends StateFormEvent {
        DeleteEvent(StateForm source, State state) {
            super(source, state);
        }
    }

    public static class CloseEvent extends StateFormEvent {
        CloseEvent(StateForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
