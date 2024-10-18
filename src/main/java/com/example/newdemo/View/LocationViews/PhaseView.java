package com.example.newdemo.View.LocationViews;


import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phase;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Forms.PhaseForm;
import com.example.newdemo.Repository.PhaseRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PhaseService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@CssImport("/generated/phrase.css")
@Route(value = "phaseView", layout = MainView.class)
public class PhaseView extends VerticalLayout {

    Grid<Phase> phasesGrid = new Grid<>(Phase.class, false);
    TextField filterText = new TextField();
    PhaseForm phaseNewForm;
    Phase originalPhase;
    PhaseForm editPhaseForm;
    CityService cityService;
    StateService stateService;
    PhaseService service;
    Dialog newFormDialog = new Dialog();
    Dialog editDialog = new Dialog();
    ComboBox<State> stateComboBox = new ComboBox<>("State");
    ComboBox<City> cityComboBox = new ComboBox<>("City");
    Icon reloadIcon = VaadinIcon.REFRESH.create();
    Button resetFilterButton = new Button(reloadIcon);

    @Autowired
    PhaseRepository phraseRepository;

    @Autowired
    public PhaseView(StateService stateService, CityService cityService, PhaseService service, PhaseRepository phaseRepository){
        this.stateService = stateService;
        this.cityService =  cityService;
        this.service = service;
        this.phraseRepository = phaseRepository;

        addClassName("phase-view");

        editPhaseForm = new PhaseForm(stateService.getAllStates(), cityService.getAllCities());
        phaseNewForm = new PhaseForm(stateService.getAllStates(), cityService.getAllCities());
        phaseNewForm.setPhases(new Phase());
        phaseNewForm.state.setItems(sortedState());
        phaseNewForm.city.setItems(sortedCity());

        phaseNewForm.city.setEnabled(false);
        phaseNewForm.state.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListByState(selectedState);
            phaseNewForm.city.setEnabled(true);
        });

        phaseNewForm.delete.setVisible(false);
        phaseNewForm.addSaveListener(this::saveNew);
        phaseNewForm.addCloseListener(e -> closeNew());

        newFormDialog.setHeaderTitle("New Phase");
        newFormDialog.addClassName("custom-dialog");
        newFormDialog.getFooter().add(phaseNewForm.buttonLayout());
        newFormDialog.add(phaseNewForm);

//        HorizontalLayout display = getHorizontalLayout();

        setSizeFull();
        configureGrid();
        getToolbar();
        add(getToolbar(), phasesGrid);
        updateList();
    }

    private void saveNew(PhaseForm.SaveEvent e){
        String name = e.getPhases().getName();
        String id = e.getPhases().getPhaseId();

        Optional<Phase> phaseName = phraseRepository.findByName(name);
        Optional<Phase> phaseId = phraseRepository.findByPhaseId(id);

        if (name.isBlank() || id.isBlank()) {
            Notification.show("All fields are required", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        } else {
            if (phaseName.isPresent() && phaseId.isPresent()) {
                Notification.show("Phase and Phase Id already exist", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (phaseName.isPresent()) {
                Notification.show("Phase already exist", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else if (phaseId.isPresent()) {
                Notification.show("Phase Id already exist", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                service.savePhases(e.getPhases());
                closeNew();
            }
        }
    }
    private void closeNew() {
        phaseNewForm.state.clear();
        phaseNewForm.city.clear();
        phaseNewForm.city.setEnabled(false);
        phaseNewForm.name.clear();
        phaseNewForm.phaseId.clear();
        phaseNewForm.setPhases(new Phase());
        newFormDialog.close();
        updateList();
    }

    private HorizontalLayout getToolbar(){
        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.addClassName("custom-filter-text");
        Button addPhrase  = new Button("Add Phases", e -> newFormDialog.open());
        addPhrase.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        addPhrase.addClassName("custom-add-button");

        stateComboBox.setItems(sortedState());
        stateComboBox.setItemLabelGenerator(State::getName);

        cityComboBox.setItems(sortedCity());
        cityComboBox.setItemLabelGenerator(City::getName);

        stateComboBox.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListByState(selectedState);
        });
        cityComboBox.setEnabled(false);

        stateComboBox.addValueChangeListener(e ->{
            cityComboBox.setEnabled(true);
            performSearch();
        });

        cityComboBox.addValueChangeListener(e -> performSearch());

        resetFilterButton.addClickListener(clickEvent -> {
            stateComboBox.clear();
            cityComboBox.clear();
            cityComboBox.setEnabled(false);
            updateList();
        });

        resetFilterButton.addClassName("custom-reset-button");

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(resetFilterButton, addPhrase);
        layout.setJustifyContentMode(JustifyContentMode.END);

        HorizontalLayout horizontalLayout = new HorizontalLayout(filterText, stateComboBox, cityComboBox, layout);
        horizontalLayout.setWidthFull();
        horizontalLayout.addClassName("buttons-layout");
        horizontalLayout.setFlexGrow(2, layout);

        return horizontalLayout;
    }

    private void performSearch() {
        State selectedState = stateComboBox.getValue();
        City selectedCity = cityComboBox.getValue();

        List<Phase> searchResults = phraseRepository.searchByStateAndCity(selectedState, selectedCity);
        List<Phase> sortedPhrases = searchResults.stream()
                .sorted(Comparator.comparing(Phase::getName))
                .toList();
        phasesGrid.setItems(sortedPhrases);
    }

    private void getCityListByState(State state){
        List<City> cityByState = cityService.getAllCitiesByStateByList(state);
        phaseNewForm.city.setItems(cityByState);
        phaseNewForm.city.setItemLabelGenerator(City::getName);
        cityComboBox.setItems(cityByState);
        cityComboBox.setItemLabelGenerator(City::getName);
    }

    private void updateList(){
        if(filterText.isEmpty()){
            List<Phase> phrases = service.getAllPhasesByFilter(filterText.getEmptyValue());

            List<Phase> sortedPhrases = phrases.stream()
                    .sorted(Comparator.comparing(Phase::getName))
                    .toList();

            phasesGrid.setItems(sortedPhrases);
        } else{
            List<Phase> phrases = service.getAllPhasesByFilter(filterText.getValue());

            List<Phase> sortedPhrases = phrases.stream()
                    .sorted(Comparator.comparing(Phase::getName))
                    .toList();

            phasesGrid.setItems(sortedPhrases);
        }
    }

    private List<State> sortedState(){
        List<State> states = stateService.getAllStates();
        return states.stream()
                .sorted(Comparator.comparing(State::getName))
                .toList();
    }

    private List<City> sortedCity(){
        List<City> cities = cityService.getAllCities();
        return cities.stream()
                .sorted(Comparator.comparing(City::getName))
                .toList();
    }

    private void configureGrid(){
        phasesGrid.addColumn(phase -> phase.getState().getName()).setHeader("State").setSortable(true);
        phasesGrid.addColumn(phase -> phase.getCity().getName()).setHeader("City").setSortable(true);
        phasesGrid.addColumn(Phase::getName).setHeader("Phase").setSortable(true);
        phasesGrid.addColumn(Phase::getPhaseId).setHeader("Phase Id").setSortable(true);
        phasesGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        List<Phase> phrases = service.getAllPhasesByFilter(filterText.getEmptyValue());
        phasesGrid.setItems(phrases);
        phasesGrid.addItemClickListener(event -> editForm(event.getItem()));
        phasesGrid.addClassName("custom-grid");

    }

    private void editForm(Phase phrases){
        if(phrases != null){
            editPhaseForm.setPhases(phrases);

            if(originalPhase == null){
                originalPhase = new Phase();
            }

            originalPhase.setName(phrases.getName());
            originalPhase.setPhaseId(phrases.getPhaseId());

            editPhaseForm.addSaveListener(this::saveEdit);
            editPhaseForm.addDeleteListener(this::deleteEdit);
            editPhaseForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("Phrase");
            editDialog.addClassName("custom-dialog");
            editDialog.getFooter().add(editPhaseForm.buttonLayout());
            editDialog.add(editPhaseForm);
            editDialog.open();
        } else{
            closeEdit();
        }
    }

    private void saveEdit(PhaseForm.SaveEvent e) {
//        String name = e.getPhases().getName();
//        String id = e.getPhases().getPhaseId();
//
//        boolean nameChanged = !name.equals(originalPhase.getName());
//        boolean phraseIdChanged = !id.equals(originalPhase.getPhaseId());
//
//        Optional<Phase> phraseName = phraseRepository.findByName(name);
//        Optional<Phase> phraseId = phraseRepository.findByPhaseId(id);
//
//        if (nameChanged && phraseName.isPresent()) {
//            Notification.show("Phase already exist", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
//        } else if (phraseIdChanged && phraseId.isPresent()) {
//            Notification.show("Phase Id already exist", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
//        } else {
            service.savePhases(e.getPhases());
            updateList();
            closeEdit();
//        }
    }

    private void deleteEdit(PhaseForm.DeleteEvent e){
        service.deletePhases(e.getPhases());
        closeEdit();
    }

    private void closeEdit(){
        updateList();
        editDialog.close();
    }
}
