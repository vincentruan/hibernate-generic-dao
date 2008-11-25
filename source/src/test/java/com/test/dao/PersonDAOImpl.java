package com.test.dao;

import org.springframework.stereotype.Repository;

import com.test.model.Person;
import com.trg.dao.hibernate.GenericDAOImpl;

@Repository
public class PersonDAOImpl extends GenericDAOImpl<Person, Long> implements PersonDAO {

}
