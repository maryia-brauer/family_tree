package family;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Project {
	public static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		Person.relationshipTypes.add("wife");
		Person.relationshipTypes.add("husband");
		Person.relationshipTypes.add("brother");
		Person.relationshipTypes.add("brothers");
		Person.relationshipTypes.add("sister");
		Person.relationshipTypes.add("sisters");
		Person.relationshipTypes.add("father");
		Person.relationshipTypes.add("mother");
		Person.relationshipTypes.add("daughter");
		Person.relationshipTypes.add("daughters");
		Person.relationshipTypes.add("son");
		Person.relationshipTypes.add("sons");

		File f = new File("C:\\Users\\Professional\\Desktop\\family.txt");
		if (!f.exists()) {
			System.out.println("New file created!");
			f.createNewFile();
		} else {
			readFile(f);
		}

		int userFirstInput = -1;
		do {
			userFirstInput = Project
					.getTheFirstNumberInput("\n1 - Add person \n2 - Print info \n0 - Exit \nPlease enter the number:");
			if (userFirstInput == 1) {
				String addPersonInput = "";
				do {
					addPersonInput = getTheAddPersonInput();
				} while (!isUserInputCorrect(addPersonInput));
				Person.userInput = addPersonInput;
				addPerson(addPersonInput);
			}
			if (userFirstInput == 2)
				for (Person p : Person.all) {
					if (p.name.equals(getPersonNumberInput(Person.all.size()))) {
						System.out.println(p);
						break;
					}
				}
			if (userFirstInput == 0) {
				int userExitInput = Project.getTheFirstNumberInput(
						"1 - to start over \n2 - to save info in a file \n0 - simply finish the program");
				if (userExitInput == 1) {
					clearFile(f);
				}
				if (userExitInput == 2) {
					clearFile(f);
					saveFile(f);
				}
				if (userExitInput == 0)
					break;
			}
		} while (userFirstInput != 0);
		
		System.out.println("You finished the program!");
		scan.close();
	}

	public static void clearFile(File f) {
		try {
			FileWriter fstream1 = new FileWriter(f);
			BufferedWriter out1 = new BufferedWriter(fstream1);
			out1.write("");
			out1.close();
		} catch (Exception e) {
			System.err.println("Error in file cleaning: " + e.getMessage());
		}
	}

	public static void saveFile(File f) {
		try {
			FileWriter fw = new FileWriter(f.getAbsolutePath(), true);
			for (Person p : Person.all) {
				fw.append(p.name + ";" + p.gender + ";" + p.isAlive + "\n");
				if (p.partner != null)
					fw.append(p.partner.name + "\n");
				else
					fw.append("*\n");
				if (p.mother != null)
					fw.append(p.mother.name + "\n");
				else
					fw.append("*\n");
				if (p.father != null)
					fw.append(p.father.name + "\n");
				else
					fw.append("*\n");
				if (!p.kids.isEmpty())
					fw.append(saveSet(p.kids) + "\n");
				else
					fw.append("*\n");
				if (!p.siblings.isEmpty())
					fw.append(saveSet(p.siblings) + "\n");
				else
					fw.append("*\n");
			}
			fw.close();
		} catch (Exception e) {
			System.out.println("Problem saving file");
		}
	}

	public static String saveSet(HashMap<String, Person> set) {
		String result = "";
		for (String kid : set.keySet()) {
			result += kid + " ";
		}
		return result.trim().replace(" ", ";");
	}

	public static void readFile(File f) {
		try {
			createPeopleFromFile(f);
			Scanner fileReader = new Scanner(f);
			int counter = 0;
			Person p = null;
			while (fileReader.hasNextLine()) {
				String line = fileReader.nextLine();
				counter++;
				if (counter == 1) {
					String name = line.split(";")[0];
					if (Person.checkIfPersonIsInTheList(name))
						p = Person.getPersonFromTheList(name);
					else {
						System.out.println("Mistake in the file");
						fileReader.close();
						return;
					}
				}
				if (p != null) {
					if (counter == 2 && !line.equals("*")) {
						if (Person.checkIfPersonIsInTheList(line))
							p.partner = Person.getPersonFromTheList(line);
						continue;
					}
					if (counter == 3 && !line.equals("*")) {
						if (Person.checkIfPersonIsInTheList(line))
							p.mother = Person.getPersonFromTheList(line);
						continue;
					}
					if (counter == 4 && !line.equals("*")) {
						if (Person.checkIfPersonIsInTheList(line))
							p.father = Person.getPersonFromTheList(line);
						continue;
					}
					if (counter == 5 && !line.equals("*")) {
						if (line.contains(";")) {
							String[] kids = line.split(";");
							for (String kid : kids) {
								if (Person.checkIfPersonIsInTheList(kid))
									p.kids.put(kid, Person.getPersonFromTheList(kid));
							}
						} else {
							if (Person.checkIfPersonIsInTheList(line))
								p.kids.put(line, Person.getPersonFromTheList(line));
						}
						continue;
					}
					if (counter == 6 && !line.equals("*")) {
						if (line.contains(";")) {
							String[] siblings = line.split(";");
							for (String sibling : siblings) {
								if (Person.checkIfPersonIsInTheList(sibling))
									p.siblings.put(sibling, Person.getPersonFromTheList(sibling));
							}
						} else {
							if (Person.checkIfPersonIsInTheList(line))
								p.siblings.put(line, Person.getPersonFromTheList(line));
						}
						counter = 0;
						continue;
					}
				}

			}
			fileReader.close();
		} catch (Exception e) {
			System.out.println("Problem reading file");
		}
	}

	public static void createPeopleFromFile(File f) {
		try {
			Scanner fileReader = new Scanner(f);
			while (fileReader.hasNextLine()) {
				String line = fileReader.nextLine();
				if (line.contains("true") || line.contains("false")) {
					String[] p = line.split(";");
					boolean life = true;
					if (p[2].equals("false"))
						life = false;
					new Person(p[0], p[1], life);
				}
			}
			fileReader.close();
		} catch (Exception e) {
			System.out.println("Problem reading file");
		}
	}

	public static String getPersonNumberInput(int limit) {
		HashMap<Integer, String> people = new HashMap<Integer, String>();
		int result = 0;
		boolean hasValidInput = false;
		do {
			int personNumber = limit;
			for (Person p : Person.all) {
				people.put(personNumber, p.name);
				System.out.println(personNumber + " - " + people.get(personNumber));
				personNumber--;
				if (personNumber == 0) {
					break;
				}
			}
			System.out.println("Please enter the number of person: ");
			scan.nextLine();
			hasValidInput = scan.hasNextInt();
			if (!hasValidInput) {
				System.err.println("That is not a number");
				scan.next();
				continue;
			}
			result = scan.nextInt();
			if (result > limit || result < 0) {
				hasValidInput = false;
				System.err.println("Choose from the options");
			}
		} while (!hasValidInput);
		scan.nextLine(); // if not stay with Enter (\n)
		return people.get(result);
	}

	public static int getTheFirstNumberInput(String text) {
		int result = 0;
		boolean hasValidInput = false;
		do {
			System.out.println(text);
			hasValidInput = scan.hasNextInt();
			if (!hasValidInput) {
				System.err.println("That is not a number");
				scan.next();
				continue;
			}
			result = scan.nextInt();
			if (result > 2 || result < 0) {
				hasValidInput = false;
				System.err.println("Choose from the options");
			}
		} while (!hasValidInput);
		scan.nextLine(); // if not stay with Enter (\n)
		return result;
	}

	public static void addPerson(String text) {
		String relationship = Person.getRelationshipType(text);
		Person p;
		String name = Person.getName(text);
		if (Person.checkIfPersonIsInTheList(name))
			p = Person.getPersonFromTheList(name);
		else
			p = new Person(text);
		switch (relationship) {
		case "wife":
		case "husband":
			p.addPartnerRelation();
			break;
		case "mother":
		case "father":
			p.addParentRelation();
			break;
		case "daughter":
		case "daughters":
		case "son":
		case "sons":
			p.addChildRelation();
			break;
		case "sister":
		case "sisters":
		case "brother":
		case "brothers":
			p.addSiblingRelation();
			break;
		default:
			break;
		}
	}

	public static String getTheAddPersonInput() {
		System.out.println(
				"Enter the info in the following format:\nName(F/M(gender))...has/had...relationsipType - Name1, Name2, "
						+ "..., NameN");
		String result = scan.nextLine();
		return result;
	}

	public static boolean isUserInputCorrect(String text) {
		if (text == null || text.isBlank()) {
			System.err.println("This is empty string");
			return false;
		}
		if (!text.contains("(F)") && !text.contains("(M)")) {
			System.err.println("No gender type given");
			return false;
		}
		if (Person.getName(text).isBlank()) {
			System.err.println("No first person's name given");
			return false;
		}
		if (!Person.isAlive(text) && !text.contains(" had ")) {
			System.err.println("No has/had given");
			return false;
		}
		if (!text.contains(" - ")) {
			System.err.println("No - symbol given");
			return false;
		}
		if (!Person.relationshipTypes.contains(Person.getRelationshipType(text))) {
			System.err.println("No relation type given");
			return false;
		}
		if (text.split(" - ").length < 2 || text.split(" - ")[1].isBlank()) {
			System.err.println("No relative name(s) given");
			return false;
		}
		return true;
	}
}
