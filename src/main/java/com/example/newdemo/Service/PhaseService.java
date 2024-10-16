package com.example.newdemo.Service;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phase;
import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.PhaseRepository;
import com.example.newdemo.Repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhaseService {
    PhaseRepository phaseRepository;
    PropertyRepository propertyRepository;

    public PhaseService(PhaseRepository phaseRepository, PropertyRepository propertyRepository)
    {
        this.phaseRepository = phaseRepository;
        this.propertyRepository = propertyRepository;
    }

    public  void savePhases(Phase phrases){
        phaseRepository.save(phrases);
    }

    public List<Phase> getAllPhasesByFilter(String filter){
        if(filter == null || filter.isEmpty()){
            return phaseRepository.findAll();
        } else{
            return phaseRepository.search(filter);
        }
    }
    public void deletePhases(Phase phrases){
        phaseRepository.delete(phrases);
    }

    public List<Phase> getAllPhases(){
        return  phaseRepository.findAll();
    }

    public List<Phase> getAllPhasesByCity(City city){
        return phaseRepository.findByCity(city);
    }

    public List<Phase> findAllPhasesOfPropertyByUser(Users user){
        return propertyRepository.findAllPhrasesByUser(user);
    }
}
