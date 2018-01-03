package ie.gmit.os;

public class FitnessRecord {	
	private String mode;
	private long duration;
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "{\"mode\":" + "\"" + mode + "\"" + ", \"duration\":" + duration + "}";
	}		
}
