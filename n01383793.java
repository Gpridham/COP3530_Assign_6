import java.util.*;
import java.io.*;

/*
		Gabriel Pridham
		N01383793
		
		COP3530
		Ken Martin
		
		Project 6 
		Linear and Quadratic HashTable

*/

public class n01383793
{
	public static void main( String[] args ) throws FileNotFoundException
	{
		
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);
		File file3 = new File(args[2]);
		Scanner inputOne = new Scanner( file1 ); // reads file name from console
		Scanner inputTwo = new Scanner( file2 );
		Scanner inputThree = new Scanner( file3 );
		
		// holds the data that will be hashed
		ArrayList<String> hashedContent = new ArrayList<String>(); 

			
		int value = 0; // ??
		while( inputOne.hasNextLine() )
		{
			
			String dataString = inputOne.nextLine(); // gets line
			if(dataString.length() != 0) // if not a blank line
				hashedContent.add(dataString);
		}					
		
		LinearProbe LHashTable = new LinearProbe( primeNumLargerThan( 2 * hashedContent.size() ) ); // creates hashtable 3 times the number of elements
		QuaHashTable QHashTable= new QuaHashTable( primeNumLargerThan( 2 * hashedContent.size() ) );
		
		// Inputting string into hash table
		for( int index = 0; index < hashedContent.size(); index++)
		{
			//System.out.println("Inserting: " + tempList.get(index) );
			LHashTable.insert( hashedContent.get(index) );
			QHashTable.insert( hashedContent.get(index));
		}
		
		System.out.println("Linear Probing Hash Table: ");
		LHashTable.displayTable();
		System.out.println("\nQuadratic Probing Hash Table: ");
		QHashTable.displayTable();
		
	
		ArrayList<String> searchStrings = new ArrayList<String>(); // strings that are used to search HashTables
		while( inputTwo.hasNextLine() )
		{
			String searchKey = inputTwo.nextLine();

			if(searchKey.length() != 0)
			{
				searchStrings.add(searchKey);
			}
		}		
		
		
		ArrayList<String> deleteStrings = new ArrayList<String>(); // strings to be deleted in hashtable
		while( inputThree.hasNextLine() )
		{
			String searchKey = inputThree.nextLine();
	
			if(searchKey.length() != 0)// if not a blank line
			{
				deleteStrings.add( searchKey );
			}

		}

		
		System.out.println("\nSearched Keys using Linear Probing: ");		
		LHashTable.searchList( searchStrings );// display info on searched Strings
		System.out.println("\nDeleted Keys using Linear Probing: ");
		LHashTable.deleteList( deleteStrings ); // display info on deleted Strings
		System.out.println(""); // prints newline
		
		System.out.println("\nSearched Key using Quadratic Probing: ");
		QHashTable.searchList( searchStrings );
		System.out.println("\nDeleted Keys using Quadratic Probing: ");
		QHashTable.deleteList( deleteStrings );
		System.out.println(""); // prints newline
		
		//LHashTable.displayTable();
		//QHashTable.displayTable(); // prints after strings have been deleted
		
	}
	
	public static int primeNumLargerThan(int n)
	{
		for(int i = n + 1; true; i++)
			if(isPrime(i))
				return i;
	}
	
	public static boolean isPrime(int n)
	{
		for(int j = 2; ( j*j <= n ); j++)
			if(n % j == 0)
				return false;
		return true;
	}
	
	
	
}

class Data
{
	private String m_key;
	private int m_probLength;
	private int m_index;
	
	/*
		Used for when finding or trying to delete a Node
	*/

	Data( int probLength )
	{
		m_probLength = probLength;
		m_key = null;
	}	
	Data( int index, String key, int probLength)
	{
		m_key = key;
		m_probLength = probLength;
		m_index = index;
	}
	
	public String toString()
	{
		return new String(m_index + " " + m_key + " " + m_probLength);
	}
	
	public String getKey(){	return m_key; }
	public int getLocation() { return m_index; }
	public int getProbLen() { return m_probLength; }
	
	
}


abstract class HashTable
{
	protected Data[] hashArray;
	protected int m_size;
	protected Data nonItem; // for deleted items

	private int totalSuccess = 0; // total number of successful finds
	private int totalSucProbLen = 0; // total accumulated prob length of successful finds
	private int averageSuccess = 0;
	
	private int totalFail = 0; // total number of failed finds
	private int totalFailProbLen = 0; // total accumulated prob length of failed finds
	private int averageFail = 0;

	
	HashTable(int size)
	{
		m_size = size;
		hashArray = new Data[m_size];
		
		//nonItem = new Data( -1, null, 0);

	    for( int index = 0; index < m_size; index++ )
		{
			hashArray[index] = null;
		}
	}
	
	public int hashFunc(String key)
	{
		int location = 0;
		
		for(int index = 0; index < key.length(); index++)
		{
			int letter = key.charAt(index) - 96; // gets character code
			// 26 for number of characters
			location  = (location * 26 + letter) % m_size;
		}
		
		return location;
	}
	
	public void displayTable()
	{
		System.out.println("Index String                                 Probe Length for Insertion");
		for(int index = 0; index < m_size; index++)
		{
			if( hashArray[index] != null && hashArray[index].getKey() != null)
			{
				int loc = hashArray[index].getLocation();
				String key = hashArray[index].getKey();
				int pLen = hashArray[index].getProbLen();
				System.out.println( String.format( "%-7d %-40s %d  ", loc, key, pLen));
			}
		}
	}

	
	
	// Used to get the stats of the average success
	public void searchList(ArrayList<String> list)	
	{
		System.out.printf("%-40s Success Failure Probe length for success  Probe length for failure\n", "String");		
		int probLen = 0;
		for(int index = 0; index < list.size(); index++ )
		{
			String searchKey = list.get( index );
			
			Data key = find( searchKey );
			if( key.getKey() != null)
			{
				probLen =	key.getProbLen();
				totalSuccess++; // total Number of succesfully found keys
				totalSucProbLen += probLen; // total tally of probe lengths.. used to get the average of successfully found keys
				System.out.printf("%-40s  yes                %d \n", searchKey, probLen); 
			//	System.out.println("SUCCESS: " + searchKey + " Prob length: " + probLen);
			//	System.out.println(searchKey + " yes               " + probLen); 
			}
			else
			{
				probLen = key.getProbLen();
				totalFail++;
				totalFailProbLen += probLen;		

				System.out.printf("%-40s          yes                                  %d \n", searchKey, probLen); 
			//	System.out.println("FAIl: " + searchKey + "Prob length: " + probLen);
			}
			
		}
		printStats();
		clear();
		
	}
	
	/*
		First uses the searchlist to get the Probe length averages
		Then deleted the key from the hashtable
	*/
	public void deleteList(ArrayList<String> list)
	{
		searchList( list );
		for( int index = 0; index < list.size(); index++ )
			 delete(list.get(index)); // deletes key from hashtable
	}
	
	public void clear()
	{
		totalSuccess = 0; // total number of successful finds
		totalSucProbLen = 0; // total accumulated prob length of successful finds
		averageSuccess = 0;
	
		totalFail = 0; // total number of failed finds
		totalFailProbLen = 0; // total accumulated prob length of failed finds
		averageFail = 0;
	}		
	
	/*
		Will have the stats in the HashTable class 
	*/	
	public void printStats()
	{
   	//System.out.println("Success: " + totalSucProbLen + " / " + totalSuccess);		
		//System.out.println("Fail: " + totalFailProbLen + " / " + totalFail);		
		System.out.println(String.format("\nAverage probe length:   %41.2f    %22.2f", (double)totalSucProbLen/ totalSuccess,  (double)totalFailProbLen / totalFail ));
	}	
	abstract public Data find(String str);
	abstract public void insert(String key);
	abstract public Data delete(String key);
	
	
}


class LinearProbe extends HashTable // linear hash table
{
	
	LinearProbe(int size)
	{
		super(size);
	}
	// returns DataLink if key found,
	// return null if not found
	
	
	/*
	// checks if there is a node already at hash locations
	public boolean collision(int location)
	{
		return hashArray[location].getFirst() != null;
	}
	*/
	
	// CHANGE TO INSERT STRING
	public void insert(String key)
	{
		
		int hashVal = hashFunc(key);  // hash the key
		int probeLength = 0;
									
		while(hashArray[hashVal] != null ) // until empty cell or -1, .. WHY was there originally hashArray[hashVal].getKey() != -1
		{
			//System.out.println("Attempting to store: " + key + ": " + hashVal); // debugging
			probeLength++;
			++hashVal;                 // go to next cell
			hashVal %= m_size;      // wraparound if necessary
		}
		
		//System.out.println(key + " Stored at location: " + hashVal + " probe length: " + probeLength + "\n" ); // debugging
		hashArray[hashVal] = new Data(hashVal, key, probeLength);    // insert item
	}  // end linearInsert()
		
	public Data find(String key)
	{
		int location = hashFunc( key );
		int probLength = 0;
		
		
		while( hashArray[location] != null )
		{
		
			if(hashArray[location].getKey() != null && hashArray[location].getKey().equals( key ))
			{
				return  hashArray[location];
			}
			++probLength;
			++location;
			location %= m_size;
		}
		
		return new Data( probLength); // will have null string
		
			//System.out.println("ERROR " + key.getKey() + " " + "already exists and location " + location);
		
		// return location of value
		// or return location not found
		// if end of array is reached, wrap aroud to beginning using %
	}	
		
	
	public Data delete(String key)
	{
		Data item = find( key );
		
		Data nonItem = new Data( -1); // need to make this represent a deleted node
		
		if(item.getKey() != null)
		{
			Data temp = item;
			hashArray[item.getLocation()] = nonItem;
			return temp;	
		}
		else
			return item; // returns a Data node with null key and the probLength found int the find method
	}

}


class QuaHashTable extends HashTable
{
	
	QuaHashTable(int size)
	{
		super(size);
	}
	
	  
// -------------------------------------------------------------
                          

	
   public void insert(String key)
	// (assumes table not full)
  {
	  int hashVal = hashFunc(key);  // hash the key
	  int stepSize =  1;
	  int probLength = 0;
									 // until empty cell or -1
	  while(hashArray[hashVal] != null ) //&& hashArray[hashVal].getKey() != -1)
		 {
			 hashVal += stepSize;        // add the step
			 hashVal %= m_size;       // for wraparound
			 ++probLength;
			
			 stepSize = (int) Math.pow( probLength + 1, 2);// 1 4 9 16 25 ...
		 }
	  hashArray[hashVal] = new Data(hashVal, key, probLength);     // insert item
  }  // end insert()
  
//-------------------------------------------------------------
  public Data find(String key)
  {

		int location = hashFunc( key );
		int stepSize = 1; // get step size
		int probLength = 0;
		
		while( hashArray[location] != null )
		{
			//System.out.println("Searching.. " + hashArray[location].toString());
			if(hashArray[location].getKey() != null && hashArray[location].getKey().equals( key ))
			{
				//System.out.println("Found.. " + hashArray[location].toString());
				return  hashArray[location];
			}
			++probLength;
			location += stepSize;
			location %= m_size;
			
			stepSize += Math.pow( probLength + 1, 2); // 1 4 9 16 25 ...
		}
		
		
		return new Data( probLength );
		
	}
	
//-------------------------------------------------------------
	public Data delete(String key )
	{

		Data item = find( key );
		
		Data nonItem = new Data(-1); // need to make this represent a deleted node
		
		if( item != null )
		{
			Data temp = item;
			hashArray[item.getLocation()] = nonItem;
			return temp;
		}
		else
			return item;
		
			
	}
	
//-------------------------------------------------------------
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


