package com.yoctopuce.doc_examples;

@SuppressWarnings("rawtypes")
public class Example implements Comparable<Example> {
    private String mName;
	private Class  mClass;
    private boolean mGeneric;

    public Example(String mName, Class mClass, boolean mGeneric) {
        this.mName = mName;
        this.mClass = mClass;
        this.mGeneric = mGeneric;
    }

    public String getName() {
        return mName;
    }

	public Class getClassToExecute() {
        return mClass;
    }

    public boolean isGeneric() {
        return mGeneric;
    }

    @Override
    public String toString() {
        return mName;
    }

	@Override
	public int compareTo(Example another) {
		return this.getName().compareTo(another.getName());
	}
}
