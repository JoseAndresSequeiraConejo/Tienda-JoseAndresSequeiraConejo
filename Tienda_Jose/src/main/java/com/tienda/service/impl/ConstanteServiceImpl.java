package com.tienda.services.impl;

import com.tienda.dao.ConstanteDao;
import com.tienda.domain.Constante;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.service.ConstanteService;

@Service
public class ConstanteServiceImpl 
        implements ConstanteService {

    @Autowired
    private ConstanteDao constanteDao;
    
    @Override
    @Transactional(readOnly=true)
    public List<Constante> getConstantes() {
        var lista = constanteDao.findAll();
        return lista;
    }

    @Override
    @Transactional(readOnly=true)
    public Constante getConstante(Constante constante) {
        return constanteDao.findById(constante.getIdConstante()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Constante constante) {
        constanteDao.save(constante);
    }

    @Override
    @Transactional
    public void delete(Constante constante) {
        constanteDao.delete(constante);
    }    

    @Override
    @Transactional(readOnly=true)
    public Constante getConstantePorAtributo(String atributo) {
        return constanteDao.findByAtributo(atributo);
    }
}