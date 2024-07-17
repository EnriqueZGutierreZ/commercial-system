package com.elolympus.services.services;

import com.elolympus.services.repository.KardexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elolympus.data.Almacen.Kardex;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KardexService {
    private final KardexRepository repository;

    @Autowired
    public KardexService(KardexRepository kardexRepository) {
        this.repository = kardexRepository;
    }

    public List<Kardex> findAll() {
        return repository.findAll();
    }

    public Optional<Kardex> findById(Long id) {
        return repository.findById(id);
    }

    public Kardex save(Kardex kardex) {
        return repository.save(kardex);
    }

    public Kardex update(Kardex kardex) {
        return repository.save(kardex);
    }

    public void delete(Kardex kardex) {
        repository.delete(kardex);
    }

}
