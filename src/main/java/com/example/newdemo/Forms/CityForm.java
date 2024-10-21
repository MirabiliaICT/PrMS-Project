package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PhaseService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;

@CssImport("/generated/locationView.css")
public class CityForm extends FormLayout {

    public ComboBox<State> state = new ComboBox<>("State");
    public TextField name = new TextField("Name");
    public TextField cityId = new TextField("City Id");

    Button save = new Button("Save");
    Button cancel = new Button("Discard Changes");
    public Button delete = new Button("Delete");

    Binder<City> cityBinder = new Binder<>(City.class);

    PhaseService phaseService;

    public CityForm(List<State> states){
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        cityBinder.bindInstanceFields(this);

        FormLayout cityFormLayout = new FormLayout(state, name, cityId);
        cityFormLayout.setResponsiveSteps( new ResponsiveStep("0", 2));
        cityFormLayout.setSizeFull();
        cityFormLayout.addClassName("custom-dialog");
        state.setSizeFull();

        add(cityFormLayout);

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(cityFormLayout);
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
        delete.addClickListener(clickEvent -> fireEvent(new DeleteEvent(this, cityBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new CloseEvent(this)));

        cityBinder.addStatusChangeListener(event -> save.setEnabled(cityBinder.isValid()));

        HorizontalLayout layout = new HorizontalLayout();
        HorizontalLayout cancelWrapper = new HorizontalLayout();

        cancelWrapper.add(cancel);

        layout.add(cancelWrapper, save, delete);
        layout.setWidthFull();
        layout.setFlexGrow(2,cancelWrapper);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        return layout;
    }

    private void validateAndSave(){
        if(cityBinder.isValid()){
            fireEvent(new SaveEvent(this, cityBinder.getBean()));
        }
    }

    public void setCity(City city){
        cityBinder.setBean(city);
    }

//    @Getter
    public static abstract class CityFormEvent extends ComponentEvent<CityForm>{
        private final City city;
        protected CityFormEvent(CityForm source, City city){
            super(source, false);
            this.city = city;
        }
        public City getCity(){
            return city;
        }
    }

    public static class SaveEvent extends CityFormEvent {
        SaveEvent(CityForm source, City city){
            super(source, city);
        }
    }

    public static class DeleteEvent extends CityFormEvent {
        DeleteEvent( CityForm source, City city){
            super(source, city);
        }
    }

    public static class CloseEvent extends CityFormEvent{
        CloseEvent(CityForm source){
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
