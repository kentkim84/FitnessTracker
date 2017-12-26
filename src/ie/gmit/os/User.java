package ie.gmit.os;

import java.util.Arrays;

public class User {
	private long index;
	private String name;
	private String address;
	private String ppsn;
	private String password;	
	private long age;
	private double weight;
	private double height;
	private FitnessRecord[] fitnessRecords;
	private MealRecord[] mealRecords;
	
	public long getIndex() {
		return index;
	}
	public void setIndex(long index) {
		this.index = index;
	}
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
	public FitnessRecord[] getFitness() {
		return fitnessRecords;
	}
	public void setFitness(FitnessRecord[] fitnessRecords) {
		this.fitnessRecords = fitnessRecords;
	}
	public MealRecord[] getMeal() {
		return mealRecords;
	}
	public void setMeal(MealRecord[] mealRecords) {
		this.mealRecords = mealRecords;
	}
	@Override
	public String toString() {
		return "User [index=" + index + ", name=" + name + ", address=" + address + ", ppsn=" + ppsn + ", password="
				+ password + ", age=" + age + ", weight=" + weight + ", height=" + height + ", fitnessRecords="
				+ Arrays.toString(fitnessRecords) + ", mealRecords=" + Arrays.toString(mealRecords) + "]";
	}	
}
