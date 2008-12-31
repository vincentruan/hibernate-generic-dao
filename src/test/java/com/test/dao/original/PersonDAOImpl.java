package com.test.dao.original;

import org.springframework.stereotype.Repository;

import com.test.model.Person;
import com.trg.dao.dao.original.GenericDAOImpl;

@Repository
public class PersonDAOImpl extends GenericDAOImpl<Person, Long> implements PersonDAO {

}
