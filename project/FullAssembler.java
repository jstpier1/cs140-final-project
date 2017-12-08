package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FullAssembler implements Assembler
{
	public int assemble(String inputFileName, String outputFileName, StringBuilder error)
	{
		boolean blank = false;
		int counter = 1;
		int retVal = 0;
		Scanner s = null;
		
		if(error == null)
		{
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		}
		
		File inputFile;
		try {
			inputFile = new File(inputFileName);
			s = new Scanner(inputFile);
		} catch (FileNotFoundException e1) {
			error.append("\nError: Unable to open the input file");
			retVal = -1;
		}
		ArrayList<String> code = new ArrayList<String>();
		ArrayList<String> data = new ArrayList<String>();
		boolean check = false;
		int offset = 0;
		
		while(s.hasNextLine())
		{
			String temp = s.nextLine();
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
		
		s.close();
		System.out.println(code);
		String[] parts;
		for(int i = 0; i<code.size(); i++)
		{
			String line = code.get(i);
			parts = line.trim().split("\\s+");
			System.out.println(Arrays.toString(parts));
	
			try 
			{
		        if((line.trim().length() == 0))
		        {
		        		blank = true;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		error.append("\nIllegal blank line in the source file" + counter);
					retVal = -1;
				}
		  
		        else if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
				{
				     line = line.substring(1);
				     error.append("\nLine starts with illegal white space" + counter);
				     retVal = -1;
				}

		        else if(line.trim().toUpperCase().equals("DATA"))
				{
					if(!(line.trim().equals("DATA")))
					{
						error.append("\nLine does not have DATA in upper case" + counter);
						retVal = -1;	
					}
					//line.trim().split("\\s+");
				}
			
		        if((parts[0].length() != 0))
		        	{
		        		if(!(((Instruction.opcodes).keySet()).contains(parts[0])))
					{
						error.append("\nError on line " + (i+1) + ": illegal mnemonic");
						retVal = -1;	
					}
					
			        else if(Instruction.opcodes.keySet().contains(parts[0].toUpperCase()) && !(Instruction.opcodes.keySet().contains(parts[0])))
					{
						error.append("\nError on line " + (i+1) + ": mnemonic must be upper case" + counter);
						retVal = -1;	
					}
				
			        else if(noArgument.contains(parts[0]))
					{
						if(parts.length != 1)
						{
							error.append("\nError on line " + (counter+1) + ": this mnemonic cannot take arguments" + counter);
							retVal = -1;	
						}
					}
				
					else if((parts.length > 2))
					{
						error.append("\nError on line " + (i+1) + ": this mnemonic has too many arguments");
						retVal = -1;	
					}
					
					else if((parts.length < 2))
					{
						error.append("\nError on line " + (i+1) + ": this mnemonic is missing an argument");
						retVal = -1;
					}
		        	}
				else if(parts.length == 2)
				{
					int flags = 0;
					 if (parts[1].charAt(0) == '#')
					 {
						 flags = 2; 
						 parts[1] = parts[1].substring(1);
					 }
					 else if(parts[1].charAt(0) == '@')
					 {
						 flags = 4;
						 parts[1] = parts[1].substring(1);
					 }
					 else if (parts[1].charAt(0) == '&')
					 {
						 flags = 6;
						 parts[1] = parts[1].substring(1);
					 }
					int arg = Integer.parseInt(parts[1],16);
					int opPart = 8*Instruction.opcodes.get(parts[0]) + flags;	
					opPart += Instruction.numOnes(opPart)%2;
				}
			} 
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (i+1) + ": argument is not a hex number" + counter);
				retVal = counter + 1;				
			}
			offset++;
			counter++;
			
		}
			
		
		counter = 1;
		for(int i = 0; i<data.size(); i++)
		{
			String line = data.get(i);
			parts = line.trim().split("\\s+");
			
			try 
			{
		        if((line.trim().length() == 0))
		        {
		        		blank = true;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		error.append("\nIllegal blank line in the source file" + counter);
					retVal = -1;
		        }
		
		        else if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
				{
				     line = line.substring(1);
				     error.append("\nLine " + counter + " starts with illegal white space" );
				     retVal = -1;
				}
			
		        else if((line.trim().equals("DATA")))
				{
					line.trim().split("\\s+");
					error.append("\nLine" + counter + " has a second separator" );
					retVal = -1;	
				}
		
		        else if(parts.length != 2)
				{
					error.append("\nLine " + counter + " does not have two parts ");
					retVal = -1;		
				}
				else
				{
					int address = Integer.parseInt(parts[0],16);
					int value = Integer.parseInt(parts[1],16);
				}
			}
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (offset+counter+1) + ": data has non-numeric memory address" + counter);
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
}
