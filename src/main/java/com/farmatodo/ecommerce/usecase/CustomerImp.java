package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.entity.CustomerEntity;
import com.farmatodo.ecommerce.enums.ERecordStatus;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerImp {

    private final CustomerRepository repository;

    public CustomerImp(CustomerRepository repository) { this.repository = repository; }

    @Transactional
    public CustomerEntity create(CustomerEntity e){
        if (repository.existsByEmail(e.getEmail()))  throw new BusinessException("El email ingresado ya existe.");
        if (repository.existsByPhone(e.getPhone()))  throw new BusinessException("El telefono ingresado ya existe.");
        e.setStatus(ERecordStatus.ACTIVE);
        return repository.save(e);
    }

    @Transactional(readOnly = true)
    public CustomerEntity get(Long id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Cliente no encontrado."));
    }

    @Transactional(readOnly = true)
    public Page<CustomerEntity> list(int page, int size){
        return repository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public CustomerEntity update(Long id, CustomerEntity patch){
        var db = repository.findById(id).orElseThrow(() -> new NotFoundException("Cliento no encontrado."));
        if (!db.getEmail().equals(patch.getEmail()) && repository.existsByEmailAndIdNot(patch.getEmail(), id))
            throw new BusinessException("El email ingresado ya existe.");
        if (!db.getPhone().equals(patch.getPhone()) && repository.existsByPhoneAndIdNot(patch.getPhone(), id))
            throw new BusinessException("El telefono ingresado ya existe.");

        db.setName(patch.getName());
        db.setEmail(patch.getEmail());
        db.setPhone(patch.getPhone());
        db.setAddress(patch.getAddress());
        return repository.save(db);
    }

    @Transactional
    public void delete(Long id){
        var db = repository.findById(id).orElseThrow(() -> new NotFoundException("Cliente no encontrado."));
        db.setStatus(ERecordStatus.DELETED);
        repository.save(db);
    }

}
