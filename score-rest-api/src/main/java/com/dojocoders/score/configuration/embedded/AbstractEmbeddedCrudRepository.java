package com.dojocoders.score.configuration.embedded;

import org.springframework.data.repository.CrudRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

abstract public class AbstractEmbeddedCrudRepository<V, K extends Serializable> implements CrudRepository<V, K> {

	private Map<K, V> values = Maps.newHashMap();

	protected abstract K getId(V entity);

	@Override
	public <S extends V> S save(S entity) {
		values.put(getId(entity), entity);
		return entity;
	}

	@Override
	public <S extends V> Iterable<S> save(Iterable<S> entities) {
		List<S> persistedEntities = Lists.newArrayList();
		for(S entity : entities) {
			persistedEntities.add(save(entity));
		}
		return persistedEntities;
	}

	@Override
	public V findOne(K id) {
		return values.get(id);
	}

	@Override
	public boolean exists(K id) {
		return values.containsKey(id);
	}

	@Override
	public Iterable<V> findAll() {
		return values.values();
	}

	@Override
	public Iterable<V> findAll(Iterable<K> ids) {
		List<V> result = Lists.newArrayList();
		for(K id : ids) {
			V value = values.get(id);
			if(value != null) {
				result.add(value);
			}
		}
		return result;
	}

	@Override
	public long count() {
		return values.size();
	}

	@Override
	public void delete(K id) {
		values.remove(id);
	}

	@Override
	public void delete(V entity) {
		delete(getId(entity));
	}

	@Override
	public void delete(Iterable<? extends V> entities) {
		for(V entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		values.clear();
	}

}
