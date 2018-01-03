package ie.gmit.os;

public class MealRecord {
	private String typeOfMeal;
	private String description;
	
	public String getTypeOfMeal() {
		return typeOfMeal;
	}
	public void setTypeOfMeal(String typeOfMeal) {
		this.typeOfMeal = typeOfMeal;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "{\"typeOfMeal\":" + "\"" + typeOfMeal + "\"" + ", \"description\":" + "\"" + description + "\"" + "}";
	}	
}
