package com.housair.bssm.toolkit.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.util.Pool;

public class RedisClientImpl extends DefaultRedisClientImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisClientImpl.class);
	
	private Pool<Jedis> redisPool;
	
	public RedisClientImpl() {
	}
	
	public RedisClientImpl(Pool<Jedis> redisPool) {
		this.redisPool = redisPool;
	}
	
	public void setRedisPool(Pool<Jedis> redisPool) {
		this.redisPool = redisPool;
	}

	@Override
	protected Pool<Jedis> getRedisPool() {
		return this.redisPool;
	}

	@Override
	protected <R> R execute(Callback<R> callBack) {
		Jedis redis = getRedisPool().getResource();
		try {
			return callBack.invoke(redis);
		} catch(Exception e) {
			logger.error("redis指令执行失败...", e);
			throw e;
		} finally {
			getRedisPool().returnResource(redis);
		}
	}
	
	@Override
	public Long del(String[] keys, String namespace) {
		final String[] nkeys = getKeyByNamespace(keys, namespace);
		return execute(new Callback<Long>() {
			@Override
			public Long invoke(JedisCommands jedis) {
				return ((Jedis) jedis).del(nkeys);
			}
		});
	}
	
	@Override
	public String mset(final Map<String, String> keyValues, final String namespace) {
		return execute(new Callback<String>() {
			@Override
			public String invoke(JedisCommands jedis) {
				return ((Jedis)jedis).mset(smapToArray(keyValues, namespace));
			}
		});
	}
	
	@Override
	public List<String> mget(String[] keys, String namespace) {
		final String[] nkeys = getKeyByNamespace(keys, namespace);
		return execute(new Callback<List<String>>() {
			@Override
			public List<String> invoke(JedisCommands jedis) {
				return ((Jedis)jedis).mget(nkeys);
			}
		});
	}
	
	@Override
	public List<Entry<String, String>> hscan(String key, String namespace, final String match) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<List<Entry<String, String>>>() {
			@Override
			public List<Entry<String, String>> invoke(JedisCommands jedis) {
				int cursor = 0;
				ScanParams scanParams = new ScanParams();
				scanParams.match(match);
				ScanResult<Map.Entry<String, String>> scanResult;
				List<Map.Entry<String, String>> res = new ArrayList<Map.Entry<String, String>>();
				do {
					scanResult = ((Jedis) jedis).hscan(nkey, String.valueOf(cursor), scanParams);
					res.addAll(scanResult.getResult());
					cursor = Integer.parseInt(scanResult.getStringCursor());
				} while (cursor > 0);
				return res;
			}
		});
	}
	
	@Override
	public Set<String> sscan(String key, String namespace, final String match) {
		final String nkey = getKeyByNamespace(key, namespace);
		return execute(new Callback<Set<String>>() {
			@Override
			public Set<String> invoke(JedisCommands jedis) {
				int cursor = 0;
				ScanParams scanParams = new ScanParams();
				scanParams.match(match);
				ScanResult<String> scanResult;
				Set<String> res = new HashSet<String>();
				do {
					scanResult = ((Jedis) jedis).sscan(nkey, String.valueOf(cursor), scanParams);
					res.addAll(scanResult.getResult());
					cursor = Integer.parseInt(scanResult.getStringCursor());
				} while (cursor > 0);
				return res;
			}
		});
	}

	@Override
	public Object patternDel(String pattern, String namespace) {
		final String patternKey = getKeyByNamespace(pattern, namespace);
		final String script = "local keys = redis.call('keys', '"+patternKey+"') \n for i=1,#keys,2000 do \n redis.call('del', unpack(keys, i, math.min(i+1999, #keys))) \n end \n return keys";
		return execute(new Callback<Object>() {
			@Override
			public Object invoke(JedisCommands jedis) {
				return ((Jedis)jedis).eval(script);
			}
		});
	}
	
	@Override
	public Object eval(final String script, final List<String> keys, String namespace, final List<String> args) {
		final List<String> nkeys = getKeyByNamespace(keys, namespace);
		return execute(new Callback<Object>() {
			@Override
			public Object invoke(JedisCommands jedis) {
				return ((Jedis) jedis).eval(script, nkeys, args);
			}
		});
	}
	
	@Override
	public Pipeline pipelined() {
		return execute(new Callback<Pipeline>() {
			@Override
			public Pipeline invoke(JedisCommands jedis) {
				return ((Jedis) jedis).pipelined(); 
			}
		});
	}

}
