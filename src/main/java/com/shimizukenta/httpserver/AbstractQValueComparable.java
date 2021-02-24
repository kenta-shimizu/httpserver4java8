package com.shimizukenta.httpserver;

public abstract class AbstractQValueComparable<T> implements Comparable<AbstractQValueComparable<T>> {
	
	private final T v;
	private final float q;
	
	public AbstractQValueComparable(T v, float q) {
		this.v = v;
		this.q = q;
	}
	
	public AbstractQValueComparable(T v) {
		this(v, 1.0F);
	}
	
	public T value() {
		return this.v;
	}
	
	@Override
	public int compareTo(AbstractQValueComparable<T> other) {
		return Float.valueOf(other.q).compareTo(Float.valueOf(this.q));
	}
	
}
