package basic;

import com.google.common.primitives.Ints;

public class JDK7Feature {

	public static void contains() {
		int[] array = {1,2,3,4,5};
		System.out.println(Ints.contain(array, target:2));
		
	}
}
