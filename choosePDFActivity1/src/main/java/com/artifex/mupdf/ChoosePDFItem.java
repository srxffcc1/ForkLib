package com.artifex.mupdf;

public class ChoosePDFItem {


	final public Type type;
	final public String name;

	public ChoosePDFItem (Type t, String n) {
		type = t;
		name = n;
	}
	public enum Type {
		PARENT, DIR, DOC
	}
}
