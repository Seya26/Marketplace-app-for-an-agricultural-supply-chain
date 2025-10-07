package it.cs.unicam.ids.filiera.demo.repositories;

import it.cs.unicam.ids.filiera.demo.entity.eventi.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByCreatoreId(Long id);

    List<Evento> findByPartecipantiId(Long id);


}
