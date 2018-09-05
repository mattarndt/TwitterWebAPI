import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main implements Comparator<String>{
    
    static ArrayList<String> alpha = new ArrayList<String>();
    static ArrayList<String> sortLength = new ArrayList<String>();
    
    
    public static void main(String[] args){
    	BinarySearchTree tree = new BinarySearchTree();
        ArrayList<String> hashArr = new ArrayList<>();
        ArrayList<String> hashArr2 = new ArrayList<>();
        ArrayList<String> hashArr3 = new ArrayList<>();
        
        TweetSearch.searchAndWriteFile();
        
        
        try(BufferedReader br = new BufferedReader(new FileReader("SearchResults.txt"))) {
			
        	for(String line; (line = br.readLine()) != null; ) {
        		if(line.contains("#")){
        			if(!line.contains("…")){
        				//extracts the hashtags and adds them to the BST
	        			Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
	        			Matcher mat = MY_PATTERN.matcher(line);
	        			while (mat.find()) {
	        				tree.addNode("#" + mat.group(1));
	        				//System.out.println("#" + mat.group(1));
	        				hashArr.add(mat.group(1));
	        				hashArr2.add(mat.group(1));
	        				hashArr3.add(mat.group(1));
	        			}
        			}
	        			
        		}
            }
		// line is not visible here.
        } catch (Exception e) {
        	
        }
		
        long startTime = System.nanoTime();
      
        //sorts the arraylist alphabetically ignoring case
        Collections.sort(hashArr, new Comparator<String>(){
            public int compare(String o1, String o2){
                return o1.compareToIgnoreCase(o2);
            }
        });
        
         
        System.out.println("sorted alphabeticaly:");
        System.out.println(hashArr);
        long endTime = System.nanoTime();
        System.out.println("Took "+(endTime - startTime) + " ns\n\n"); 
        
        startTime = System.nanoTime();
        System.out.println("sorted by length then by letter:");
        Collections.sort(hashArr2, new Main());
        System.out.println(hashArr2);
        endTime = System.nanoTime();
        System.out.println("Took "+(endTime - startTime) + " ns\n\n");
        
        startTime = System.nanoTime();
        hashArr3 = mergeSort(hashArr3);
        System.out.println("merge sorted arraylist, alphabetically but capitols first then lowercase:");
        System.out.println(hashArr3);
        endTime = System.nanoTime();
        System.out.println("Took "+(endTime - startTime) + " ns");
        /*
        ArrayList<Integer> randArr = new ArrayList<>();
        for(int i = 0; i < 100; i++){
        	randArr.add(getRandomNumberInRange(1,1000));
        }
        System.out.println(randArr);
        */
    }
    
    @Override
    public int compare(String o1, String o2) {  
      if (o1.length() > o2.length()) {
         return 1;
      } else if (o1.length() < o2.length()) {
         return -1;
      }
      return o1.compareTo(o2);
    }
    
     public static void compareLength(String a) {
        if (alpha.isEmpty()) {
            alpha.add(a);
        }//End of if
        else {
            int x = 0;
            boolean toggle = false;

            for (int i = 0; i < alpha.size(); i++) {

                if (alpha.get(i).compareToIgnoreCase(a) > 0) {
                    x = alpha.indexOf(alpha.get(i));
                    toggle = true;
                    break;
                }

            }
            if (( toggle == true) && (alpha.get(x).compareToIgnoreCase(a) > 0)) {

                if (((x - 1) < 0)) {
                    alpha.add(0, a);

                }//end of if x-1 <0
                else if (((x - 1) < 0) && (alpha.get(x).compareToIgnoreCase(a) < 0)) {
                    alpha.add(1, a);
                } else {
                    alpha.add(x, a);
                }
            }
        }
    }
     
     private static int getRandomNumberInRange(int min, int max) {

 		if (min >= max) {
 			throw new IllegalArgumentException("max must be greater than min");
 		}

 		Random r = new Random();
 		return r.nextInt((max - min) + 1) + min;
 	}
     
     public static ArrayList<String> mergeSort(ArrayList<String> whole) {
    	    ArrayList<String> left = new ArrayList<String>();
    	    ArrayList<String> right = new ArrayList<String>();
    	    int center;
    	 
    	    if (whole.size() == 1) {    
    	        return whole;
    	    } else {
    	        center = whole.size()/2;
    	        // copy the left half of whole into the left.
    	        for (int i=0; i<center; i++) {
    	                left.add(whole.get(i));
    	        }
    	 
    	        //copy the right half of whole into the new arraylist.
    	        for (int i=center; i<whole.size(); i++) {
    	                right.add(whole.get(i));
    	        }
    	 
    	        // Sort the left and right halves of the arraylist.
    	        left  = mergeSort(left);
    	        right = mergeSort(right);
    	 
    	        // Merge the results back together.
    	        merge(left, right, whole);
    	    }
    	    return whole;
    	}
     private static void merge(ArrayList<String> left, ArrayList<String> right, ArrayList<String> whole) {
    	    int leftIndex = 0;
    	    int rightIndex = 0;
    	    int wholeIndex = 0;
    	 
    	    // As long as neither the left nor the right ArrayList has
    	    // been used up, keep taking the smaller of left.get(leftIndex)
    	    // or right.get(rightIndex) and adding it at both.get(bothIndex).
    	    while (leftIndex < left.size() && rightIndex < right.size()) {
    	        if ( (left.get(leftIndex).compareTo(right.get(rightIndex))) < 0) {
    	            whole.set(wholeIndex, left.get(leftIndex));
    	            leftIndex++;
    	        } else {
    	            whole.set(wholeIndex, right.get(rightIndex));
    	            rightIndex++;
    	        }
    	        wholeIndex++;
    	    }
    	 
    	    ArrayList<String> rest;
    	    int restIndex;
    	    if (leftIndex >= left.size()) {
    	        // The left ArrayList has been use up...
    	        rest = right;
    	        restIndex = rightIndex;
    	    } else {
    	        // The right ArrayList has been used up...
    	        rest = left;
    	        restIndex = leftIndex;
    	    }
    	 
    	    // Copy the rest of whichever ArrayList (left or right) was not used up.
    	    for (int i=restIndex; i<rest.size(); i++) {
    	        whole.set(wholeIndex, rest.get(i));
    	        wholeIndex++;
    	    }
    	}
}