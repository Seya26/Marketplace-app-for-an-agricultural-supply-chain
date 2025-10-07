package it.cs.unicam.ids.filiera.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cs.unicam.ids.filiera.demo.entity.Prodotto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class Carrello implements Serializable {

	private final Map<Long, RigaCarrello> righe = new LinkedHashMap<>();

	public void aggiungi(Prodotto p, Long qty) {
		RigaCarrello r = righe.get(p.getId());
		if (r == null) {
			righe.put(p.getId(), new RigaCarrello(
					p.getId(),
					p.getNome(),
					p.getPrezzo(),
					qty
			));
		} else {
			r.setQuantita(r.getQuantita() + qty);
		}
	}

	public void svuota() { righe.clear(); }

	@JsonIgnore
	public boolean isVuoto() { return righe.isEmpty(); }

	public Collection<RigaCarrello> getRighe() { return righe.values(); }

	public BigDecimal getTotale() {
		return righe.values().stream()
				.map(r -> r.getPrezzoUnitario().multiply(BigDecimal.valueOf(r.getQuantita())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public static Carrello newCarrello() { return new Carrello(); }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Carrello{\n");
		righe.values().forEach(r ->
				sb.append(" - ").append(r.getNome())
						.append(" x ").append(r.getQuantita())
						.append(" @ ").append(r.getPrezzoUnitario())
						.append("\n"));
		sb.append("Totale: ").append(getTotale()).append(" }");
		return sb.toString();
	}

	public void rimuovi(Long prodottoId) {
		righe.remove(prodottoId);
	}


}
