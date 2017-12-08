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
		int offset = 1;
		
		while(s.hasNextLine())
		{
			String temp = s.nextLine();
			if(temp.trim().equals("DATA"))
			{
				check = true;	
				data.add(temp);
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
		int blankLine = -1;
		for(int i = 0; i<code.size(); i++)
		{
			String line = code.get(i);
			parts = line.trim().split("\\s+");
			System.out.println(Arrays.toString(parts));
	
			try 
			{
		        if((line.trim().length() == 0) && blank == false)
		        {
		        		blank = true;
		        		blankLine = counter;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		error.append("\nError on line " + blankLine + ": Illegal blank line in the source file");
		        		retVal = counter;
		        		blank = false;
				}

		        else if(line.trim().toUpperCase().equals("DATA"))
				{
					if(!(line.trim().equals("DATA")))
					{
						error.append("\nLine " + counter + " does not have DATA in upper case");
						retVal = counter;	
					}
					//line.trim().split("\\s+");
				}
			
		        if((parts[0].length() != 0))
		        	{

				        if (line.charAt(0) == ' ' || line.charAt(0) == '\t')
						{
						     line = line.substring(1);
						     error.append("\nLine " + counter + " starts with illegal white space");
						     retVal = counter;
						}
				        
				        else if(Instruction.opcodes.keySet().contains(parts[0].toUpperCase()) && !(Instruction.opcodes.keySet().contains(parts[0])))
						{
							error.append("\nError on line " + counter + ": mnemonic must be upper case");
							retVal = counter;	
						}
				        
				        else if(!(((Instruction.opcodes).keySet()).contains(parts[0])))
						{
							error.append("\nError on line " + counter + ": illegal mnemonic");
							retVal = counter;	
						}
					
				        else if(noArgument.contains(parts[0]))
						{
							if(parts.length != 1)
							{
								error.append("\nError on line " + (counter) + ": this mnemonic cannot take arguments");
								retVal = counter;	
							}
						}
					
						else if((parts.length > 2))
						{
							error.append("\nError on line " + counter + ": this mnemonic has too many arguments");
							retVal = counter;	
						}
						
						else if((parts.length < 2))
						{
							error.append("\nError on line " + counter + ": this mnemonic is missing an argument");
							retVal = counter;
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
			} 
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + counter + ": argument is not a hex number");
				retVal = counter;				
			}
			offset++;
			counter++;
			
		}
			
		
		//counter++; Is this right?
		for(int i = 0; i<data.size(); i++)
		{
			String line = data.get(i);
			parts = line.trim().split("\\s+");
			
			try 
			{
		        if((line.trim().equals("DATA")) && i != 0)
				{
					line.trim().split("\\s+");
					error.append("\nLine " + counter + " has a second separator" );
					retVal = counter;	
				}
		        if((line.trim().length() == 0) && blank == false)
		        {
		        		blank = true;
		        		blankLine = counter;
		        }
		        else if(blank == true && (line.trim().length() != 0))
		        {
		        		error.append("\nError on Line " + blankLine + ": Illegal blank line in the source file ");
		        		retVal = -1;
		        		blank = false;
				}
		
		        else if (parts[0].length() != 0 && (line.charAt(0) == ' ' || line.charAt(0) == '\t'))
				{
				     line = line.substring(1);
				     error.append("\nLine " + counter + " starts with illegal white space" );
				     retVal = counter;
				}
		        
				else if (parts[0].length() != 0 && i != 0)
				{
					int address = Integer.parseInt(parts[0],16);
				}
		
		        else if(parts.length != 2 && parts[0].length() != 0 && i != 0)
				{
					error.append("\nLine " + counter + " does not have two parts ");
					retVal = counter;		
				}
		        
			}
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (counter) + ": data has non-numeric memory address");
				retVal = counter;				
			}
			try
			{
				if (parts[0].length() != 0 && i != 0)
				{
					int value = Integer.parseInt(parts[1],16);
				}
			}
			catch(NumberFormatException e)
			{
				error.append("\nError on line " + (counter) + ": data has non-numeric value");
				retVal = counter;				
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				error.append("\nError on line " + (counter) + ": data is missing a value");
				retVal = counter;				
			}
		
			counter++;
		
		}
		
		try
		{
			if(retVal == 0)
			{
				SimpleAssembler makeOutput = new SimpleAssembler();
				makeOutput.assemble(inputFileName, outputFileName, error);
			}
			else
			{
				File output = new File(outputFileName);
				BufferedWriter bwr = new BufferedWriter(new FileWriter(output));
				bwr.write(error.toString());
				bwr.close();
			}
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
