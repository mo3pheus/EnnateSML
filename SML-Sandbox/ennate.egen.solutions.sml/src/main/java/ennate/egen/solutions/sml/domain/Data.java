package ennate.egen.solutions.sml.domain;

public class Data {
	private Double[] fields;
	private String classId;
	private int numberOfFields;

	public Data(String content, String delimiter, int numberOfFields) throws NumberFormatException {
		String[] parts = content.split(delimiter);
		this.numberOfFields = numberOfFields;

		/*
		 * Sanity check
		 */
		if (parts.length != numberOfFields + 1) {
			System.out.println(
					"Corrupted string passed in" + content + " expected fields length = " + numberOfFields);
			return;
		}

		fields = new Double[numberOfFields];
		int i;
		for (i = 0; i < numberOfFields; i++) {
			fields[i] = Double.parseDouble(parts[i]);
		}
		classId = parts[i];
	}

	public Data(int numberOfFields) {
		this.numberOfFields = numberOfFields;
		fields = new Double[numberOfFields];

		/*
		 * Initialize the array
		 */
		for (int i = 0; i < numberOfFields; i++) {
			fields[i] = new Double(0.0d);
		}

		classId = "";
	}

	public String toString() {
		StringBuilder dataBuilder = new StringBuilder();
		try {
			for (int i = 0; i < numberOfFields; i++) {
				dataBuilder.append(fields[i]);
				dataBuilder.append(" ");
			}
			dataBuilder.append(classId);
		} catch (Exception e) {

		}
		return dataBuilder.toString();
	}

	public Double[] getFields() {
		return fields;
	}

	public void setFields(Double[] fields) {
		this.fields = fields;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
}
