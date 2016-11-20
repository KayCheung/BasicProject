package com.housair.bssm.shiro.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import com.housair.bssm.toolkit.redis.IRedisClient;

/**
 * 
 * @author zhangkai
 *
 */
public class RedisSessionDAO extends AbstractSessionDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDAO.class);
	
    private String namespace = "shiro.cahce";
    
    private String shiroSessionKey = "shiro.session.default";
    
    private IRedisClient redisClient;
    
    private long maxIdleTimeInMillis = 1800000;
    
    public RedisSessionDAO() {}
    
    public RedisSessionDAO(IRedisClient redisClient) {
    	this.redisClient = redisClient;
    }
    
    public RedisSessionDAO(IRedisClient redisClient, String namespace) {
    	this(redisClient);
    	this.namespace = namespace;
    }
    
    public RedisSessionDAO(IRedisClient redisClient, String shiroSessionKey, String namespace) {
    	this(redisClient, namespace);
    	this.shiroSessionKey = shiroSessionKey;
    }
    
    public RedisSessionDAO(IRedisClient redisClient, String shiroSessionKey, String namespace, long maxIdleTimeInMillis) {
    	this(redisClient, shiroSessionKey, namespace);
		this.maxIdleTimeInMillis = maxIdleTimeInMillis;
    }

	public void setMaxIdleTimeInMillis(long maxIdleTimeInMillis) {
		this.maxIdleTimeInMillis = maxIdleTimeInMillis;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setShiroSessionKey(String shiroSessionKey) {
		this.shiroSessionKey = shiroSessionKey;
	}

	public void setRedisClient(IRedisClient redisClient) {
		this.redisClient = redisClient;
	}

	/**
     * save session
     * @param session
     * @throws UnknownSessionException
     */
    private void saveSession(Session session) throws UnknownSessionException{
        if(session == null || session.getId() == null){
            logger.error("session or session id is null");
            return;
        }
        if(logger.isInfoEnabled()) {
        	logger.info("save session [{}]", session.getId());
        }
        session.setTimeout(maxIdleTimeInMillis);
        String key = session.getId().toString();
        byte[] val = SerializationUtils.serialize(session);
        redisClient.hsetObject(shiroSessionKey, namespace, key, val);
    }
	
	@Override
	public void update(Session session) throws UnknownSessionException {
		if(logger.isInfoEnabled()) {
        	logger.info("update session [{}]", session.getId());
        }
		saveSession(session);
	}

	@Override
	public void delete(Session session) {
		if(session == null || session.getId() == null){  
            logger.error("session or session id is null");  
            return;  
        }  
		if(logger.isInfoEnabled()) {
        	logger.info("delete session [{}]", session.getId());
        }
		redisClient.hdel(shiroSessionKey, namespace, session.getId().toString());
	}

	@Override
	public Collection<Session> getActiveSessions() {
        Set<String> keys = redisClient.hkeys(shiroSessionKey, namespace);
		Set<Session> sessions = Sets.newHashSet();
		
		if(null != keys && !keys.isEmpty()) {
			for (String key : keys) {
				Session session = (Session) SerializationUtils.deserialize(redisClient.hget(shiroSessionKey, namespace, key, new TypeReference<byte[]>() {
				}));
				sessions.add(session);
			}
		}
        return sessions;
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);
		if(logger.isInfoEnabled()) {
        	logger.info("do create session [{}]", sessionId);
        }
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		if(sessionId == null){
            logger.error("session id is null");
            return null;
        }
		if(logger.isInfoEnabled()) {
        	logger.info("do read session [{}]", sessionId);
        }
        String key = sessionId.toString();
        Session session = (Session) SerializationUtils.deserialize(redisClient.hget(shiroSessionKey, namespace, key, new TypeReference<byte[]>() {
		}));
        return session;
	}

}
