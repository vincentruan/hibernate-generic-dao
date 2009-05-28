package sample.trg.service;

import java.util.List;

import sample.trg.model.Citizen;


public interface CitizenService {

	public void persist(Citizen citizen);

	public List<Citizen> findAll();

	public Citizen findByName(String name);

	public void flush();
}