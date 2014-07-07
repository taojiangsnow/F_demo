package DataInput.Loaders.TrainData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import Context.Context;
import DataInput.Filters.SampleByTimeInterval;
import Main.Maskit;
import Utils.Sequence;
import Utils.Date;
import Utils.PreferredSettings;

public class MITtextLoader extends TxtLoader{
	static boolean NONZERO = true;
	
	public MITtextLoader(String f) {
		super(f);
	}
	
	@Override
	public void getDataSet() throws IOException {
		// TODO Auto-generated method stub
		int sumlines = getNumOfData(); //number of data of one user
		BufferedReader br;
		br = new BufferedReader(new FileReader(filename));
		
		String line;
		StringTokenizer tokenizer;
		String context = null;
		Date date = new Date();
		Date curdate = new Date();
		Sequence s = null;
		int year = 0;
		int month = 0;
		int day = 0;
		int hour = 0;
		int minute = 0;
		int sec = 0;
		int curlocid = 0;
		int lineno = 1;
		
		/*
		Maskit.locids = new ArrayList<Integer>(); //a set of location id
		Maskit.scontext_id = new HashMap<String,Integer>(); 
		Maskit.locids.add(Maskit.start_index); //"start"
		Maskit.scontext_id.put("start", -1);
	    Maskit.scontext_id.put("end", -2);
	    */
		
		System.out.println("lines "+sumlines);
		
		line = br.readLine();
		StringBuilder builder_context;
		while((line != null) && (lineno <= sumlines/2)) //user first half data to train
		//while(line != null)
		{   
			tokenizer = new StringTokenizer(line);
			builder_context = new StringBuilder();
			try {
				System.out.println("lineno "+lineno);
				year = Integer.parseInt(tokenizer.nextToken());
				month = Integer.parseInt(tokenizer.nextToken());
				day = Integer.parseInt(tokenizer.nextToken());
				hour = Integer.parseInt(tokenizer.nextToken());
				minute = Integer.parseInt(tokenizer.nextToken());
				sec = Integer.parseInt(tokenizer.nextToken());
				
				/*builder_context includes area id and tower id*/
				builder_context.append(tokenizer.nextToken());
				String temp = tokenizer.nextToken();
				if (temp.length() > 1) {
					builder_context.append(temp);
					builder_context.deleteCharAt(builder_context.indexOf(".")-1);	
				} else {
					builder_context.append(".");
					builder_context.append(temp);
				}
				context = builder_context.toString();
				builder_context = null;
				
				curlocid = Integer.parseInt(tokenizer.nextToken());				
			} catch(NumberFormatException exception) {
		        System.out.println("Error in input. Line ignored: " +line);
		    }
			
			Context newc = new Context(context, year, month, day, hour, minute, sec, curlocid);
			curdate.setDate(year, month, day, hour, minute, sec);
					
			if ((date == null) || (!curdate.equals(date))) {
				s = new Sequence();
				if ((curlocid != 0) && (hour >= ((SampleByTimeInterval)PreferredSettings.filter).getStartHour()) 
						&& (hour < ((SampleByTimeInterval)PreferredSettings.filter).getEndHour())) {
					s.addContext(newc);
				}
				if (s.getLength() != 0) {
					observation.add(s);
				}
				date.setDate(curdate.getYear(), curdate.getMonth(), curdate.getDay(), curdate.getHour(), curdate.getMinute(), curdate.getSec());				
			} else {
				if ((curlocid != 0) && (hour >= ((SampleByTimeInterval)PreferredSettings.filter).getStartHour()) 
			            && (hour < ((SampleByTimeInterval)PreferredSettings.filter).getEndHour())) {
					s.addContext(newc);	
				}				
			}
						
		    line = br.readLine();
		    lineno += 1;		    
		 }
		 br.close(); 
		 
		 super.output();
		 
		 //System.out.println(observation.size());
		 for (int i = 0; i < observation.size(); i++) {
			 Sequence fs = PreferredSettings.filter.filter(observation.get(i));
			 observation.get(i).clear();
			 observation.set(i, null);
			 observation.remove(i);
			 observation.add(i, fs);
		 }

		 for (int i = 0; i < Maskit.locids.size(); i++) {
			 System.out.print(Maskit.locids.get(i) + "\t");
			 if ((i != 0) && (i % 6 == 0)) {
	            	System.out.println();
	         }
		 }
		 
		 //Maskit.mapping.output();
	}
	
	public Sequence getLongestTrace() {
		if (observation.size() <= 1) {
			return observation.get(0);
		}
		
		int longest = 0;
		for (int i = 1; i < observation.size(); i++) {
			if (observation.get(i).getLength() > observation.get(longest).getLength()) {
				longest = i;
			}
		}
		//System.out.println("longest: "+longest);
		return observation.get(longest);
	}
	
	/*
	private int getLines(String f) throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		String line;
		int sumlines = 0;
		
		try {
			fr = new FileReader(f);
			br =new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("The txt file is not found!");
		}
		
		line = br.readLine();
		while(line != null) {
			sumlines += 1;
			line = br.readLine();
		}
		br.close();
		return sumlines;
	}
	*/
}
