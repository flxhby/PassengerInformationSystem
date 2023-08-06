package eu.derzauberer.pis.controller.web.studio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.derzauberer.pis.dto.PageDto;
import eu.derzauberer.pis.model.Station;
import eu.derzauberer.pis.service.StationService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/studio/stations")
public class StudioStationController {

	@Autowired
	private StationService stationService;
	
	@GetMapping
	public String getStationsPage(Model model, 
			@RequestParam(name = "query", required = false) String query,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "pageSize", defaultValue = "100") int pageSize
			) {
		if (query != null && !query.isEmpty()) {
			final String serach = query.replace('+', ' ');
			model.addAttribute("page", new PageDto<>(stationService.searchByName(serach), page, pageSize));
		} else {			
			model.addAttribute("page", new PageDto<Station>(stationService, page, pageSize));
		}
		return "/studio/stations.html";
	}
	
	@GetMapping("/export")
	public void exportStations(Model model, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
		final String content = stationService.exportEntities();
		response.setContentType("application/octet-stream");
		final String headerKey = "Content-Disposition";
		final String headerValue = "attachment; filename = " + stationService.getName() + ".json";
		response.setHeader(headerKey, headerValue);
		final ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(content.getBytes("UTF-8"));
		outputStream.close();
	}
	
	@GetMapping("/edit")
	public String editStation(@RequestParam(value = "id", required = false) String id, Model model) {
		stationService.getById(id).ifPresent(station -> {
			model.addAttribute("station", station);
		});
		return "/studio/edit/station.html";
	}
	
}
