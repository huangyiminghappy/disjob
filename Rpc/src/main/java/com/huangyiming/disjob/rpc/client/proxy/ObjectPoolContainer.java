package com.huangyiming.disjob.rpc.client.proxy;

public class ObjectPoolContainer {

	private long borrowedCount;
	private long createdCount; 
	private long destroyedByBorrowValidationCount;
	private long destroyedByEvictorCount;
	private long destroyedCount;
	private long numActive;
	private long numIdle;
	private long numWaiters;
	private long returnedCount;
	
	public long getBorrowedCount() {
		return borrowedCount;
	}

	public void setBorrowedCount(long borrowedCount) {
		this.borrowedCount = borrowedCount;
	}

	public long getCreatedCount() {
		return createdCount;
	}

	public void setCreatedCount(long createdCount) {
		this.createdCount = createdCount;
	}

	public long getDestroyedByBorrowValidationCount() {
		return destroyedByBorrowValidationCount;
	}

	public void setDestroyedByBorrowValidationCount(
			long destroyedByBorrowValidationCount) {
		this.destroyedByBorrowValidationCount = destroyedByBorrowValidationCount;
	}

	public long getDestroyedByEvictorCount() {
		return destroyedByEvictorCount;
	}

	public void setDestroyedByEvictorCount(long destroyedByEvictorCount) {
		this.destroyedByEvictorCount = destroyedByEvictorCount;
	}

	public long getDestroyedCount() {
		return destroyedCount;
	}

	public void setDestroyedCount(long destroyedCount) {
		this.destroyedCount = destroyedCount;
	}

	public long getNumActive() {
		return numActive;
	}

	public void setNumActive(long numActive) {
		this.numActive = numActive;
	}

	public long getNumIdle() {
		return numIdle;
	}

	public void setNumIdle(long numIdle) {
		this.numIdle = numIdle;
	}

	public long getNumWaiters() {
		return numWaiters;
	}

	public void setNumWaiters(long numWaiters) {
		this.numWaiters = numWaiters;
	}

	public long getReturnedCount() {
		return returnedCount;
	}

	public void setReturnedCount(long returnedCount) {
		this.returnedCount = returnedCount;
	}

	@Override
	public String toString() {
		return "ObjectPoolContainer [BorrowedCount=" + borrowedCount
				+ ", CreatedCount=" + createdCount
				+ ", DestroyedByBorrowValidationCount="
				+ destroyedByBorrowValidationCount
				+ ", DestroyedByEvictorCount=" + destroyedByEvictorCount
				+ ", DestroyedCount=" + destroyedCount + ", NumActive="
				+ numActive + ", NumIdle=" + numIdle + ", NumWaiters="
				+ numWaiters + ", ReturnedCount=" + returnedCount + "]";
	}
}
