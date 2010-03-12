package sample.trg.service;

import java.util.List;

import sample.trg.model.Town;


public interface TownService {

	public void persist(Town citizen);

	public List<Town> findAll();

	public Town findByName(String name);

}