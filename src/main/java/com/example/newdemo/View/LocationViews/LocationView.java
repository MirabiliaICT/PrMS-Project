package com.example.newdemo.View.LocationViews;

import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PhaseService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "location", layout = MainView.class)
@CssImport("/generated/locationView.css")
public class LocationView extends VerticalLayout {

    private final VerticalLayout content = new VerticalLayout();
    private final StateService stateService;
    private final CityService cityService;
    private final PhaseService phaseService;

    @Autowired
    public LocationView(StateService stateService, CityService cityService, PhaseService phaseService) {
        this.stateService = stateService;
        this.cityService = cityService;
        this.phaseService = phaseService;

        Tab stateTab = new Tab("State");
        Tab cityTab = new Tab("City");
        Tab phaseTab = new Tab("Phase");

        stateTab.addClassName("location-items");
        cityTab.addClassName("location-items");
        phaseTab.addClassName("location-items");

        Tabs tabs = new Tabs(stateTab, cityTab, phaseTab);
        tabs.addClassName("location-tabs");

        HorizontalLayout display = new HorizontalLayout(tabs);
        display.addClassName("location-navbar");

        content.setSizeFull();
        content.setPadding(false);
        setPadding(false);

        addClassName("location-view");
        setSizeFull();

        add(tabs, content);

        tabs.setSelectedTab(stateTab);
        setContent(stateTab);
        setTabStyles(stateTab, stateTab, cityTab, phaseTab);


        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            setTabStyles(selectedTab, stateTab, cityTab, phaseTab);
            setContent(selectedTab);
        });
    }

    private void setTabStyles(Tab selectedTab, Tab... tabs) {
        for (Tab tab : tabs) {
            if (tab == selectedTab) {
                tab.getStyle().set("color", "#ffffff");
            } else {
                tab.getStyle().remove("color");
            }
        }
    }

    private void setContent(Tab selectedTab) {
        content.removeAll();

        if ("State".equals(selectedTab.getLabel())) {
            content.add(new StateView(stateService));
        } else if ("City".equals(selectedTab.getLabel())) {
            content.add(new CityView(cityService, stateService));
        } else if ("Phase".equals(selectedTab.getLabel())) {
            content.add(new PhaseView(stateService, cityService, phaseService));
        }
    }
}

