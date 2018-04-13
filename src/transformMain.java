import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

public class transformMain {

	public static void main(String[] args) {

		// Read in the File

		System.out.println("Enter the name of your file: ");
		Scanner scanner = new Scanner(System.in);
		String fileName = scanner.nextLine().trim();
		scanner.close();

		try {
			File file = new File(fileName);
			Scanner input = new Scanner(file);

			String source;

			if (input.hasNextLine()){
				source = input.nextLine();
			}
			else{
				input.close();
				source="";
				throw new Exception("This file needs to be two lines of content.");
			}

			String destination;
			if (input.hasNextLine()){
				destination = input.nextLine();
			}
			else{
				input.close();
				destination="";
				throw new Exception("This file needs to be two lines of content.");
			}

			input.close();

			// Force the read in lines to be lower case letters only

			source = source.replaceAll("[^a-zA-Z]", "").toLowerCase();

			destination = destination.replaceAll("[^a-zA-Z]", "").toLowerCase();

			if(source.length() != destination.length()){
				throw new Exception("The two inputs must be of the same size!");
			}


			ArrayList<String> stepsTakenTotal = editDistance(source, destination);

			System.out.println("Steps taken: " + stepsTakenTotal.size() + " steps");
			System.out.println("Source: " + source);
			System.out.println("");

			for(int i = 0; i < stepsTakenTotal.size(); i++){
				System.out.println("Step " + (i + 1) + ": " + stepsTakenTotal.get(i));
			}
			System.out.println("");
			System.out.println("Destination: " + destination);

        } catch (java.io.FileNotFoundException ex) {
        	System.out.println("That file does not exist. Be sure to put the file extension and relative path from this program's folder or the absolute path.");
        }

		catch (Exception ex) {
            System.out.println("There was an error with your input.");
            System.out.println(ex);
        }


	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String> editDistance(String source, String destination) {



		int sourceLength = source.length();

		//Initialize memoized list
		ArrayList<ArrayList<String>> savedResults = new ArrayList<ArrayList<String>>();
		for(int i = 0; i <= sourceLength + 1; i++){
			savedResults.add(new ArrayList<String>());
		};

		String currentStep;

		for (int i = sourceLength - 1; i >= 0; i--){

			// As we start from the end, assume we'll calculate the first part of the string later, and proceed with what we have now
			// "Assume that I've already figured 0 to i out, now how do I solve the rest?"
			if (i > 0) {
				currentStep = destination.substring(0, i) + source.substring(i);
			} else {
				currentStep = source;
			}

			// If the string is in place, we don't need to do anything
			if(currentStep.charAt(i) == destination.charAt(i) ){
				savedResults.set(i, savedResults.get(i + 1));
				continue;
			}

			ArrayList<ArrayList<String>> choices = new ArrayList<ArrayList<String>>();

			// First we do our substitutions; we replace the current character and assume the rest were already solved
			ArrayList<String> stepsTaken = new ArrayList<String>();
			performSubstitution(currentStep, destination, i, stepsTaken);

			ArrayList<String> substitutionHistory = (ArrayList<String>) savedResults.get(i+1).clone();
			substitutionHistory.addAll(0, stepsTaken);

			choices.add(substitutionHistory);

			//Then we attempt to do all of our reversals; we need to check all possible cases of reversals
			for(int j = sourceLength - 1; j > i; j--){
				stepsTaken.clear();
				performReverse(currentStep, destination, i, j, stepsTaken);
				ArrayList<String> reverseHistory = (ArrayList<String>) savedResults.get(j+1).clone();

				reverseHistory.addAll(0, stepsTaken);

				choices.add(reverseHistory);
			}

			// Finally, we locate the minimum number of steps we took to get here
			ArrayList<String> minimumSteps = new ArrayList<String>();
			int minimumStepsLength = Integer.MIN_VALUE;
			for(ArrayList<String> choice : choices){
				int choiceSize = choice.size();
				if(minimumStepsLength < 0 || choiceSize < minimumStepsLength){
					minimumStepsLength = choiceSize;
					minimumSteps = choice;
				}
			}

			savedResults.set(i, minimumSteps);

		}

		return savedResults.get(0);

	}


	public static String performSubstitution(String source, String destination, int i, ArrayList<String> stepsTaken){

		String changedSource;

		if(source.charAt(i) != destination.charAt(i)){
			changedSource = source.substring(0, i) + destination.charAt(i) + source.substring(i+1);
			stepsTaken.add("Substitute character at index " + i + ": " + changedSource);
		}
		else {
			changedSource = source;
		}

		return changedSource;
	}

	public static String performReverse(String source, String destination, int start, int end, ArrayList<String> stepsTaken) {

		String changedSource;

		StringBuilder sb = new StringBuilder(source.substring(start, end + 1));
		changedSource = source.substring(0, start) + sb.reverse().toString() + source.substring(end + 1);

		stepsTaken.add("Reverse characters from index " + start + " to " + end + ":" + changedSource);

		for (int i = start; i <= end; i++){
			if(changedSource.charAt(i) != destination.charAt(i)) {
				performSubstitution(changedSource, destination, i, stepsTaken);
			}
		}
		return changedSource;
	}

}
