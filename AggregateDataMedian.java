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
import org.h2.value.ValueDouble;
import org.h2.value.ValueArray;
import org.h2.value.ValueLong;
import java.util.*;

/**
 *	Class to compute the median of data objects
 *  @author	Priyanka Samanta
 *  @author	Shraddha Atrawalkar
 *  @date 03-Nov-2015
 */
class AggregateDataMedian extends AggregateData {
	
	//ArrayList tp store the values;
	ArrayList<Value> our_dataArray = new ArrayList<Value>();

	@Override
	
	//Adding elements to the list
	void add(Database database, int dataType, boolean distinct, Value v) {
		if (v == null) {
			return;
		}
		else{
			our_dataArray.add(v);
		}
	}

	@Override
	Value getValue(Database database, int dataType, boolean distinct) {  

		//Converting the list to the type of Value array
		Value[] values = new Value[our_dataArray.size()];
		for(int i=0;i<our_dataArray.size();i++){
			values[i]=our_dataArray.get(i);
		}
		
		//Using comparator mode to sort the list of values
		final CompareMode compareMode = database.getCompareMode();
        Arrays.sort(values, new Comparator<Value>() {
            @Override
            public int compare(Value v1, Value v2) {
                Value a1 = v1;
                Value a2 = v2;
                return a1.compareTo(a2, compareMode);
            }
        });
		
		//To store the median value
		Value median=null;
		boolean flag=true;
		int median_point=0;
		
		//checking for the odd/even size of the list
		if(our_dataArray.size()%2!=0){
			median_point=our_dataArray.size()/2;
			median=values[median_point];
		}
		else{
			//handling the case of Integer
			Value representative=values[0];
			try{
				int rep_value = representative.getInt();
				int i = 2;
				Value div = ValueInt.get(i);
				median=(values[our_dataArray.size()/2].add(values[our_dataArray.size()/2+1]) ).divide(div);
				flag=false;
				
			}
			catch(Exception e){
				
			}
			finally{
				if(flag){
					//handling the case of double
					double i = 2.0;
					Value div = ValueDouble.get(i);
					median=(values[our_dataArray.size()/2].add(values[our_dataArray.size()/2+1]) ).divide(div);
				}
			}
			
		}
		//returning the median value
        return median.convertTo(dataType);	
	}
}