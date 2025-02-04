package eu.derzauberer.pis.repositories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.derzauberer.pis.configuration.SpringConfiguration;
import eu.derzauberer.pis.model.Entity;
import eu.derzauberer.pis.util.ProgressStatus;

public abstract class EntityRepository<T extends Entity<T>> {
	
	private final String name;
	private final Class<T> type;
	private final Logger logger;
	
	protected static final String DIRECTORY = "data/entities";
	protected static final String FILE_TYPE = ".json";
	private static final ObjectMapper OBJECT_MAPPER = SpringConfiguration.getBean(ObjectMapper.class);
	
	public EntityRepository(String name, Class<T> type, Logger logger) {
		this.name = name;
		this.type = type;
		this.logger = logger;
		try {
			Files.createDirectories(Paths.get(DIRECTORY, name));
		} catch (IOException exception) {
			logger.error("Couldn't create directory {}: {} {}", DIRECTORY + "/" + name, exception.getClass().getSimpleName(), exception.getMessage());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public abstract void add(T entity);
	
	public abstract boolean removeById(String id);
	
	public abstract boolean containsById(String id);
	
	public abstract Optional<T> getById(String id);
	
	public abstract List<T> getAll();
	
	public abstract List<T> getRange(int beginn, int end);
	
	public abstract int size();
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public String exportEntities() {
		try {
			final Collection<T> entities = getAll();
			final HashMap<String, Collection<T>> types = new HashMap<>();
			types.put(name, entities);
			String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(types);
			logger.info("Exported {} {}", entities.size(), name);
			return json;
		} catch (JsonProcessingException exception) {
			logger.error("Couldn't package {}: {} {}",  name, exception.getClass().getSimpleName(), exception.getMessage());
			return null;
		}
	}
	
	public void importEntities(String json) {
		try {
			final ObjectNode jsonTypes = new ObjectMapper().readValue(json, ObjectNode.class);
			final ArrayNode jsonEntities = (ArrayNode) jsonTypes.get(name);
			int i = 0;
			if (jsonEntities != null) {
				for (JsonNode jsonEntity : jsonEntities) {
					final T entity = OBJECT_MAPPER.readValue(jsonEntity.toString(), type);
					add(entity);
					i++;
				}
			}
			logger.info("Imported {} {}", i, name);
		} catch (IOException exception) {
			logger.error("Couldn't extract {}: {} {}", name, exception.getClass().getSimpleName(), exception.getMessage());
		}
	}
	
	protected List<T> loadEntities() {
		return loadEntities(false);
	}
	
	protected List<T> loadEntities(boolean progress) {
		try {
			final List<T> entities = new ArrayList<>();
			final ProgressStatus progressStatus = new ProgressStatus(name, new File(DIRECTORY, getName()).list().length);
			for (Path path : Files.list(Paths.get(DIRECTORY, name)).toList()) {
				if (!Files.exists(path)) continue;
				final String content = Files.readString(path);
				final T entity = OBJECT_MAPPER.readValue(content, type);
				entities.add(entity);
				if (progress) progressStatus.count();
			}
			return entities;
		} catch (IOException exception) {
			logger.error("Couldn't load entities from {}: {} {}!", getName(), exception.getClass().getSimpleName(), exception.getMessage());
			return new ArrayList<>();
		}
	}
	
	protected List<T> loadEntitiesInRange(int beginn, int end) {
		try {
			final List<T> entities = new ArrayList<>();
			for (Path path : Files.list(Paths.get(DIRECTORY, name)).toList().subList(beginn, end)) {
				if (!Files.exists(path)) continue;
				final String content = Files.readString(path);
				final T entity = OBJECT_MAPPER.readValue(content, type);
				entities.add(entity);
			}
			return entities;
		} catch (IOException exception) {
			logger.error("Couldn't load entities from {}: {} {}!", getName(), exception.getClass().getSimpleName(), exception.getMessage());
			return new ArrayList<>();
		}
	}
	
	
	protected boolean containsEntity(String id) {
		final Path path = Paths.get(DIRECTORY, name, id + FILE_TYPE);
		return Files.exists(path);
	}
	
	protected Optional<T> loadEntity(String id) {
		try {
			final Path path = Paths.get(DIRECTORY, name, id + FILE_TYPE);
			if (!Files.exists(path)) return Optional.empty();
			final String content = Files.readString(path);
			final T entity = OBJECT_MAPPER.readValue(content, type);
			return Optional.of(entity);
		} catch (IOException exception) {
			logger.error("Couldn't load entity with id {} from {}: {} {}!", id, getName(), exception.getClass().getSimpleName(), exception.getMessage());
			return Optional.empty();
		}
	}
	
	protected void saveEntity(T entity) {
		if (entity.getId() == null || entity.getId().isEmpty()) {
			throw new IllegalArgumentException("Entity id must be not null and not empty!");
		}
		try {
			final Path path = Paths.get(DIRECTORY, name, entity.getId() + FILE_TYPE);
			final String content = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
			Files.writeString(path, content);
		} catch (IOException exception) {
			logger.warn("Couldn't save entity with id {} from {}: {} {}", entity.getId(), getName(), exception.getClass().getSimpleName(), exception.getMessage());
		}
	}
	
	protected boolean deleteEnity(String id) {
		try {
			return Files.deleteIfExists(Paths.get(DIRECTORY, name, id + FILE_TYPE));
		} catch (IOException exception) {
			logger.warn("Couldn't delete entity with id {} from {}: {} {}", id, getName(), exception.getClass().getSimpleName(), exception.getMessage());
		}
		return false;
	}
	
}
