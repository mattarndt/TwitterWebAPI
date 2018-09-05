import java.util.LinkedList;
import java.util.Queue;

public class BinarySearchTree implements Comparable<BinarySearchTree>{
	Node root;
	
	/*public void insert(String word){
		Node n1 = new Node(word);
		if(root == null){
			root = n1;
		} else {
			Node parIns = root;
			Node insNode = root;
			
			while()
		}
	}
	*/
	
	public void addNode(String word){
		Node newNode = new Node(word);
		if(root == null){
			root = newNode;
		} else {
			Node focusNode = root;
			Node parent;
	
			
			while(true){
				parent = focusNode;
				
				if(word.compareTo(focusNode.word) < 0){
					focusNode = focusNode.left;
					
					if(focusNode == null){
						parent.left = newNode;
						return;
					}
				} else if (word.compareTo(focusNode.word) > 0){
					focusNode = focusNode.right;
					
					if(focusNode == null){
						parent.right = newNode;
						return;
					}
				}
				else{
					parent.freq = parent.freq + 1;
					return;
				}
			}
		}
		
	}

	@Override
	public int compareTo(BinarySearchTree o) {
		return 0;
	}


	
	
}



class Node{
	int freq;
	String word;
	
	Node left;
	Node right;
	
	public Node(String word){
		this.freq = 1;
		this.word = word;
		left = null;
		right = null;
	}
	
	public String toString(){
		return "word = " + word + " freq = " + freq;
	}

}