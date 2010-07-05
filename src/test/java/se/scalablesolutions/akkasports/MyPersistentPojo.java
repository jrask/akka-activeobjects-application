package se.scalablesolutions.akkasports;

import se.scalablesolutions.akka.actor.annotation.transactionrequired;
import se.scalablesolutions.akka.persistence.common.PersistentMap;
import se.scalablesolutions.akka.persistence.redis.RedisStorage;

@transactionrequired
public class MyPersistentPojo {
	
	static byte[] KEY = "KEY".getBytes();
	
	private PersistentMap<byte[], byte[]> state;
	
	public void create(String uuid,String value) {
		state = RedisStorage.getMap(uuid);
		if(state.contains(KEY)) {
			throw new RuntimeException("Pojo with value: " + new String(state.get(KEY).get()) + " already exists");
		}
		state.put(KEY,value.getBytes());
	}
	
	public boolean load(String uuid) {
		state = RedisStorage.getMap(uuid);
		if(state.contains(KEY)) {
			return true;
		}
		return false;
	}
	
}
