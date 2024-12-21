
package com.lab6.laboratorio6.repository;

import com.lab6.laboratorio6.model.Eventos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Eventos, Integer> {
    
}

