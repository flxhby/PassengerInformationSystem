package eu.derzauberer.pis.controller.api;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import eu.derzauberer.pis.model.LineLiveData;
import eu.derzauberer.pis.service.LineService;

@RestController
@RequestMapping("/api/lines")
public class ApiLineController {
	
	@Autowired
	private LineService lineService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping("{id}")
	public LineLiveData getLine(@PathVariable("id") String id) {
		return lineService.getById(id).orElseThrow(() -> getNotFoundException(id));
	}
	
	@PostMapping
	public LineLiveData setLine(LineLiveData line) {
		lineService.add(line);
		return line;
	}
	
	@PutMapping
	public LineLiveData updateLine(LineLiveData line) {
		final LineLiveData existingLine = lineService.getById(line.getId()).orElseThrow(() -> getNotFoundException(line.getId()));
		modelMapper.map(line, existingLine);
		return existingLine;
	}
	
	@DeleteMapping("{id}")
	public void deleteLine(@PathVariable("id") String id) {
		if (!lineService.removeById(id)) throw getNotFoundException(id);
	}
	
	private ResponseStatusException getNotFoundException(String id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Line with id " + id + " does not exist!");
	}

}
