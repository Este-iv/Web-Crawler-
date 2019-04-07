import javax.annotation.Generated;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
* @author Esteban Serna
* @author Nick Jeffers
*/
// Current score...
// So far the highest 385 ... now 435, new score: 450.

// Does the max help out with that?

//focused
//correctness with <max -1>: points 441.. correctness 321
// without: 450 .. correctness 330/380..


/**
 * What was wrong with the code & What got fixed:
 *
 * the code was adding things and taking things out of the stack in an odd way.
 * We had to pin point what the issue was for the more general cases as it would provide a
 * bigger insight to what is needed to be done.
 *
 * Priority queue:- Extract max, it as not preforming the right tasks. So it had to be redone for
 * the sake of the code.
 *
 * Unfocused:- Did not have as many issue with it, as it was already optimized and there was not much
 * generalization needing to be done.
 *
 * Focused:- There where a lot of issues with in the if-statements, where something should be false or
 * vise versa, and it would preform the wrong thing, or just even skip over important if blocks that are needed.
 * Relevance and figuring out how it was adding it into the stack, as some of the things didnt add up. Which was lead
 * to there being issues with if statements and what was being returned.
 *
 * Right now the issues is faced with still the relevance but how to keep track of it. It seems that both
 * focused and unfocused are suffering form the issue. Mostly focused as it is looking to follow and interesting path.
 *
 * So the question is, with the code in front, how to follow such a path? or how to get it to do it, if it
 * should be doing that and it is being skipped..
 *
 */




/**
 * Going over the issues in this..
 * There looks to be a wrapping issue
 * Given the final text file and compared to the final output, everything is there but out of order/
 * looking like it is wrapped... for some odd reason in N1 : N1 M1 is first before N1 L1.
 *
 * Looking at the smaller things it looks to be incremented by one as well
 * E.i.
 * 		wikiWW(expected) wikiXX(outputted) ...
 * 	this is seen in all of the ones that are to be single 	checks or those of multiple checks..
 */

public class WikiCrawler 
{
//	static final String BASE_URL = "https://en.wikipedia.org/";
    static final String BASE_URL="http://web.cs.iastate.edu/~pavan/";
    
    String seedUrl;
    int max;    
    String[] topics;
    String fileName; 
    
    int count;
    PriorityQ queue = new PriorityQ();
    ArrayList<String> visited;
    
    String subUrl;
 	boolean[] words;
 	int rel = 100000;
 	
    public WikiCrawler(String seed, int max, String[] topics, String output)
    {
        this.seedUrl = seed; //related address of seed URL
        this.max = max; //maximum number of pages to consider
        this.topics = topics; //array of strings representing keywords in a topic-list
        this.fileName = output; //string representing the filename where the web graph over 
        						//discovered pages are written
      
        this.count = 0;
   	 	this.queue = new PriorityQ();
   	 	this.visited = new ArrayList<String>();
   	 	
   	 	this.subUrl = "";
   	 	this.words = new boolean[topics.length];
    }


    public boolean extractLinksHelper(String url, int topicsCount) throws IOException{

		URL url2 = new URL(BASE_URL + url);
		InputStream is = url2.openStream();
		String html = "";
		String tempread = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			tempread = br.readLine();
			while (tempread != null) {
				html += tempread;
				tempread = br.readLine();
			}
		}catch(Exception e){
			System.out.println("Invalid Link");
		}

		if(count % 20 == 0)
		{
			try {
				Thread.sleep(3000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		int sum = 0;

		for(int j = 0; j < topics.length; j++)
		{

			sum += count(html,topics[j]);

		}
		if(sum == topics.length)
			return true;

		return false;

	}


   // parse the string doc and return a list of links from the HTML document 
    public ArrayList<String> extractLinks(String document)
    {         	    	    
    	String doc = document;	 		
	 	ArrayList<String> links = new ArrayList<String>();	 		
	 		
	 	//read up to first occurrence of <p>
	 	boolean out = false;
	 		
	 	while(out == false)
	 	{
	 		if(doc.contains("<p>")){
	 			out = true;
	 		}
	 		else{
	 			return new ArrayList<String>();
	 		}
	 	}
	 		 	
 		subUrl = "";
		 
 		if(doc.length() > 0)
 		{
 			//check for words
 			for(int j = 0; j < topics.length; j++)
 			{

 				if(doc.contains(topics[j])){
 					words[j] = true;
 				}

 			}	
			 
 			//read for links
			/**
			 * Is the issues in here?
			 */
 			for(int i = 0; i < doc.length(); i++)
			{
 				if(doc.charAt(i) == '"' && doc.charAt(i+1) == '/')
				{
					i += 2;
					while(doc.charAt(i) != '"')
					{
						
						subUrl += doc.charAt(i);
						i++;
							 
						if(i >= doc.length()){
							break;
						}
					}
					 if(subUrl.length()>4)
					 {

						 if(subUrl.substring(0, 4).equals("wiki") && subUrl.contains("#") == false && subUrl.contains(":") == false)
						 {

							 links.add(subUrl);
						 }
					 }
					 subUrl="";
				}
			 }	 		
	 	}       
        return links;
    }
     
    public void crawl(boolean focused) throws InterruptedException, IOException
    {
        if(focused) {
        	crawl_focused();
        }
        else {
        	crawl_unfocused();
        }      
    }
   
  //explores the web pages using relevance and the priority queue
    private void crawl_focused() throws IOException 
    {
		//setup file writer
   	 	String graph = "";
   	 	FileWriter fw = null;
   	 	File f = new File(fileName);
   	 	
   	 	try {
   	 		fw = new FileWriter(f);
	 	} 
	 	catch (IOException e1) {
	 		e1.printStackTrace();
	 	}
   	  		 	
   	 	//access first page;
		String seed = seedUrl.substring(1);
   	 	queue.add(seed, relevance(seedUrl));
   	 	visited.add(seed);
   	 	
   	 	//access all pages
   	 	while(!queue.isEmpty())
	 	{
   	 		if(this.count % 20 == 0)
	 		{
	 			try {
	 				Thread.sleep(3000);
	 			}
	 			catch (InterruptedException e) {
	 				e.printStackTrace();
	 			}
	 		}
   	 		
   	 		String currentUrl = queue.getValue(0); //get link with max relevance
   	 		URL url = new URL(BASE_URL + queue.extractMax());
	 		InputStream is = url.openStream();
	 		String html = "";
	 		BufferedReader br = new BufferedReader(new InputStreamReader(is));
	 		
	 		String tempread="";
	 		tempread=br.readLine();
	 		while(tempread!=null){
	 			html+=tempread;
	 			tempread=br.readLine();
	 		}
	 		
   	 		ArrayList<String> tempVisited = new ArrayList<String>();
	 		tempVisited.add(currentUrl);
	 		
	 		ArrayList<String> links = extractLinks(html);
	 				
	 		boolean goodpage = true;
   	 		for(int x = 0; x < words.length; x++)
   	 		{
   	 			if(words[x] == false){
   	 				goodpage = false;
   	 			}
   	 		}

   	 		for(int i = 0; i < links.size(); i++){

   	 			if(!(relevance(links.get(i)) >= topics.length)){
   	 				links.remove(i);
   	 				i=0;
				}
			}

			/**
			 * Is There is an issue here as all those that need to be visited are not.. ?
			 *
			 */

			count = 0;
   	 		if(goodpage)
	 		{
	 			int linkssize = links.size();
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(links.size() > 0) {
						if (count < max ) //add to queue and visited
						{
							if (!tempVisited.contains(links.get(0))) {
								if (currentUrl.charAt(0) != '/') {
									graph += "/" + currentUrl + " /" + links.get(0) + "\n";
								} else {
									graph += currentUrl + " /" + links.get(0) + "\n";
								}
								tempVisited.add(links.get(0));
							}
							if (links.size() > 0 && !visited.contains(links.get(0))) {
								queue.add(links.get(0), relevance(links.get(0)));
								count++;
								visited.add(links.remove(0));
							} else {
								if (links.size() > 0)
									links.remove(0);

								// Issue is around links.remove()
								// that is where the issue is..
								// The issue exist in if statement of it being a good page.
							}

						} else //don't mess with queue or visited anymore, to put in file must be in visited.
						{
							boolean test = visited.contains(links.get(0));
							boolean test2 = !tempVisited.contains(links.get(0));


							if (visited.contains(links.get(0)) && !tempVisited.contains(links.get(0))) {
								if (currentUrl.charAt(0) != '/') {
									graph += "/" + currentUrl + " /" + links.get(0) + "\n";
								} else {
									graph += "/" + currentUrl + " /" + links.get(0) + "\n";
								}
								tempVisited.add(links.get(0));
							}
							else if(!visited.contains(links.get(0))){
								visited.add(links.get(0));
							}
							else
								links.remove(0);


						}
					}
	 			}
	 		}


	 		if(goodpage && count < max)
	 		{
	 			int linkssize = links.size();
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(!tempVisited.contains(links.get(0)))
	 				{
	 					graph += "/"+currentUrl + " /" + links.get(0) + "\n";
	 					tempVisited.add(links.get(0));
	 				}
	 				if(!visited.contains(links.get(0)))
	 				{
	 					if(count<max)
	 					{
	 						queue.add(links.get(0), relevance(links.get(0)));
	 						count ++;
	 					}
	 					visited.add(links.remove(0));
	 				}
	 				else{
	 					links.remove(0);
	 				}
	 			}
	 		}

			/**
			 * Would the issue be here for the addition of a path?
			 */

			else if(goodpage)
	 		{
	 			int linkssize=links.size();
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(!tempVisited.contains(links.get(0)))
	 				{
	 					if(visited.contains(links.get(0)))
	 					{

	 						graph += " /" +currentUrl + " /" + links.get(0) + "\n";
	 						tempVisited.add(links.remove(0));
	 					}

	 				}
	 			}
	 		}
	 	}
   	 	fw.write((count+1) + "\n"+graph);
	 	fw.close();
    }














	//explores the web pages using BFS
    private void crawl_unfocused() throws IOException
    {
    	//setup file writer
   	 	String graph = "";
   	 	FileWriter fw = null;
   	 	File f = new File(fileName);
   	 	
   	 	try {
   	 		fw = new FileWriter(f);
	 	} 
	 	catch (IOException e1) {
	 		e1.printStackTrace();
	 	}
   	  		 	
   	 	//access first page;
		String seed = seedUrl.substring(1);
   	 	queue.add(seed, rel--);
   	 	visited.add(seed);
   	 	
   	 	//access all pages
   	 	while(!queue.isEmpty())
	 	{

   	 		if(count % 20 == 0)
	 		{
	 			try {
	 				Thread.sleep(3000);
	 			}
	 			catch (InterruptedException e) {
	 				e.printStackTrace();
	 			}
	 		}
   	 		
   	 		String currentUrl = queue.getValue(0); //get link with max relevance
   	 		URL url = new URL(BASE_URL + queue.extractMax());
	 		InputStream is = url.openStream();
	 		String html = "";
	 		BufferedReader br = new BufferedReader(new InputStreamReader(is));
	 		
	 		String tempread="";
	 		tempread=br.readLine();
	 		while(tempread!=null){
	 			html+=tempread;
	 			tempread=br.readLine();
	 		}
	 		
   	 		ArrayList<String> tempVisited = new ArrayList<String>();
	 		tempVisited.add(currentUrl);
	 		
	 		ArrayList<String> links = extractLinks(html);
	 				
	 		boolean goodpage = true;
   	 		for(int x = 0; x < words.length; x++)
   	 		{
   	 			if(words[x] == false){
   	 				goodpage = false;
   	 			}
   	 		}
   	 		
   	 		if(goodpage)
	 		{
	 			int linkssize = links.size();
	 			
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(count < max -1) //add to queue and visited
	 				{
	 					if(!tempVisited.contains(links.get(0)))
	 					{
	 						if(!currentUrl.equals(links.get(0))){
	 						if(currentUrl.charAt(0) != '/')
	 						{
	 							graph +="/" + currentUrl + " /" + links.get(0) + "\n";
	 						}
	 						else {
	 							graph += currentUrl + " /" + links.get(0) + "\n";
	 						}
	 						}
	 						tempVisited.add(links.get(0));
	 					}
	 					if(!visited.contains(links.get(0)))
	 					{
	 						queue.add(links.get(0), rel--);
	 						count++;
	 						visited.add(links.remove(0));
	 					}
	 					else{
	 						links.remove(0);
	 					}
	 				}
	 				else //don't mess with queue or visited anymore, to put in file must be in visited.
	 				{
	 					boolean check_visited = visited.contains(links.get(0));
	 					boolean check_temp = tempVisited.contains(links.get(0));
	 					if(check_visited && !check_temp)
	 					{
	 						if(!currentUrl.equals(links.get(0))){
	 						if(currentUrl.charAt(0) != '/')
	 						{
	 							graph +="/" + currentUrl + " /" + links.get(0) + "\n";
	 						}
	 						else {
	 							graph += currentUrl + " /" + links.get(0) + "\n";
	 						}
	 						}
	 						tempVisited.add(links.get(0));
	 					}
	 					links.remove(0);
	 				}
	 			}
	 		}
		 
	 		if(goodpage && count < max)
	 		{
	 			int linkssize = links.size();
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(!tempVisited.contains(links.get(0)))
	 				{
	 					graph += currentUrl + " " + links.get(0) + "\n";
	 					tempVisited.add(links.get(0));
	 				}
	 				if(!visited.contains(links.get(0)))
	 				{
	 					if(count<max)
	 					{
	 						queue.add(links.get(0), rel--);
	 						count++;
	 					}
	 					visited.add(links.remove(0));
	 				}
	 				else{
	 					links.remove(0);
	 				}
	 			}
	 		}
	 		
	 		else if(goodpage)
	 		{
	 			int linkssize=links.size();
	 			for(int y = 0; y < linkssize; y++)
	 			{
	 				if(!tempVisited.contains(links.get(0)))
	 				{
	 					if(visited.contains(links.get(0)))
	 					{
	 						graph += currentUrl + " " + links.get(0) + "\n";
	 						tempVisited.add(links.remove(0));
	 					}
	 				}
	 			}
	 		}
	 	}
   	 	  	 	
   	 	fw.write((count + 1) + "\n"+graph);
	 	fw.close();
    }
    
    public int relevance(String page) throws IOException
    {
    	String currentUrl = page;
	 	URL url = new URL(BASE_URL + currentUrl);
 		InputStream is = url.openStream();
 		count++;
 		String html = "";
 		BufferedReader br = new BufferedReader(new InputStreamReader(is));
 		
 		String tempread="";
 		tempread=br.readLine();
 		while(tempread!=null){
 			html+=tempread;
 			tempread=br.readLine();
 		}
 		
 		if(count % 20 == 0)
	 	{
	 		try {
	 			Thread.sleep(3000);
	 		}
	 		catch (InterruptedException e) {
	 			e.printStackTrace();
	 		}
	 	}
 		
    	int sum = 0;
    	
    	for(int j = 0; j < topics.length; j++)
		{

				sum += count(html,topics[j]);


		}
    	
    	return sum;
    }
    
    public static int count(String text, String find) 
    {
        int index = 0;
        int count = 0;
        int length = find.length();
        
        while( (index = text.indexOf(find, index)) != -1 ) {  
                index += length; count++;
        }
        return count;
    }
    
    
}


