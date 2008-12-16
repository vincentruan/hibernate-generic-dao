package com.test.dao;

import org.springframework.stereotype.Repository;

import com.test.model.Address;
import com.trg.dao.dao.original.GenericDAOImpl;

@Repository
public class AddressDAOImpl extends GenericDAOImpl<Address, Long> implements AddressDAO {

}
