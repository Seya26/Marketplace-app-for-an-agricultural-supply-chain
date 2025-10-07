package it.cs.unicam.ids.filiera.demo.repositories;

import it.cs.unicam.ids.filiera.demo.entity.Ordine;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Long> {

    @EntityGraph(attributePaths = {"righe", "righe.prodotto"})
    List<Ordine> findByAcquirenteId(Long acquirenteId);



}
