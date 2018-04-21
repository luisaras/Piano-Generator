import gen.Generator;

public class Main {

	static void testRandom() { new Generator("Random Test", 4); }
	static void testTemplate() { new Generator("Template2", "Template Test", 4); }
	static void testStatic() { new Generator("Static Test"); }
	
	public static void main(String[] args) {
		testStatic();
    }

}
