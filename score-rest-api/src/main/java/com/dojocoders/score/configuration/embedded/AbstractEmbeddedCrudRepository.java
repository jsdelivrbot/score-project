package com.dojocoders.score.configuration.embedded;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractEmbeddedCrudRepository<V, K extends Serializable> implements ElasticsearchRepository<V, K> {

	private Map<K, V> values = Maps.newHashMap();

	protected abstract K getId(V entity);

	@Override
	public <S extends V> S save(S entity) {
		values.put(getId(entity), entity);
		return entity;
	}

	@Override
	public Optional<V> findById(K id) {
		return Optional.ofNullable(values.get(id));
	}

	@Override
	public Iterable<V> findAll() {
		return values.values();
	}

	@Override
	public long count() {
		return values.size();
	}

	public void delete(K id) {
		values.remove(id);
	}

	@Override
	public void delete(V entity) {
		delete(getId(entity));
	}

	@Override
	public void deleteAll() {
		values.clear();
	}

	@Override
	public Iterable<V> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<V> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends V> S index(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<V> search(QueryBuilder query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<V> search(QueryBuilder query, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<V> search(SearchQuery searchQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<V> searchSimilar(V entity, String[] fields, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<V> getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends V> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(K id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<V> findAllById(Iterable<K> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteById(K id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends V> entities) {
		// TODO Auto-generated method stub
		
	}

}
