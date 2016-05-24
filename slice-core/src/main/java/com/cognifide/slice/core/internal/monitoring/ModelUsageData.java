/*-
 * #%L
 * Slice - Core
 * %%
 * Copyright (C) 2012 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.cognifide.slice.core.internal.monitoring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ModelUsageData {

	private ConcurrentHashMap<Class<?>, ModelUsageData> subModels = new ConcurrentHashMap<Class<?>, ModelUsageData>();

	private AtomicLong count = new AtomicLong();

	private AtomicLong totalTime = new AtomicLong();

	public void addTimeMeasurement(Long timeMeasurement) {
		count.incrementAndGet();
		totalTime.addAndGet(timeMeasurement);
	}

	public long getCount() {
		return count.get();
	}

	public long getTotalTime() {
		return TimeUnit.NANOSECONDS.toMillis(totalTime.get());
	}

	public ConcurrentHashMap<Class<?>, ModelUsageData> getSubModels() {
		return subModels;
	}

	public double getAverageTime() {
		return totalTime.get() / (double) count.get() / 1000000;
	}

	public void add(ModelUsageData statistics) {
		this.count.addAndGet(statistics.count.get());
		this.totalTime.addAndGet(statistics.totalTime.get());
	}

	public ModelUsageData copy() {
		ModelUsageData copy = new ModelUsageData();
		copy.count.set(this.count.get());
		copy.totalTime.set(this.totalTime.get());
		return copy;
	}

	public void clearSubModels() {
		subModels = new ConcurrentHashMap<Class<?>, ModelUsageData>();
	}
}