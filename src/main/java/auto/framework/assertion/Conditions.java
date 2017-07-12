package auto.framework.assertion;

public class Conditions {
	
	public static final Condition<Object> equals =  new Condition.Equals();
	public static final Condition<Object> notEquals =  new Condition.NotEquals();
	public static final Condition<String> equalsIgnoreCase =  new Condition.EqualsIgnoreCase();
	public static final Condition<String> notEqualsIgnoreCase =  new Condition.NotEqualsIgnoreCase();
	public static final Condition<String> contains =  new Condition.Contains();
	public static final Condition<String> notContains =  new Condition.NotContains();
	public static final Condition<String> containsIgnoreCase =  new Condition.ContainsIgnoreCase();
	public static final Condition<String> notContainsIgnoreCase =  new Condition.NotContainsIgnoreCase();
	public static final Condition<String> matches =  new Condition.Matches();
	public static final Condition<String> notMatches =  new Condition.NotMatches();
	public static final Condition<Object> isNull =  new Condition.IsNull();
	public static final Condition<Object> notIsNull =  new Condition.NotIsNull();
	public static final Condition<Object> isBlank =  new Condition.IsBlank();
	public static final Condition<Object> notIsBlank =  new Condition.NotIsBlank();
	
/*	private static class NegativeCondition<Expected> implements Condition<Expected> {

		private final Condition<Expected> positive;
		
		public NegativeCondition(Condition<Expected> positive){
			this.positive = positive;
		}
		
		@Override
		public String name() {
			return "negative<"+positive.name()+">";
		}

		@Override
		public Boolean verify(Expected expected, Object actual) {
			return positive.verify(expected, actual);
		}
		
	}	*/

	public static interface Condition<Expected> {
		
		public String name();
		public Boolean verify(Expected expected,Object actual);

		static class Equals implements Condition<Object> {	
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return expected==actual || (expected!=null && expected.equals(actual));
			}

			@Override
			public String name() {
				return "equals";
			}
		};
		
		public static class NotEquals extends Equals {	
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "does not equal";
			}
		};

		public static class EqualsIgnoreCase implements Condition<String> {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return expected.equalsIgnoreCase(String.valueOf(actual));
			}

			@Override
			public String name() {
				return "equals (ignore case)";
			}
		};

		public static class NotEqualsIgnoreCase extends EqualsIgnoreCase {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "does not equal (ignore case)";
			}
		};

		public static class Contains implements Condition<String> {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return actual!=null && String.valueOf(actual).contains(expected);
			}

			@Override
			public String name() {
				return "contains";
			}
		};

		public static class NotContains extends Contains {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "does not contain";
			}
		};


		public static class ContainsIgnoreCase implements Condition<String> {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return actual!=null && String.valueOf(actual).toLowerCase().contains(String.valueOf(expected).toLowerCase());
			}

			@Override
			public String name() {
				return "contains (ignore case)";
			}
		};

		public static class NotContainsIgnoreCase extends ContainsIgnoreCase {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "does not contain (ignore case)";
			}
		};

		public static class Matches implements Condition<String> {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return actual!=null && String.valueOf(actual).matches(expected);
			}

			@Override
			public String name() {
				return "matches";
			}
		};
		
		public static class NotMatches extends Matches {	
			
			@Override
			public Boolean verify(String expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "does not match";
			}
		};
		
		public static class IsNull implements Condition<Object> {		
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return actual==null;
			}

			@Override
			public String name() {
				return "is null";
			}
		};

		public static class NotIsNull extends IsNull {	
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "is not null";
			}
		};
		
		public static class IsBlank implements Condition<Object> {		
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return actual==null||String.valueOf(actual).equals("");
			}

			@Override
			public String name() {
				return "is blank";
			}
		};

		public static class NotIsBlank extends IsNull {	
			
			@Override
			public Boolean verify(Object expected, Object actual) {
				return !super.verify(expected, actual);
			}

			@Override
			public String name() {
				return "is not blank";
			}
		};
		
	}
	
}


