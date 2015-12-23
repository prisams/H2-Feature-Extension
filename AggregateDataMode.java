/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.expression;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.util.ValueHashMap;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueInt;
import org.h2.value.ValueArray;
import org.h2.value.ValueLong;
import java.util.*;

/**
 * Class to comoute the Mode of data
 * @author Chandni Pakalapati
 * @author Shashank Narkhede
 * @date 03-Nov-2015
 */
class AggregateDataMode extends AggregateData {
	
	//ArrayList tp store the values;
	HashMap<Value,Integer> our_dataMap = new HashMap<Value,Integer>();

	@Override
	
	//Adding elements to the HashMap and increasing the count of occurance
	void add(Database database, int dataType, boolean distinct, Value v) {
		if (v == null) {
			return;
		}
		Integer no_of_occurance=our_dataMap.get(v);
		Integer temp_occurance;
		if(no_of_occurance == null){
			temp_occurance=1;
		}
		else{
			temp_occurance=no_of_occurance+1;
		}
		our_dataMap.put(v,temp_occurance);
	}

	@Override
	//getting mode for the value
	Value getValue(Database database, int dataType, boolean distinct) {  

		Value mode=null;
		int count=0;
		
		for (Map.Entry<Value, Integer> e : our_dataMap.entrySet()) {
			Value key = e.getKey();
			int tempCount = e.getValue();
			if (tempCount>count){
				count=tempCount;
				mode=key;
			}
		}
		
        return mode.convertTo(dataType);
	}
}