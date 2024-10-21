package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phase;
import com.example.newdemo.Entity.State;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class PhaseForm extends FormLayout {
    public ComboBox<State> state = new ComboBox<>("State");
    public ComboBox<City> city = new ComboBox<>("City");
    public TextField name = new TextField("Name");
    public TextField phaseId = new TextField("Phase Id");
    Button save = new Button("Save");
    Button cancel = new Button("Discard Changes");
    public Button delete = new Button("Delete");
    Binder<Phase> phasesBinder = new Binder<>(Phase.class);

    public PhaseForm(List<State> states, List<City> cities){
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        phasesBinder.bindInstanceFields(this);

        FormLayout phaseFormLayout = new FormLayout(state, city, name, phaseId);
        phaseFormLayout.setResponsiveSteps(new ResponsiveStep("0", 2));
        phaseFormLayout.setSizeFull();

        phaseFormLayout.addClassName("custom-dialog");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(phaseFormLayout);
        mainLayout.addClassName("phrase-main-layout");
        add(mainLayout);
        save.addClassName("phase-save");
    }

    public HorizontalLayout buttonLayout() {
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new PhaseForm.DeleteEvent(this, phasesBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new PhaseForm.CloseEvent(this)));

        save.addClassName("custom-save-button");
        delete.addClassName("custom-delete-button");
        cancel.addClassName("custom-discard-button");

        phasesBinder.addStatusChangeListener(event -> save.setEnabled(phasesBinder.isValid()));

        HorizontalLayout layout = new HorizontalLayout();

        layout.add(cancel);

        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        HorizontalLayout newHorizontalLayout =  new HorizontalLayout(layout, save, delete);
        newHorizontalLayout.setWidthFull();
        newHorizontalLayout.setFlexGrow(2, layout);


        return newHorizontalLayout;
    }

    private void validateAndSave(){
        if(phasesBinder.isValid()){
            fireEvent(new PhaseForm.SaveEvent(this, phasesBinder.getBean()));
        }
    }

    public void setPhases(Phase phrases){
        phasesBinder.setBean(phrases);
    }

    public static abstract class PhraseFormEvent extends ComponentEvent<PhaseForm> {
        private final Phase phases;
        protected PhraseFormEvent(PhaseForm source, Phase phrases){
            super(source, false);
            this.phases = phrases;
        }
        public Phase getPhases(){
            return phases;
        }
    }


    public static class SaveEvent extends PhraseFormEvent {
        SaveEvent(PhaseForm source, Phase phrases){
            super(source, phrases);
        }
    }

    public static class DeleteEvent extends PhraseFormEvent{
        DeleteEvent(PhaseForm source, Phase phrases){
            super(source, phrases);
        }
    }

    public static class CloseEvent extends PhraseFormEvent {
        CloseEvent(PhaseForm source){
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener){
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener){
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener){
        return addListener(CloseEvent.class, listener);
    }
}
