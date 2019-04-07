import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

/**
* @author Esteban Serna
* @author Nick Jeffers
*/

public class PriorityQ 
{	
	private ArrayList<Tuple<String, Integer>> PQ;
	
	//Tuple class for the priority queue
	public class Tuple<V,P> 
	{
		private V val;
		private P key;

		public Tuple(V val, P key) 
		{
			this.val = val;
		    this.key = key;
		}
		
		public V getVal() { return val; }
		public P getKey() { return key; }
		
		public void setVal(V v) {val = v;}
		public void setKey(P k) {key = k;}
	}
		  
    public PriorityQ() 
    {
    	PQ = new ArrayList<Tuple<String, Integer>>();
    }
    
	public void add(String s, int p) 
    {   	   
    	Tuple<String, Integer> new_tuple = new Tuple<String, Integer>(s, p);
    	new_tuple.setVal(s);
    	new_tuple.setKey(p);   	
    	
    	PQ.add(new_tuple);
    	bottom_to_top(PQ, PQ.size(), PQ.size() - 1);  	   		   	 	   	
    }
    
    public String returnMax() 
    {
    	if(!PQ.isEmpty()) {
    		return PQ.get(0).getVal();
    	}
    	else
    		return null;
    }
    
    public String extractMax() 
    {
//    	if(!PQ.isEmpty())
//    	{
//    		String result = PQ.get(0).getVal();
//
//			PQ.remove(0);
//
//    		return result;
//    	}
//    	else
//    		return null;

		if(PQ.isEmpty()){
			return null;
		}
		String result = PQ.get(0).getVal();
		PQ.set(0,PQ.get(PQ.size()-1));
		PQ.remove(PQ.size()-1);
		heapify(PQ,PQ.size(),0);

		return result;
    	
    }
    
    public void remove(int i) 
    {
    	if(!PQ.isEmpty())
    	{   		
    		PQ.remove(i);
    		heapify(PQ, PQ.size(), i);
    	}  	
    }
    
    public void decrementPriority(int i, int k) 
    {
    	if(!PQ.isEmpty())
    	{
    		PQ.get(i).setKey(getKey(i) - k);
    		heapify(PQ, PQ.size(), i);
    	}   	
    }
    
    public int[] priorityArray() 
    {
    	int[] B = new int[PQ.size()];
    	
    	if(!PQ.isEmpty())
    	{
    		for(int i = 0; i < B.length; i++ )
    		{
    			B[i] = getKey(i);   			
    		}   
    		return B;
    	}
    	else
    		return null;	
    }

    public int getKey(int i) 
    {
    	return PQ.get(i).getKey();
    }

    public String getValue(int i) 
    {       
    	return PQ.get(i).getVal();             
    }

    public boolean isEmpty() 
    {
    	if(PQ.isEmpty()) {
			return true;
		}
		else
			return false;
    }

    /**
     * Helper method to heapify
     * 
     * @param n is size of heap and i is the root
     */
    void heapify(ArrayList<Tuple<String, Integer>> pq, int n, int i) 
    { 
        int largest = i; // Initialize largest as root 
        int left = 2*i + 1; 
        int right = 2*i + 2;  
  
        // If left child is larger than root 
        if (left < n && getKey(left) > getKey(largest)) 
            largest = left; 
  
        // If right child is larger than largest so far 
        if (right < n && getKey(right) > getKey(largest)) 
            largest = right; 
  
        // If largest is not root 
        if (largest != i) 
        { 
        	Collections.swap(pq, i, largest);
        	
            // Recursively call heapify
            heapify(pq, n, largest); 
        } 
    } 
    
    void bottom_to_top(ArrayList<Tuple<String, Integer>> pq, int n, int i)
    {   	
        if(i == 0){ //if i is at the root
        	return;
        }
        else
        {
        	int parent = 0;
        	//right child
        	if(i % 2 == 0)
        	{
        		if((i - 2) >= 0){
        			parent = (i - 2) / 2;
        		}
        	}
        	//left child
        	if(i % 2 == 1)
        	{
        		if(i - 1 >= 0){
        			parent = (i - 1) / 2;
        		}
        	}
        	if(pq.get(i).getKey() > pq.get(parent).getKey())
        	{
        		Tuple temp = pq.get(parent);
        		pq.set(parent, pq.get(i));
        		pq.set(i, temp);
        		bottom_to_top(pq, pq.size(), parent);
        	}
        }
    }
    
}



