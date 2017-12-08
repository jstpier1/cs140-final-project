package project;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import project.Instruction;
import java.util.Map;
import java.util.TreeMap;

public interface Assembler
{
	public Set<String> noArgument = new TreeSet<String>(Arrays.asList("HALT", "NOP", "NOT"));
	
	public int assemble(String inputFileName, String outputFileName, StringBuilder error) 
	{
		/**
		 * Method to assemble a file to its executable representation. 
		 * If the input has errors one or more of the errors will be reported 
		 * the StringBulder. The errors may not be the first error in 
		 * the code and will depend on the order in which instructions 
		 * are checked. There is no attempt to report all the errors.
		 * The line number of the last error that is reported 
		 * is returned as the value of the method. 
		 * A return value of 0 indicates that the code had no errors 
		 * and an output file was produced and saved. If the input or 
		 * output cannot be opened, the return value is -1.
		 * The unchecked exception IllegalArgumentException is thrown 
		 * if the error parameter is null, since it would not be 
		 * possible to provide error information about the source code.
		 * @param inputFileName the source assembly language file name
		 * @param outputFileName the file name of the executable version  
		 * of the program if the source program is correctly formatted
		 * @param error the StringBuilder to store the description 
		 * of the error or errors reported. It will be empty (length 
		 * zero) if no error is found.
		 * @return 0 if the source code is correct and the executable 
		 * is saved, -1 if the input or output files cannot be opened, 
		 * otherwise the line number of a reported error.
		 */
		
		Scanner s = new Scanner(new File(inputFileName));
		ArrayList<String> code = new ArrayList<String>();
		ArrayList<String> data = new ArrayList<String>();
		boolean check = false;
		int offset = 0;
		
		while(s.hasNext())
		{
			String temp = s.next();
			if(temp.trim().equals("DATA"))
			{
				check = true;
				
			}
			else if(!check)
			{
				code.add(temp);
			}
			else
			{
				data.add(temp);
			}
		}
		
		
		
		boolean blank = false;
		int lineNumber;
		int counter = 0;
		int retVal = 0;

		if(error == null)
		{
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		}
		
		for(int i = 0; i<code.size(); i++)
		{
			String line = code.get(i);
			String[] parts = line.trim().split("\\s+");


			try 
			{
		        if((line.trim().length() == 0))
		        {
		        		blank = true;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		lineNumber = counter;
		        		throw new Exception();
		        }
		     }
			catch(Exception e)
			{
				error.append("\nIllegal blank line in the source file" + lineNumber);
				retVal = -1;
			}
			
			try
			{
				if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
				{
				     line = line.substring(1);
				}
			}
			catch(Exception e)
			{
				error.append("\nLine starts with illegal white space" + lineNumber);
				retVal = -1;
			} 
			
			try
			{
				if(line.trim().toUpperCase().equals("DATA"))
				{
					if(!(line.trim().equals("DATA")))
					{
						throw new Exception();

					}
					line.trim().split("\\s+");
				}
			
			}
			catch(Exception e)
			{
				error.append("\nLine does not have DATA in upper case" + lineNumber);
				retVal = -1;
			}
			
			try
			{
				if(!(opcodes.keySet().contains(parts[0])))
				{
					throw new Exception();
				}
			}
			catch(Exception e)
			{
				error.append("\nError on line " + (i+1) + ": illegal mnemonic");
				retVal = -1;
			}
			
			try
			{
				if(opcodes.toUpperCase().contains(parts[0]) && !(opcodes.contains(parts[0])))
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				error.append("\nError on line " + (i+1) + ": mnemonic must be upper case" + lineNumber);
				retVal = -1;
			}
			
			
			try
			{
				if(noArgument.contains(line.trim()))
				{
					if(parts.length != 1)
					{
						throw new Exception();
					}
				}
			}
			catch (Exception e)
			{
				error.append("\nError on line " + (counter+1) + ": this mnemonic cannot take arguments" + lineNumber);
				retVal = -1;
			}
			
			try
			{
				if((parts.length <= 2) && !(noArgument.contains(line.trim())))
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				error.append("\nError on line " + (i+1) + ": this mnemonic is missing an argument");
				retVal = -1;
			}
			

			try
			{
				if((parts.length >= 2) && !(noArgument.contains(line.trim())))
				{
					throw new Exception();
				}
			}
			catch (Exception e)
			{
				error.append("\nError on line " + (i+1) + ": this mnemonic has too many arguments");
				retVal = -1;
			}
			
			try
			{
				if(parts.length == 2)
				{
					int flags = 0;
					if (parts[1].charAt(0) == '#' || parts[1].charAt(0) == '@' || parts[1].charAt(0) == '&')
					{
						 parts[1] = parts[1].substring(1);
					}
					int arg = Integer.parseInt(parts[1],16);
					int opPart = 8*Instruction.opcodes.get(parts[1]) + flags;	
				}
			} 
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (i+1) + ": argument is not a hex number" + lineNumber);
				retVal = counter + 1;				
			}
			offset++;
			counter++;
			
		}
			
		
		counter = 0;
		for(int i = 0; i<data.size(); i++)
		{
			String line = data.get(i);
			String[] parts = line.trim().split("\\s+");
			
			try 
			{
		        if((line.trim().length() == 0))
		        {
		        		blank = true;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		lineNumber = counter;
		        		throw new Exception();
		        }
		     }
			catch(Exception e)
			{
				error.append("\nIllegal blank line in the source file" + lineNumber);
				retVal = -1;
			}
			
			try
			{
				if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
				{
				     line = line.substring(1);
				}
			}
			catch(Exception e)
			{
				error.append("\nLine starts with illegal white space" + lineNumber);
				retVal = -1;
			} 
			
			try
			{
				if((line.trim().equals("DATA")))
				{
					line.trim().split("\\s+");
					throw new Exception();
				}
			}
			catch(Exception e)
			{
				error.append("\nLine has a second separator" + lineNumber);
				retVal = -1;
			}
			
			try
			{
				int address = Integer.parseInt(parts[0],16);
				int value = Integer.parseInt(parts[1],16);
			}
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (offset+counter+1) + ": data has non-numeric memory address" + lineNumber);
				retVal = offset + counter + 1;				
			}
		
		counter++;
		
		}
		
		try
		{
			File output = new File(outputFileName);
			BufferedWriter bwr = new BufferedWriter(new FileWriter(output));
			bwr.write(error.toString());
			bwr.close();
		}
		catch (FileNotFoundException e)
		{
			error.append("\nError: Unable to write the assembled program to the output file");
			retVal = -1;
		} catch (IOException e)
		{
			error.append("\nUnexplained IO Exception");
			retVal = -1;
		}
		
		return retVal;
		
	}
	
	public class DataPair
	{
		protected int address;
		protected int value;
		
		public DataPair(int ad, int val)
		{
			address = ad;
			value = val;
		}
		
		public String toString()
		{
			return "DataPair (" + address + ", " + value + ")";
		}
	}
}

