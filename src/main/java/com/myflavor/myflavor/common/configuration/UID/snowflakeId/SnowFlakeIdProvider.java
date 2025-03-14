package com.myflavor.myflavor.common.configuration.UID.snowflakeId;

import java.util.concurrent.atomic.AtomicLong;

public class SnowFlakeIdProvider {
	private final long twepoch = 1288834974657L; // 트위치 채번시간 ( 에포크 시간 오버플로 때문에 )
	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private final long sequenceBits = 12L;
	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long workerId;
	private long datacenterId;
	private AtomicLong sequence = new AtomicLong(0L);
	private long lastTimestamp = -1L;

	public SnowFlakeIdProvider(long workerId, long datacenterId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
				String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(
				String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	public synchronized long nextId() {
		long timestamp = timeGen();

		if (lastTimestamp == timestamp) {
			long currentSequence = sequence.incrementAndGet() & sequenceMask;
			sequence.set(currentSequence);

			if (currentSequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence.set(0L);
		}

		lastTimestamp = timestamp;

		return ((timestamp - twepoch) << timestampLeftShift) |
			(datacenterId << datacenterIdShift) |
			(workerId << workerIdShift) |
			sequence.get();

	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected long timeGen() {
		// FIXME NTP 서버로 부터 받아와야 정확함.
		return System.currentTimeMillis();
	}

}
