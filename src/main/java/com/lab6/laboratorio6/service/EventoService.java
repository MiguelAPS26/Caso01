
package com.lab6.laboratorio6.service;

import com.lab6.laboratorio6.model.Eventos;
import com.lab6.laboratorio6.repository.EventoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoService {
    @Autowired
    private EventoRepository repository;

    public List<Eventos> listar() {
        return repository.findAll();
    }

    public Eventos guardar(Eventos evento) {
        return repository.save(evento);
    }

    public void eliminar(int id) {
        repository.deleteById(id);
    }

    public Eventos obtenerPorId(int id) {
        return repository.findById(id).orElse(null);
    }
}

