package tr.com.serayuzgur.runlight.json;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONManager {
	// Instances are fully thread-safe so cache mapper creation is expensive.
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	static{
		JSONManager.getMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	
	
	public static ObjectMapper getMapper(){
		return MAPPER;
	}

}
