package it.cs.unicam.ids.filiera.demo.repositories;

import it.cs.unicam.ids.filiera.demo.entity.Bundle;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {

    List<Prodotto> findByVenditoreId(Long venditoreId);

    List<Prodotto> findByAttesaTrue();

    List<Prodotto> findByAttesaFalse();

    List<Prodotto> findByCategoria(String categoria);

    @Query("SELECT b FROM Bundle b LEFT JOIN FETCH b.items i LEFT JOIN FETCH i.prodotto WHERE b.id = :id")
    Optional<Bundle> findBundleWithProdotti(@Param("id") Long id);

    @Query("SELECT DISTINCT b FROM Bundle b LEFT JOIN FETCH b.items WHERE b.confermato = true")
    List<Bundle> findBundleConfermatiConProdotti();


    @Query("SELECT DISTINCT b FROM Bundle b LEFT JOIN FETCH b.items i LEFT JOIN FETCH i.prodotto")
    List<Bundle> findTuttiIBundleConProdotti();


    @Query("""
    SELECT b FROM Bundle b
    WHERE b.attesa = true AND b.venditoreId = :venditoreId
""")
    Optional<Bundle> findBundleInAttesaByVenditoreId(@Param("venditoreId") Long venditoreId);


    @Query("SELECT b FROM Bundle b LEFT JOIN FETCH b.items WHERE b.id = :id")
    Optional<Bundle> findBundleWithItems(@Param("id") Long id);

}