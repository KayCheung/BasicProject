package com.housair.bssm.toolkit.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.ShardedJedis;
import redis.clients.util.Pool;

public class ShardedRedisClientImpl extends DefaultRedisClientImpl {

private static final Logger logger = LoggerFactory.getLogger(RedisClientImpl.class);
	
	private Pool<ShardedJedis> redisPool;
	
	public ShardedRedisClientImpl() {
	}
	
	public ShardedRedisClientImpl(Pool<ShardedJedis> redisPool) {
		this.redisPool = redisPool;
	}
	
	public void setRedisPool(Pool<ShardedJedis> redisPool) {
		this.redisPool = redisPool;
	}

	@Override
	protected Pool<ShardedJedis> getRedisPool() {
		return this.redisPool;
	}

	@Override
	protected <R> R execute(Callback<R> callBack) {
		ShardedJedis redis = getRedisPool().getResource();
		try {
			return callBack.invoke(redis);
		} catch(Exception e) {
			logger.error("redis指令执行失败...", e);
			throw e;
		} finally {
			getRedisPool().returnResource(redis);
		}
	}

}
