package com.housair.bssm.toolkit.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.housair.bssm.toolkit.util.JsonUtils;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.util.Pool;

/**
 * 
 * @author zhangkai
 * @version 1.0.0
 */
public abstract class DefaultRedisClientImpl implements IRedisClient {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRedisClientImpl.class);

	private static final String DOT = ".";
	
	interface Callback<R> {
		R invoke(JedisCommands jedis);
	}
	
	protected abstract Pool<?> getRedisPool();
	
	protected abstract <R> R execute(Callback<R> callBack);
	
	protected String getKeyByNamespace(String key, String namespace) {
		if (!StringUtils.isEmpty(namespace)) {
			return namespace + DOT + key;
		}
		return key;
	}
	
	protected String[] getKeyByNamespace(String[] keys, String namespace) {
		String[] paramByte = null;
		if (keys != null && keys.length > 0) {
			paramByte = new String[keys.length];
			for (int i = 0; i < keys.length; i++) {
				paramByte[i] = getKeyByNamespace(keys[i], namespace);
			}
		}
		return paramByte;
	}

	protected List<String> getKeyByNamespace(List<String> keys, String namespace) {
		List<String> _keys = new ArrayList<String>();
		String _key = null;
		for (String key : keys) {
			_key = getKeyByNamespace(key, namespace);
			_keys.add(_key);
		}
		return _keys;
	}
	
	public String[] getKeyByNamespace(Map<String, String> keyMap, String namespace) {
		String[] paramByte = null;
		if (keyMap != null && keyMap.size() > 0) {
			paramByte = new String[keyMap.size()];
			Iterator<Entry<String, String>> it = keyMap.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				paramByte[index++] = getKeyByNamespace(entry.getKey(), namespace);
			}
		}
		return paramByte;
	}
	
	public String[] smapToArray(Map<String, String> keyValues, String namespace) {
		String[] paramByte = null;
		if (keyValues != null && keyValues.size() > 0) {
			paramByte = new String[keyValues.size() * 2];
			Iterator<Entry<String, String>> it = keyValues.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				paramByte[index++] = getKeyByNamespace(entry.getKey(), namespace);
				paramByte[index++] = entry.getValue();
			}
		}
		return paramByte;
	}
	
	
	@Override
	public String set(String key, String namespace, final String value, int expireTime) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.set(nkey, value);
			}
			
		});
	}

	@Override
	public String set(String key, String namespace, final String value, final String nxxx, final String expx, final long expireTime) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.set(nkey, value, nxxx, expx, expireTime);
			}
		});
	}

	@Override
	public String get(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.get(nkey);
			}
		});
	}

	@Override
	public <T> T get(String key, String namespace, final TypeReference<T> type) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<T>() {
			@Override
			public T invoke(JedisCommands jedis) {
				String result = jedis.get(nkey);
				if(!StringUtils.isEmpty(result)) {
					return JsonUtils.toObject(result, type);
				}
				return null;
			}
		});
	}

	@Override
	public Boolean exists(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.exists(nkey);
			}
		});
	}

	@Override
	public String type(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.type(nkey);
			}
		});
	}

	@Override
	public Long expire(String key, String namespace, final int seconds) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.expire(nkey, seconds);
			}
		});
	}

	@Override
	public Long expireAt(String key, String namespace, final long unixTime) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.expireAt(nkey, unixTime);
			}
		});
	}

	@Override
	public Long ttl(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.ttl(nkey);
			}
		});
	}

	@Override
	public Boolean setbit(String key, String namespace, final long offset, final boolean value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.setbit(nkey, offset, value);
			}
		});
	}

	@Override
	public Boolean getbit(String key, String namespace, final long offset) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.getbit(nkey, offset);
			}
		});
	}

	@Override
	public Long setrange(String key, String namespace, final long offset, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.setrange(nkey, offset, value);
			}
		});
	}

	@Override
	public String getrange(String key, String namespace, final long startOffset, final long endOffset) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.getrange(nkey, startOffset, endOffset);
			}
		});
	}

	@Override
	public String getSet(String key, String namespace, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.getSet(nkey, value);
			}
		});
	}

	@Override
	public Long setnx(String key, String namespace, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.setnx(nkey, value);
			}
		});
	}

	@Override
	public <T> String setex(String key, String namespace, final int seconds, final T value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.setex(nkey, seconds, JsonUtils.toJson(value));
			}
		});
	}

	@Override
	public String setex(String key, String namespace, final int seconds, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.setex(nkey, seconds, value);
			}
		});
	}

	@Override
	public Long decrBy(String key, String namespace, final long integer) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.decrBy(nkey, integer);
			}
		});
	}

	@Override
	public Long decr(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.decr(nkey);
			}
		});
	}

	@Override
	public Long incrBy(String key, String namespace, final long integer) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.incrBy(nkey, integer);
			}
		});
	}

	@Override
	public Long incr(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.incr(nkey);
			}
		});
	}

	@Override
	public String substr(String key, String namespace, final int start, final int end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.substr(nkey, start, end);
			}
		});
	}

	@Override
	public Long hset(String key, String namespace, final String field, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.hset(nkey, field, value);
			}
		});
	}

	@Override
	public String hget(String key, String namespace, final String field) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.hget(nkey, field);
			}
		});
	}

	@Override
	public <T> T hget(String key, String namespace, final String field, final Class<T> type) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<T>() {
			@Override
			public T invoke(JedisCommands jedis) {
				String result = jedis.hget(nkey, field);
				if(!StringUtils.isEmpty(result)) {
					return JsonUtils.toObject(result, type);
				}
				return null;
			}
		});
	}

	@Override
	public <T> T hget(String key, String namespace, final String field, final TypeReference<T> type) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<T>() {
			@Override
			public T invoke(JedisCommands jedis) {
				String result = jedis.hget(nkey, field);
				if(!StringUtils.isEmpty(result)) {
					return JsonUtils.toObject(result, type);
				}
				return null;
			}
		});
	}

	@Override
	public Long hsetnx(String key, String namespace, final String field, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.hsetnx(nkey, field, value);
			}
		});
	}

	@Override
	public String hmset(String key, String namespace, final Map<String, String> hash) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.hmset(nkey, hash);
			}
		});
	}

	@Override
	public List<String> hmget(String key, String namespace, final String... fields) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.hmget(nkey, fields);
			}
		});
	}

	@Override
	public Long hincrBy(String key, String namespace, final String field, final long value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.hincrBy(nkey, field, value);
			}
		});
	}

	@Override
	public Boolean hexists(String key, String namespace, final String field) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.hexists(nkey, field);
			}
		});
	}

	@Override
	public Long hdel(String key, String namespace, final String... field) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.hdel(nkey, field);
			}
		});
	}

	@Override
	public Long hlen(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.hlen(nkey);
			}
		});
	}

	@Override
	public Set<String> hkeys(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.hkeys(nkey);
			}
		});
	}

	@Override
	public List<String> hvals(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.hvals(nkey);
			}
		});
	}

	@Override
	public <T> List<T> hvalsObject(String key, String namespace, Class<T> type) {
		final String nkey = getKeyByNamespace(key, namespace);
		List<String> vals = hvals(nkey, namespace);
		List<T> rtnList = null;
    	T rtn = null;
    	if(null != vals && vals.size() > 0){
    		rtnList = new ArrayList<T>();
    		for(String val : vals){
    			rtn = JsonUtils.toObject(val, type);
    			rtnList.add(rtn);
    		}
    	}
    	return rtnList;
	}

	@Override
	public Map<String, String> hgetAll(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Map<String, String>>() {
			@Override
			public Map<String, String> invoke(JedisCommands jedis) {
				return jedis.hgetAll(nkey);
			}
		});
	}

	@Override
	public Long llen(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.llen(nkey);
			}
		});
	}

	@Override
	public List<String> lrange(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.lrange(nkey, start, end);
			}
		});
	}

	@Override
	public String ltrim(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.ltrim(nkey, start, end);
			}
		});
	}

	@Override
	public String lindex(String key, String namespace, final long index) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.lindex(nkey, index);
			}
		});
	}

	@Override
	public String lset(String key, String namespace, final long index, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.lset(nkey, index, value);
			}
		});
	}

	@Override
	public Long lrem(String key, String namespace, final long count, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.lrem(nkey, count, value);
			}
		});
	}

	@Override
	public String lpop(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.lpop(nkey);
			}
		});
	}

	@Override
	public String rpop(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.rpop(nkey);
			}
		});
	}

	@Override
	public Long sadd(String key, String namespace, final String... member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.sadd(nkey, member);
			}
		});
	}

	@Override
	public Set<String> smembers(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.smembers(nkey);
			}
		});
	}

	@Override
	public Long srem(String key, String namespace, final String... member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.srem(nkey, member);
			}
		});
	}

	@Override
	public String spop(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.spop(nkey);
			}
		});
	}

	@Override
	public Long scard(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.scard(nkey);
			}
		});
	}

	@Override
	public Boolean sismember(String key, String namespace, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.sismember(nkey, member);
			}
		});
	}

	@Override
	public String srandmember(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.srandmember(nkey);
			}
		});
	}

	@Override
	public List<String> srandmember(String key, String namespace, final int count) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.srandmember(nkey, count);
			}
		});
	}

	@Override
	public Long zadd(String key, String namespace, final double score, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zadd(nkey, score, member);
			}
		});
	}

	@Override
	public Set<String> zrange(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrange(nkey, start, end);
			}
		});
	}

	@Override
	public Long zrem(String key, String namespace, final String... member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zrem(nkey, member);
			}
		});
	}

	@Override
	public Double zincrby(String key, String namespace, final double score, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Double>() {
			@Override
			public Double invoke(JedisCommands jedis) {
				return jedis.zincrby(nkey, score, member);
			}
		});
	}

	@Override
	public Long zrank(String key, String namespace, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zrank(nkey, member);
			}
		});
	}

	@Override
	public Long zrevrank(String key, String namespace, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zrevrank(nkey, member);
			}
		});
	}

	@Override
	public Set<String> zrevrange(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrevrange(nkey, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeWithScores(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrangeWithScores(nkey, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrevrangeWithScores(nkey, start, end);
			}
		});
	}

	@Override
	public Long zcard(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zcard(nkey);
			}
		});
	}

	@Override
	public Double zscore(String key, String namespace, final String member) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Double>() {
			@Override
			public Double invoke(JedisCommands jedis) {
				return jedis.zscore(nkey, member);
			}
		});
	}

	@Override
	public List<String> sort(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.sort(nkey);
			}
		});
	}

	@Override
	public List<String> sort(String key, String namespace, final SortingParams sortingParameters) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return jedis.sort(nkey, sortingParameters);
			}
		});
	}

	@Override
	public Long zcount(String key, String namespace, final double min, final double max) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zcount(nkey, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(String key, String namespace, final double min, final double max) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrangeByScore(nkey, min, max);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String namespace, final double max, final double min) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrevrangeByScore(nkey, min, max);
			}
		});
	}

	@Override
	public Set<String> zrangeByScore(String key, String namespace, final double min, final double max, final int offset, final int count) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrangeByScore(nkey, min, max, offset, count);
			}
		});
	}

	@Override
	public Set<String> zrevrangeByScore(String key, String namespace, final double max, final double min, final int offset, final int count) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				return jedis.zrevrangeByScore(nkey, min, max, offset, count);
			}
		});
	}

	@Override
	public Long zremrangeByRank(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zremrangeByRank(nkey, start, end);
			}
		});
	}

	@Override
	public Long zremrangeByScore(String key, String namespace, final double start, final double end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.zremrangeByScore(nkey, start, end);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String namespace, final double min, final double max) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrangeByScoreWithScores(nkey, min, max);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String namespace, final double max, final double min) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrevrangeByScoreWithScores(nkey, max, min);
			}
		});
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String namespace, final double min, final double max, final int offset,
			final int count) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrangeByScoreWithScores(nkey, max, min, offset, count);
			}
		});
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String namespace, final double max, final double min, final int offset,
			final int count) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<Tuple>>() {
			@Override
			public Set<Tuple> invoke(JedisCommands jedis) {
				return jedis.zrevrangeByScoreWithScores(nkey, max, min, offset, count);
			}
		});
	}

	@Override
	public Long linsert(String key, String namespace, final LIST_POSITION where, final String pivot, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.linsert(nkey, where, pivot, value);
			}
		});
	}

	@Override
	public Long del(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.del(nkey);
			}
		});
	}

	@Override
	public Long del(String[] keys, String namespace) {
		return 0L;
	}

	@Override
	public Long lpush(String key, String namespace, final String... fields) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.lpush(nkey, fields);
			}
		});
	}

	@Override
	public Long rpush(String key, String namespace, final String... fields) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.rpush(nkey, fields);
			}
		});
	}

	@Override
	public String mset(final Map<String, String> keyValues, final String namespace) {
		return null;
	}

	@Override
	public List<String> mget(String[] keys, String namespace) {
		return null;
	}

	@Override
	public Boolean setbit(String key, String namespace, final long offset, final String value) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Boolean>() {
			@Override
			public Boolean invoke(JedisCommands jedis) {
				return jedis.setbit(nkey, offset, value);
			}
		});
	}

	@Override
	public Long strlen(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.strlen(nkey);
			}
		});
	}

	@Override
	public String echo(final String string) {
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return jedis.echo(string);
			}
		});
	}

	@Override
	public Long bitcount(String key, String namespace) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.bitcount(nkey);
			}
		});
	}

	@Override
	public Long bitcount(String key, String namespace, final long start, final long end) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return jedis.bitcount(nkey, start, end);
			}
		});
	}

	@Override
	public <T> String set(String key, String namespace, final T value, final int time) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				String res = jedis.set(nkey, JsonUtils.toJson(value));
				if(time > 0) {
					Long r = jedis.expire(nkey, time);
					if(r != 1) {
						logger.error("key:" +  nkey + "expire exception!!!");
					}
				}
				return res;
			}
		});
	}

	@Override
	public <T> T get(String key, String namespace, final Class<T> type) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<T>() {
			@Override
			public T invoke(JedisCommands jedis) {
				return JsonUtils.toObject(jedis.get(nkey), type);
			}
		});
	}

	@Override
	public <T> Long hsetObject(String key, String namespace, final String field, final T value) {
		return hset(key, namespace, field, JsonUtils.toJson(value));
	}

	@Override
	public <T> T hgetObject(String key, String namespace, String field, Class<T> type) {
		String res = hget(key, namespace, field);
		if(!StringUtils.isEmpty(res)) {
			return JsonUtils.toObject(res, type);
		}
		return null;
	}

	@Override
	public <T> Map<String, T> hgetAllObjects(String key, String namespace, Class<T> type) {
		Map<String, String> mvals = hgetAll(key, namespace);
		Map<String, T> allObjs = new HashMap<String, T>();
		for (Entry<String, String> item : mvals.entrySet()) {
			String _key = item.getKey();
			T _value = JsonUtils.toObject(item.getValue(), type);
			allObjs.put(_key, _value);
		}
		return allObjs;
	}

	@Override
	public Object patternDel(String pattern, String namespace) {
		return null;
	}

	@Override
	public Object eval(final String script, final List<String> keys, String namespace, final List<String> args) {
		return null;
	}

	@Override
	public List<Entry<String, String>> hscan(String key, String namespace, String match) {
		return null;
	}

	@Override
	public Set<String> sscan(String key, String namespace, String match) {
		return null;
	}

	@Override
	public Pipeline pipelined() {
		return null;
	}

}
