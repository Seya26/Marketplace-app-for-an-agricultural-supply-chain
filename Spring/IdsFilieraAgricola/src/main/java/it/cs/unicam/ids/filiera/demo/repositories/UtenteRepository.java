package it.cs.unicam.ids.filiera.demo.repositories;

import it.cs.unicam.ids.filiera.demo.entity.UtenteVerificato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<UtenteVerificato, Long> {

    Optional<UtenteVerificato> findByEmail(String email);
    @Query("""
    SELECT u
    FROM UtenteVerificato u
    WHERE type(u) IN (Produttore, Trasformatore, Distributore)
    """)
    List<UtenteVerificato> findAllVenditori();

    @Query("""
            SELECT u
            FROM UtenteVerificato u
            WHERE type(u) IN (Curatore)
            """)
    List<UtenteVerificato> findAllCuratori();

    @Query("""
            SELECT u
            FROM UtenteVerificato u
            WHERE type(u) IN (Animatore)
            """)
    List<UtenteVerificato> findAllAnimatore();

}
