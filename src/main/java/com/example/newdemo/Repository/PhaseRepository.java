package com.example.newdemo.Repository;


import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phase;
import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {

    @Query("select c from Phase c where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<Phase> search(@Param("searchTerm") String filter);

    @Query("SELECT c FROM Phase c WHERE (:state is null or c.state = :state)" +
        "AND (:city is null or c.city = :city)")
    List<Phase> searchByStateAndCity(@Param("state") State state,
                                     @Param("city") City city);

    Optional<Phase> findByName(String name);
    Optional<Phase> findByPhaseId(String phaseId);

    @Query("SELECT p FROM Phase p WHERE p.city = :city")
    List<Phase> findByCity(@Param("city") City city);
}
