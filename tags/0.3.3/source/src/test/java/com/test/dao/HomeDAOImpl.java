package com.test.dao;

import org.springframework.stereotype.Repository;

import com.test.model.Home;
import com.trg.dao.hibernate.GenericDAOImpl;

@Repository
public class HomeDAOImpl extends GenericDAOImpl<Home, Long> implements HomeDAO {

}
