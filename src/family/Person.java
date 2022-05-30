package family;

import java.util.ArrayList;
import java.util.HashMap;

public class Person {

	String name;
	String gender;
	boolean isAlive;
	Person partner;
	Person mother;
	Person father;
	HashMap<String, Person> kids = new HashMap<String, Person>();
	HashMap<String, Person> siblings = new HashMap<String, Person>();

	static String userInput;
	static ArrayList<Person> all = new ArrayList<Person>();
	static ArrayList<String> relationshipTypes = new ArrayList<String>();

	Person(String text) {
		this.name = getName(text);
		this.gender = getGender(text);
		this.isAlive = isAlive(text);
		if (checkIfPersonIsInTheList(this.name) == false) {
			Person.all.add(this);
		}
	}

	Person(String name, String gender, boolean isAlive) {
		this.name = name;
		this.gender = gender;
		this.isAlive = isAlive;
		Person.all.add(this);
	}

	static boolean checkIfPersonIsInTheList(String name) {
		return Person.all.stream().filter(p -> p.name.equals(name)).findAny().isPresent();
	}

	static Person getPersonFromTheList(String name) {
		return Person.all.stream().filter(p -> p.name.equals(name)).findAny().get();
	}

	static String getName(String text) {
		return text.trim().split("\\(")[0];
	}

	static String getGender(String text) {
		return text.split("\\(")[1].split("\\)")[0].trim();
	}

	static boolean isAlive(String text) {
		return text.contains(" has ");
	}

	static String[] getRelativesNames(String text) {
		return text.split("-")[1].trim().split(", ");
	}

	static String getRelationshipType(String text) {
		String t = text.split("-")[0].trim();
		return t.split(" ")[t.split(" ").length - 1];
	}

	// Mary(F) has a husband - Jim
	void addPartnerRelation() {
		if (this.partner != null) {
			System.out.println(this.name + " already has a partner");
			return;
		}
		if (getRelationshipType(userInput).equals("husband") && this.gender.equals("F")) {
			String husbandName = getRelativesNames(userInput)[0];
			if (checkIfPersonIsInTheList(husbandName)) {
				makePartnership(getPersonFromTheList(husbandName));
			} else {
				Person husband = new Person(husbandName, "M", true);
				makePartnership(husband);
			}
		} else if (getRelationshipType(userInput).equals("wife") && this.gender.equals("M")) {
			String wifeName = getRelativesNames(userInput)[0];
			if (checkIfPersonIsInTheList(wifeName)) {
				makePartnership(getPersonFromTheList(wifeName));
			} else {
				Person wife = new Person(wifeName, "F", false);
				makePartnership(wife);
			}
		}
	}

	void makePartnership(Person p) {
		if (p.partner != null) {
			System.out.println(p.name + " already has a partner");
			return;
		}
		this.partner = p;
		p.partner = this;

		this.kids = getKids(p, this);
		p.kids = getKids(p, this);

		if (!this.kids.isEmpty() && p.gender.equals("F")) {
			for (Person k : this.kids.values()) {
				k.mother = p;
			}
		}

		if (!this.kids.isEmpty() && p.gender.equals("M")) {
			for (Person k : this.kids.values()) {
				k.father = p;
			}
		}
		makeSiblingsByParent(p);
	}

	HashMap<String, Person> getKids(Person one, Person two) {
		boolean firstEmpty = (one.kids == null) || one.kids.isEmpty();
		boolean secondEmpty = (two.kids == null) || two.kids.isEmpty();
		if (firstEmpty & secondEmpty)
			return new HashMap<String, Person>();
		else if (firstEmpty)
			return new HashMap<String, Person>(two.kids);
		else if (secondEmpty)
			return new HashMap<String, Person>(one.kids);
		else {
			HashMap<String, Person> result = new HashMap<String, Person>();
			result.putAll(one.kids);
			result.putAll(two.kids);
			return result;
		}
	}

	void addParentRelation() {
		if (getRelationshipType(userInput).equals("mother")) {
			if (this.mother != null) {
				System.out.println(this.name + " already has a mother");
				return;
			}
			String motherName = getRelativesNames(userInput)[0];
			if (checkIfPersonIsInTheList(motherName)) {
				makeMotherRelation(getPersonFromTheList(motherName));
			} else {
				Person mother = new Person(motherName, "F", true);
				makeMotherRelation(mother);
			}

		}
		if (getRelationshipType(userInput).equals("father")) {
			if (this.father != null) {
				System.out.println(this.name + " already has a father");
				return;
			}
			String fatherName = getRelativesNames(userInput)[0];
			if (checkIfPersonIsInTheList(fatherName)) {
				makeFatherRelation(getPersonFromTheList(fatherName));
			} else {
				Person father = new Person(fatherName, "M", true);
				makeFatherRelation(father);
			}
		}
	}

	void makeMotherRelation(Person p) {
		this.mother = p;
		p.kids.put(this.name, this);
		if (p.partner != null) {
			p.partner.kids.put(this.name, this);
			this.father = p.partner;
		}
		this.makeSiblingsByParent(p);
	}

	void makeFatherRelation(Person p) {
		this.father = p;
		p.kids.put(this.name, this);
		if (p.partner != null) {
			p.partner.kids.put(this.name, this);
			this.mother = p.partner;
		}
		this.makeSiblingsByParent(p);
	}

	// Jim(M) has 2 daughters - Annie, Katie
	void addChildRelation() {
		if (getRelationshipType(userInput).equals("son") || getRelationshipType(userInput).equals("sons")) {
			for (String kidName : getRelativesNames(userInput)) {
				if (checkIfPersonIsInTheList(kidName)) {
					makeChildRelation(getPersonFromTheList(kidName));
				} else {
					Person kid = new Person(kidName, "M", true);
					makeChildRelation(kid);
				}
			}
		}
		if (getRelationshipType(userInput).equals("daughter") || getRelationshipType(userInput).equals("daughters")) {
			for (String kidName : getRelativesNames(userInput)) {
				if (checkIfPersonIsInTheList(kidName)) {
					makeChildRelation(getPersonFromTheList(kidName));
				} else {
					Person kid = new Person(kidName, "F", true);
					makeChildRelation(kid);
				}
			}
		}
	}

	void makeChildRelation(Person p) {
		if (this.gender.equals("F")) {
			if (p.mother != null) {
				System.out.println(p.name + " already has a mother");
				return;
			}
			p.mother = this;
			this.kids.put(p.name, p);
			if (this.partner != null) {
				this.partner.kids.put(p.name, p);
				p.father = this.partner;
			}
			p.makeSiblingsByParent(this);
		}
		if (this.gender.equals("M")) {
			if (p.father != null) {
				System.out.println(p.name + " already has a father");
				return;
			}
			p.father = this;
			this.kids.put(p.name, p);
			if (this.partner != null) {
				this.partner.kids.put(p.name, p);
				p.mother = this.partner;
			}
			p.makeSiblingsByParent(this);
		}
	}

	// Vil(M) has 2 brothers - Rick, Morty
	void addSiblingRelation() {
		if (getRelationshipType(userInput).equals("brother") || getRelationshipType(userInput).equals("brothers")) {
			for (String brotherName : getRelativesNames(userInput)) {
				if (checkIfPersonIsInTheList(brotherName)) {
					makeSiblingRelation(getPersonFromTheList(brotherName));
				} else {
					Person brother = new Person(brotherName, "M", true);
					makeSiblingRelation(brother);
				}
			}
		}
		if (getRelationshipType(userInput).equals("sister") || getRelationshipType(userInput).equals("sisters")) {
			for (String sisterName : getRelativesNames(userInput)) {
				if (checkIfPersonIsInTheList(sisterName)) {
					makeSiblingRelation(getPersonFromTheList(sisterName));
				} else {
					Person sister = new Person(sisterName, "F", true);
					makeSiblingRelation(sister);
				}
			}
		}
	}

	void makeSiblingRelation(Person p) {
		Person mum = getMother(this, p);
		if (mum != null) {
			mum.makeChildRelation(p);
			mum.makeChildRelation(this);
			this.makeSiblingsByParent(mum);
		} else {
			Person dad = getFather(this, p);
			if (dad != null) {
				dad.makeChildRelation(p);
				dad.makeChildRelation(this);
				this.makeSiblingsByParent(dad);
			}
		}

	}

	void makeSiblingsWithoutParent(Person one, Person two) {
		one.siblings.put(two.name, two);
		two.siblings.put(one.name, one);
		if (one.siblings.size() != two.siblings.size()) {
			for (Person p : one.siblings.values()) {
				for (Person l : two.siblings.values()) {
					if (p.equals(l) || p.equals(two))
						continue;
					two.siblings.put(p.name, p);
					p.siblings.put(two.name, two);
				}
			}
			for (Person p : two.siblings.values()) {
				for (Person l : one.siblings.values()) {
					if (p.equals(l) || p.equals(one))
						continue;
					one.siblings.put(p.name, p);
					p.siblings.put(one.name, one);
				}
			}
		}

	}

	void makeSiblingsByParent(Person parent) {
		this.siblings.putAll(parent.kids);
		if (parent.kids.containsKey(this.name))
			this.siblings.remove(this.name);
		for (Person kid : parent.kids.values()) {
			if (kid.name.equals(this.name))
				continue;
			kid.siblings.put(this.name, this);
		}
	}

	Person getMother(Person one, Person two) {
		boolean first = (one.mother != null);
		boolean second = (two.mother != null);

		if (!first && !second) {
			makeSiblingsWithoutParent(one, two);
			return null;
		}
		if (one.mother.equals(two.mother) || (first && !second))
			return one.mother;
		else if (!first && second)
			return two.mother;
		else {
			System.out.println("They can not be siblings");
			return null;
		}
	}

	Person getFather(Person one, Person two) {
		boolean first = (one.father != null);
		boolean second = (two.father != null);

		if (!first && !second) {
			makeSiblingsWithoutParent(one, two);
			return null;
		}
		if (one.father.equals(two.father) || (first && !second))
			return one.father;
		else if (!first && second)
			return two.father;
		else {
			System.out.println("They can not be siblings");
			return null;
		}
	}

	String getNamesFromSet(HashMap<String, Person> set) {
		String male = "";
		String female = "";
		String result = "";
		if (set.size() > 0) {
			int maleCounter = 0;
			int femaleCounter = 0;
			for (Person p : set.values()) {
				if (p.gender.equals("M")) {
					maleCounter++;
					male += p.name + ", ";
					continue;
				}
				femaleCounter++;
				female += p.name + ", ";
			}
			if (male.length() > 3)
				male = male.substring(0, (male.length() - 2));
			if (female.length() > 3)
				female = female.substring(0, (female.length() - 2));
			if (maleCounter >= 3)
				male = replaceLastComma(male);
			if (femaleCounter >= 3)
				female = replaceLastComma(female);
			if (maleCounter < 1)
				result = "no " + getRelationTypeBySetsName(set)[0] + "s, ";
			else
				result = maleCounter + " " + getRelationTypeBySetsName(set)[0] + "(s) - " + male + ", ";
			if (femaleCounter < 1)
				result += "no " + getRelationTypeBySetsName(set)[1] + "s";
			else
				result += femaleCounter + " " + getRelationTypeBySetsName(set)[1] + "(s) - " + female;
		} else
			result = "no " + getRelationTypeBySetsName(set)[2];
		return result;
	}

	String[] getRelationTypeBySetsName(HashMap<String, Person> set) {
		String[] result = { "", "", "" };
		if (set.equals(this.siblings)) {
			result[0] = "brother";
			result[1] = "sister";
			result[2] = "siblings";
		} else {
			result[0] = "son";
			result[1] = "daughter";
			result[2] = "kids";
		}
		return result;
	}

	String replaceLastComma(String t) {
		String result = "";
		String[] s = t.split(", ");
		for (String name : s) {
			result += name;
			if (name.equals(s[s.length - 1])) {
				result += " and ";
				continue;
			}
			result += ", ";
		}
		return result;
	}

	public String toString() {
		String t = this.name + " ";
		if (this.isAlive)
			t += "has ";
		else
			t += "had ";
		if (this.father != null)
			t += "father " + this.father.name + ", ";
		else
			t += "no father, ";
		if (this.mother != null)
			t += "mother " + this.mother.name + ", ";
		else
			t += "no mother, ";
		if (this.partner != null)
			t += "partner " + this.partner.name + ", ";
		else
			t += "no patner, ";
		t += getNamesFromSet(this.siblings) + ", ";
		t += getNamesFromSet(this.kids) + ".";
		return t;
	}
}
