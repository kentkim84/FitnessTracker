package ie.gmit.os;


import java.util.List;

public class User {	
	private String name;
	private String address;
	private String ppsn;
	private String password;	
	private long age;
	private double weight;
	private double height;
	private List<FitnessRecord> fitnessRecordList;
	private List<MealRecord> mealRecordList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPpsn() {
		return ppsn;
	}
	public void setPpsn(String ppsn) {
		this.ppsn = ppsn;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getAge() {
		return age;
	}
	public void setAge(long age) {
		this.age = age;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public List<FitnessRecord> getFitnessRecordList() {
		return fitnessRecordList;
	}
	public void setFitnessRecordList(List<FitnessRecord> fitnessRecordList) {
		this.fitnessRecordList = fitnessRecordList;
	}
	public List<MealRecord> getMealRecordList() {
		return mealRecordList;
	}
	public void setMealRecordList(List<MealRecord> mealRecordList) {
		this.mealRecordList = mealRecordList;
	}
	@Override
	public String toString() {
		return "{name=" + name + ", address=" + address + ", ppsn=" + ppsn + ", password=" + password + ", age="
				+ age + ", weight=" + weight + ", height=" + height + ", fitnessRecordList=" + fitnessRecordList
				+ ", mealRecordList=" + mealRecordList + "}";
	}
}
