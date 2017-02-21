package ennate.egen.solutions.sml.domain;

public class Iris {

	private double petalLength;
	private double petalWidth;
	private double sepalLength;
	private double sepalWidth;
	private String flowerType;

	public Iris(double petalLength, double sepalLength, double petalWidth, double sepalWidth, String flowerType) {
		this.petalLength = petalLength;
		this.petalWidth = petalWidth;
		this.sepalLength = sepalLength;
		this.sepalWidth = sepalWidth;
		this.flowerType = flowerType;
	}

	public Iris(String content, String delimiter) {
		String[] fields = content.split(delimiter);

		// Sanity check
		if (fields.length != 5) {
			System.out.println("String passed in is corrupted! " + content
					+ " Expected number of fields = 5 - delimited by " + delimiter);
			return;
		}

		// parse the string into the individual fields
		try {
			sepalLength = Double.parseDouble(fields[0]);
			sepalWidth = Double.parseDouble(fields[1]);
			petalLength = Double.parseDouble(fields[2]);
			petalWidth = Double.parseDouble(fields[3]);
			flowerType = fields[4];
		} catch (NumberFormatException nfe) {
			System.out.println("String passed in is corrupted! " + content
					+ " Expected number of fields = 5 - delimited by " + delimiter);
			return;
		}
	}

	public String toString() {
		return "sepalLen" + "gth = " + sepalLength + " sepalWidth = " + sepalWidth + " petalLength = " + petalLength
				+ "petalWidth = " + petalWidth + " Flower = " + flowerType;
	}

	public String getFlowerType() {
		return flowerType;
	}

	public void setFlowerType(String flowerType) {
		this.flowerType = flowerType;
	}

	public double getPetalLength() {
		return petalLength;
	}

	public void setPetalLength(double petalLength) {
		this.petalLength = petalLength;
	}

	public double getPetalWidth() {
		return petalWidth;
	}

	public void setPetalWidth(double petalWidth) {
		this.petalWidth = petalWidth;
	}

	public double getSepalLength() {
		return sepalLength;
	}

	public void setSepalLength(double sepalLength) {
		this.sepalLength = sepalLength;
	}

	public double getSepalWidth() {
		return sepalWidth;
	}

	public void setSepalWidth(double sepalWidth) {
		this.sepalWidth = sepalWidth;
	}
}
