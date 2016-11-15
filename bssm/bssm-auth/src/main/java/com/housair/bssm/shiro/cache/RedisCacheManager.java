package com.housair.bssm.shiro.cache;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.housair.bssm.toolkit.redis.IRedisClient;
import com.housair.bssm.toolkit.util.JsonUtils;

/**
 * 
 * @author zhangkai
 *
 */
public class RedisCacheManager implements CacheManager {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);

    @SuppressWarnings("rawtypes")
	private final ConcurrentMap<String, Cache> caches;
    
    private String namespace = "shiro.cahce";
    
    private String shiroCacheKey = "shiro.cache.default";
    
    private IRedisClient redisClient;
    
    public RedisCacheManager() {
    	caches = Maps.newConcurrentMap();
	}
    
    public RedisCacheManager(IRedisClient redisClient) {
    	this();
    	this.redisClient = redisClient;
	}
    
    public RedisCacheManager(IRedisClient redisClient, String namespace) {
    	this(redisClient);
    	this.namespace = namespace;
	}

    public RedisCacheManager(IRedisClient redisClient, String namespace, String shiroCacheKey) {
    	this(redisClient, namespace);
    	this.shiroCacheKey = shiroCacheKey;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setShiroCacheKey(String shiroCacheKey) {
		this.shiroCacheKey = shiroCacheKey;
	}

	public void setRedisClient(IRedisClient redisClient) {
		this.redisClient = redisClient;
	}

	@Override
    @SuppressWarnings("unchecked")
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Cache name cannot be null or empty.");
        }
        Cache<K, V> cache = caches.get(name);
        if (cache == null) {
            cache = createCache(name);
			Cache<K, V> existing = caches.putIfAbsent(name, cache);
            if (existing != null) {
                cache = existing;
            }
        }
        return cache;
	}

	protected <K, V> Cache<K, V> createCache(String name) throws CacheException {
		Cache<K, V> cache = null;
		if(!caches.containsKey(name)) {
			cache = new RedisCache<K, V>();
			caches.put(name, cache);
		}
		return cache;
	}
	
	class RedisCache<K, V> implements Cache<K, V> {

		@SuppressWarnings("unchecked")
		@Override
		public V get(K key) throws CacheException {
			logger.debug("Get objects from Redis based on key. key [{}]", key);
			if(null == key) {
				return null;
			}
			byte[] value = redisClient.hget(shiroCacheKey, namespace, key.toString(), new TypeReference<byte[]>() {
			});
			return (V) SerializationUtils.deserialize(value);
		}

		@Override
		public V put(K key, V value) throws CacheException {
			logger.debug("Store data to redis. kye [{}], value [{}]", key, value);
			redisClient.hsetObject(shiroCacheKey, namespace, key.toString(), SerializationUtils.serialize(value));
			return value;
		}

		@Override
		public V remove(K key) throws CacheException {
			logger.debug("Delete from redis. key [{}]", key);
			V v = get(key);
			redisClient.hdel(shiroCacheKey, namespace, key.toString());
			return v;
		}

		@Override
		public void clear() throws CacheException {
			logger.debug("Clear cache from redis.");
			redisClient.del(shiroCacheKey, namespace);
		}

		@Override
		public int size() {
			logger.debug("Get cache size from redis.");
			return redisClient.hlen(shiroCacheKey, namespace).intValue();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Set<K> keys() {
			logger.debug("Get keys from redis.");
			return (Set<K>) redisClient.hkeys(shiroCacheKey, namespace);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Collection<V> values() {
			logger.debug("Get values from redis.");
			List<V> values = Lists.newArrayList();
			List<String> result = redisClient.hvals(shiroCacheKey, namespace);
			for (int i = 0; null != result && i < result.size(); i++) {
				byte[] bytes = JsonUtils.toObject(result.get(i), new TypeReference<byte[]>() {
				});
				values.add((V) SerializationUtils.deserialize(bytes));
			}
			return values;
		}

	}

}
