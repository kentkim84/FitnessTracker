package ie.gmit.os;

public class User {
	private int index;
	private String name;
	private String address;
	private String ppsn;
	private int age;
	private float weight;
	private float height;
	private FitnessRecord[] fitnessRecords;
	private MealRecord[] mealRecords;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
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
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
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
}
