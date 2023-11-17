package net.runelite.client.plugins.socketba.jacob;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class GameTimer {
	private final Instant startTime = Instant.now();

	private Instant prevWave;

	GameTimer() {
		this.prevWave = this.startTime;
	}

	String getTime(boolean waveTime) {
		Duration elapsed;
		Instant now = Instant.now();
		if (waveTime) {
			elapsed = Duration.between(this.prevWave, now);
		} else {
			elapsed = Duration.between(this.startTime, now).minusMillis(600L);
		}
		return formatTime(LocalTime.ofSecondOfDay(elapsed.getSeconds()));
	}

	long getTimeInSeconds(boolean waveTime) {
		Duration elapsed;
		Instant now = Instant.now();
		if (waveTime) {
			elapsed = Duration.between(this.prevWave, now);
		} else {
			elapsed = Duration.between(this.startTime, now).minusMillis(600L);
		}
		return elapsed.getSeconds();
	}

	void setWaveStartTime() {
		this.prevWave = Instant.now();
	}

	private static String formatTime(LocalTime time) {
		if (time.getHour() > 0)
			return time.format(DateTimeFormatter.ofPattern("HH:mm"));
		return (time.getMinute() > 9) ? time.format(DateTimeFormatter.ofPattern("mm:ss")) : time.format(DateTimeFormatter.ofPattern("m:ss"));
	}
}
