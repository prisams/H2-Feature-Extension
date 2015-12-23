/*
 * Copyright 2004-2014 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.value;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.engine.SysProperties;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;

/**
 * Implementation of the PASSWORD data type.
 * @author	Priyanka Samanta
 * @date	15-NOV-2015
 */
public class ValuePassword extends Value {

    private static final ValuePassword EMPTY = new ValuePassword("");

    /**
     * The string data.
     */
    protected final String value;

    protected ValuePassword(String value) {
        this.value = value;
    }

    @Override
    public String getSQL() {
        return StringUtils.quoteStringSQL(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValuePassword
                && value.equals(((ValuePassword) other).value);
    }

    @Override
    protected int compareSecure(Value o, CompareMode mode) {
        // compatibility: the other object could be another type
        ValuePassword v = (ValuePassword) o;
        return mode.compareString(value, v.value, false);
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public long getPrecision() {
        return value.length();
    }

    @Override
    public Object getObject() {
        return value;
    }

    @Override
    public void set(PreparedStatement prep, int parameterIndex)
            throws SQLException {
        prep.setString(parameterIndex, value);
    }

    @Override
    public int getDisplaySize() {
        return value.length();
    }

    @Override
    public int getMemory() {
        return value.length() * 2 + 48;
    }

    @Override
    public Value convertPrecision(long precision, boolean force) {
        if (precision == 0 || value.length() <= precision) {
            return this;
        }
        int p = MathUtils.convertLongToInt(precision);
        return getNew(value.substring(0, p));
    }

    @Override
    public int hashCode() {
        // TODO hash performance: could build a quicker hash
        // by hashing the size and a few characters
        return value.hashCode();
    }

    @Override
    public int getType() {
        return Value.STRING;//String
    }

    /**
     * Get or create a string value for the given string.
     *
     * @param s the string
     * @return the value
     */
    public static Value get(String s) {
        return get(s, false);
    }

    /**
     * Get or create a string value for the given string.
     *
     * @param s the string
     * @param treatEmptyStringsAsNull whether or not to treat empty strings as
     *            NULL
	 * Handling all the cases for Password Validation
     * @return the value
     */
	 
    public static Value get(String s, boolean treatEmptyStringsAsNull) {
		//password should be minimum 8 characters long
		if(s.length()<8){
			throw DbException.getUnsupportedException("Password must be 8 characters long");
		}
		//password has to start with an alphabet only
		char firstChar=s.charAt(0);
		if (!(((firstChar>=65) &&(firstChar<=90)) || ((firstChar>=97) && (firstChar<=122)))){
			throw DbException.getUnsupportedException("Password must start with an alphabet");
		}
		
		//password should be a combination of number uppercase 
		//lowercase number and anyone of the speacial characters: @#?!
		boolean number_flag=false;
		boolean upperCase_flag=false;
		boolean lowerCase_flag=false;
		boolean specialCharacter_flag=false;
		
		for (int i=0;i<s.length();i++){
			//to validate the digits [0-9]
			if ((s.charAt(i)>=48) &&(s.charAt(i)<=57)){
				number_flag=true;
			}
			//to validate the upper case character [A-Z]
			if ((s.charAt(i)>=65) &&(s.charAt(i)<=90)){
				upperCase_flag=true;
			}
			//to validate the lower case characters [a-z]
			if ((s.charAt(i)>=97) &&(s.charAt(i)<=122)){
				lowerCase_flag=true;
			}
			 //to validate the specail characters [@#?!]
			if ((s.charAt(i)==64) || (s.charAt(i)==35) || 
					(s.charAt(i)==63) || (s.charAt(i)==33)){
				specialCharacter_flag=true;
			}
		}
		if (!(lowerCase_flag && upperCase_flag && number_flag && specialCharacter_flag)){
			throw DbException.getUnsupportedException
			("Password must be a combination of number, upperCase,lowerCase and special character #@!?");
		}
        if (s.isEmpty()) {
            return treatEmptyStringsAsNull ? ValueNull.INSTANCE : EMPTY;
        }
        ValuePassword obj = new ValuePassword(StringUtils.cache(s));
        if (s.length() > SysProperties.OBJECT_CACHE_MAX_PER_ELEMENT_SIZE) {
            return obj;
        }
        return Value.cache(obj);
    }

    /**
     * Create a new String value of the current class.
     * This method is meant to be overridden by subclasses.
     *
     * @param s the string
     * @return the value
     */
    protected Value getNew(String s) {
        return ValuePassword.get(s);
    }
}
