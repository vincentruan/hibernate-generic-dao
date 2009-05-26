package sample.trg.oldworld.web;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sample.trg.oldworld.model.Citizen;
import sample.trg.oldworld.service.CitizenService;
import sample.trg.oldworld.service.TownService;
import sample.trg.oldworld.webhelps.Util;

@Controller
@RequestMapping("/citizen/edit")
public class CitizenForm {
	CitizenService citizenService;
	
	@Autowired
	public void setCitizenService(CitizenService citizenService) {
		this.citizenService = citizenService;
	}
	
	TownService townService;
	
	@Autowired
	public void setTownService(TownService townService) {
		this.townService = townService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public Citizen setupForm(@RequestParam(value="id", required=false) Long id) {
		if (id != null)
			return citizenService.findById(id);
		else
			return new Citizen();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute("citizen") Citizen citizen, HttpServletRequest request) {
		citizenService.save(citizen);
		return Util.addSearchParamsToURL("redirect:/citizen/list.do", request.getParameterMap(), true, true, true);
	}
	
	@ModelAttribute("towns")
	public Collection<Map<String,Object>> populatePetTypes() {
		return townService.findAllWithForDropdown();
	}
}
