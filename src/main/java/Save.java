package main.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import main.java.GUI.SmallNotification;
import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;

public class Save {
	final static String dirPath = "C:\\";
	
	static class Version0_0_0{
		void SaveField(String s, boolean overwrite){
			
			FieldInString();
			try {
				File statText = new File(dirPath + s + ".txt");
				FileOutputStream is = new FileOutputStream(statText);
				OutputStreamWriter osw = new OutputStreamWriter(is);
				Writer w = new BufferedWriter(osw);
				w.write(FieldtoString());

				w.close();
			} catch (IOException e) {
				System.err.println("Eh?");
			}
		}
		
		boolean LoadField(File f) {
			try {
				@SuppressWarnings("resource")
				String s = new Scanner(f).useDelimiter("\\A").next();
				StringToObjects(s);
			} catch (Exception e) {
				System.err.println("Eror!");
				return false;
			}
			ObjectsToField();
			
			return true;
		}
		
		void FieldInString(){
			SetObjectsLengths();
			int l = 0;
			for(int i = 0; i < Field.dot.length; i++)
				if(Field.dot[i].Active){
					//TODO Dots[l] = i + "-" + Field.dot[i].getMetadata();
					l++;
				}
			l = 0;
			for(int i = 0; i < Field.lever.length; i++)
				if(Field.lever[i].Active){
					Levers[l] = i + "-" + Field.lever[i].getMetadata();
					l++;
				}
			l = 0;
			for(int i = 0; i < Field.lamp.length; i++)
				if(Field.lamp[i].Active){
					Lamps[l] = i + "-" + Field.lamp[i].getMetadata();
					l++;
				}
		}
		
		void SetObjectsLengths(){
			int l = 0;
			for(int i = 0; i < Field.dot.length; i++)
				if(Field.dot[i].Active) l++;
			Dots = new String[l];
			l = 0;
			for(int i = 0; i < Field.lever.length; i++)
				if(Field.lever[i].Active) l++;
			Levers = new String[l];
			l = 0;
			for(int i = 0; i < Field.lamp.length; i++)
				if(Field.lamp[i].Active) l++;
			Lamps = new String[l];
		}
		
		String FieldtoString(){
			String r = "";
			for(int i = 0; i < Dots.length; i++)
				r = r + "(" + Dots[i] + ")";
			r = r + "|";
			for(int i = 0; i < Levers.length; i++)
				r = r + "(" + Levers[i] + ")";
			r = r + "|";
			for(int i = 0; i < Lamps.length; i++)
				r = r + "(" + Lamps[i] + ")";
			return r;
		}
		
		void StringToObjects(String s){
			//if(s.replaceAll("|", "").length() != s.length()){
			boolean f = false;
			int i = 0;
			String c = s;
			while(c.length() > 0 && !c.startsWith("|")){
				if(c.startsWith("("))
					i++;
				c = c.substring(1);
			}
			Dots = new String[i];
			for(int j = 0; j < Dots.length; j++)
				Dots[j] = "";
			i = 0;
			while (s.length() > 0 && !s.startsWith("|")){
				if(s.startsWith("(")){
					s = s.substring(1);
					f = true;
				}
				else if(s.startsWith(")"))f = false;
				if(f)Dots[i] = Dots[i] + s.substring(0,1);
				if(s.startsWith(")"))i++;
				s = s.substring(1);
			}
			s = s.substring(1);
			i = 0;
			c = s;
			while(c.length() > 0 && !c.startsWith("|")){
				if(c.startsWith("("))
					i++;
				c = c.substring(1);
			}
			Levers = new String[i];
			for(int j = 0; j < Levers.length; j++)
				Levers[j] = "";
			i = 0;
			while (s.length() > 0 && !s.startsWith("|")){
				if(s.startsWith("(")){
					s = s.substring(1);
					f = true;
				}
				else if(s.startsWith(")"))f = false;
				if(f)Levers[i] = Levers[i] + s.substring(0,1);
				if(s.startsWith(")"))i++;
				s = s.substring(1);
			}
			s = s.substring(1);
			i = 0;
			c = s;
			while(c.length() > 0 && !c.startsWith("|")){
				if(c.startsWith("("))
					i++;
				c = c.substring(1);
			}
			Lamps = new String[i];
			for(int j = 0; j < Lamps.length; j++)
				Lamps[j] = "";
			i = 0;
			while (s.length() > 0 && !s.startsWith("|")){
				if(s.startsWith("(")){
					s = s.substring(1);
					f = true;
				}
				else if(s.startsWith(")"))f = false;
				if(f)Lamps[i] = Lamps[i] + s.substring(0,1);
				if(s.startsWith(")"))i++;
				s = s.substring(1);
			}
		}
		
		void ObjectsToField(){
			String CDots[] = Dots.clone();
			String CLevers[] = Levers.clone();
			String CLamps[] = Lamps.clone();
			
			
			
			if(Dots != null && Dots.length > 0)
			dot = new Dot[getNumber(Dots[Dots.length-1])+1];
			if(Levers != null && Levers.length > 0)
			lever = new Lever[getNumber(Levers[Levers.length-1])+1];
			if(Lamps != null && Lamps.length > 0)
			lamp = new Lamp[getNumber(Lamps[Lamps.length-1])+1];
			
			DotConnectedTo = new int[dot.length][];
			
			for(int i = 0; i < dot.length; i++)
				dot[i] = new Dot();
			for(int i = 0; i < lever.length; i++)
				lever[i] = new Lever();
			for(int i = 0; i < lamp.length; i++)
				lamp[i] = new Lamp();
			
			for(int i = 0; i < Dots.length; i++){
				int n = getNumber(Dots[i]);
				dot[n].Active = true;
				dot[n].number = n;
				do Dots[i] = Dots[i].substring(1);
				while(!Dots[i].startsWith("-"));
				Dots[i] = Dots[i].substring(1);
				dot[n].isOn = Dots[i].substring(0, 1).equals("1");
				Dots[i] = Dots[i].substring(1);	
				dot[n].isInvert = Dots[i].substring(0, 1).equals("1");
				Dots[i] = Dots[i].substring(1);	
				dot[n].isSmall = Dots[i].substring(0, 1).equals("1");
				if(Dots[i].substring(0, 1).equals("1")) {
					Dots[i] = Dots[i].substring(1);
					dot[n].Side = Byte.parseByte(Dots[i].substring(0, 1));
					dot[n].nSide = dot[n].Side;
				}
				Dots[i] = Dots[i].substring(1);		
				int l = 1;
				if(Dots[i].startsWith("-")){
					l = -1;			
					Dots[i] = Dots[i].substring(1);	
				}	
					
				dot[n].x = l*getNumber(Dots[i]);
				dot[n].nx = dot[n].x;
				do Dots[i] = Dots[i].substring(1);
				while(!Dots[i].startsWith("-"));
				Dots[i] = Dots[i].substring(1);	
				l = 1;
				if(Dots[i].startsWith("-")){
					l = -1;			
					Dots[i] = Dots[i].substring(1);	
				}
				dot[n].y = l*getNumber(Dots[i]);
				dot[n].ny = dot[n].y;
				Dots[i] = Dots[i].substring(1);	
				if(Dots[i].length() > 0 && Dots[i].contains("-")){				
					while(!Dots[i].startsWith("-") && Dots[i].length() > 0)
						Dots[i] = Dots[i].substring(1);
					Dots[i] = Dots[i].substring(1);	
				
					l = 0;
					int j = 0;
					dot[n].ConnectedTo = new Dot[Dots[i].length()-Dots[i].replaceAll("-", "").length()+1];
					while(Dots[i].length() > 0){
						
						if(Dots[i].startsWith("-")){
							dot[n].ConnectedTo[j] = dot[l];
							j++;
							l = 0;
						}
						else
							l = Integer.parseInt(l + Dots[i].substring(0,1));
						Dots[i] = Dots[i].substring(1);
						dot[n].ConnectedTo[dot[n].ConnectedTo.length-1] = dot[l];
					}
				}
				
			}
			for(int i = 0; i < Levers.length; i++){
				int n = getNumber(Levers[i]);
				lever[n].Active = true;
				do Levers[i] = Levers[i].substring(1);
				while(!Levers[i].startsWith("-"));
				Levers[i] = Levers[i].substring(1);
				lever[n].isOn = Levers[i].substring(0, 1).equals("1");
				Levers[i] = Levers[i].substring(1);	
				int l = 1;
				if(Levers[i].startsWith("-")){
					l = -1;			
					Levers[i] = Levers[i].substring(1);	
				}	
				lever[n].x = l*getNumber(Levers[i]);
				lever[n].nx = lever[n].x;
				do Levers[i] = Levers[i].substring(1);
				while(!Levers[i].startsWith("-"));
				Levers[i] = Levers[i].substring(1);	
				l = 1;
				if(Levers[i].startsWith("-")){
					l = -1;			
					Levers[i] = Levers[i].substring(1);	
				}
				lever[n].y = l*getNumber(Levers[i]);
				lever[n].ny = lever[n].y;
				Levers[i] = Levers[i].substring(1);
			}
			
			for(int i = 0; i < Lamps.length; i++){
				int n = getNumber(Lamps[i]);
				lamp[n].Active = true;
				do Lamps[i] = Lamps[i].substring(1);
				while(!Lamps[i].startsWith("-"));
				Lamps[i] = Lamps[i].substring(1);
				lamp[n].isOn = Lamps[i].substring(0, 1).equals("1");
				Lamps[i] = Lamps[i].substring(1);	
				int l = 1;
				if(Lamps[i].startsWith("-")){
					l = -1;			
					Lamps[i] = Lamps[i].substring(1);	
				}	
				lamp[n].x = l*getNumber(Lamps[i]);
				lamp[n].nx = lamp[n].x;
				do Lamps[i] = Lamps[i].substring(1);
				while(!Lamps[i].startsWith("-"));
				Lamps[i] = Lamps[i].substring(1);	
				l = 1;
				if(Lamps[i].startsWith("-")){
					l = -1;			
					Lamps[i] = Lamps[i].substring(1);	
				}
				lamp[n].y = l*getNumber(Lamps[i]);
				lamp[n].ny = lamp[n].y;
				Lamps[i] = Lamps[i].substring(1);
			}
			
			Dots = CDots.clone();
			Levers = CLevers.clone();
			Lamps = CLamps.clone();	
		}
		
			int getNumber(String s){
			int i = 0;
			if(s != null && s != "")
				do{ i = Integer.parseInt(i + s.substring(0,1));
					s = s.substring(1);
				}while(!s.startsWith("-") && s.length() > 0);
			return i;
		}
		String Dots[];
		String Levers[];
		String Lamps[];
		
		Dot dot[] = new Dot[0];
		Lever lever[] = new Lever[0];
		Lamp lamp[] = new Lamp[0];
		
		int[][] DotConnectedTo;
	}
	
	static class Version1_0_0{
		
		static String saveVersion = "1.0.0";
		
		ArrayList<Dot> tempDot = new ArrayList<Dot>();
		ArrayList<ArrayList<Integer>> tempDotConnectedTo = new ArrayList<ArrayList<Integer>>(); 
		ArrayList<Lever> tempLever = new ArrayList<Lever>();
		ArrayList<Lamp> tempLamp = new ArrayList<Lamp>();
		
		long getSaveVersion(){
			String[] parse = saveVersion.split("\\.");
			return (Long.parseLong(parse[0])&4294967295l) + (Long.parseLong(parse[1])&65535) + (Long.parseLong(parse[2])&65535) ;
		}
		
		void save(File f){
			ByteBuffer write = new ByteBuffer();
			write.addLong(getSaveVersion());
			
			for(Dot pointer : Field.dot){
				if(!pointer.Active)
					continue;
				write.addInt(pointer.getIDCoords());
				pointer.addMetadata2(write);
			}
			for(Lever pointer : Field.lever){
				if(!pointer.Active)
					continue;
				write.addInt(pointer.getIDCoords());
			}
			for(Lamp pointer : Field.lamp){
				if(!pointer.Active)
					continue;
				write.addInt(pointer.getIDCoords());
			}
			
			try {
				
				
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(write.getBytes());
				fos.close();
				
			} catch (IOException e) {
				 System.err.println("Eh?");
				 return;
			}
		}
		boolean load(File f){
			byte[] data = null;
			try {
				data = Files.readAllBytes(f.toPath());
				
			} catch (IOException e) {
				System.out.println(e.toString());
				return false;
			}
			
			ByteBufferReader read = new ByteBufferReader(data);
			long version = read.getLong();
			if(version != getSaveVersion())
				System.err.println("ERR: Save versions are not exactly alike");
			
			tempDot = new ArrayList<Dot>();
			tempDotConnectedTo = new ArrayList<ArrayList<Integer>>(); 
			tempLever = new ArrayList<Lever>();
			tempLamp = new ArrayList<Lamp>();
			
			while (read.hasNextInt()) {
				int IDcoord = read.getInt();
				
				boolean isOn = (IDcoord&1) == 1;
				byte ID = (byte) (((byte)IDcoord)>>1);
				int x = (IDcoord >> 8 & 0xfff)-(1<<11);
				int y = (IDcoord >> 20 & 0xfff)-(1<<11);
				
				if(ID >= 1 && ID <= 6){
					Dot pointer = new Dot(ID);
					pointer.coords(x, y);
					ArrayList<Integer> list = new ArrayList<Integer>();
					int i = read.getInt();
					while((i&(-2147483648)) == 0){
						list.add(i);
						i = read.getInt();
					}
					list.add(i&2147483647);
					pointer.number = list.get(0);
					pointer.isOn = isOn;
					pointer.Active = true;
					
					tempDot.add(pointer);
					tempDotConnectedTo.add(list);
				}
				if(ID == 7){
					Lever pointer = new Lever();
					pointer.coords(x, y);
					pointer.isOn = isOn;
					pointer.Active = true;
					tempLever.add(pointer);
				}
				if(ID == 8){
					Lamp pointer = new Lamp();
					pointer.coords(x, y);
					pointer.isOn = isOn;
					pointer.Active = true;
					tempLamp.add(pointer);
				}
			}
			
			
			int maxDotNum = 0;
			for(Dot d : tempDot)
				maxDotNum = Math.max(maxDotNum, d.number);
			maxDotNum++;
			System.out.println(maxDotNum);
			tempDot.ensureCapacity(maxDotNum);
			for(int i = tempDot.size(); i < maxDotNum; i++)
				tempDot.add(new Dot());
			for(int i = 0; i < maxDotNum; i++){
				Dot wrongPosition = tempDot.get(i);
				while(wrongPosition.number != -1 && wrongPosition.number != i){
					tempDot.set(i, tempDot.get(wrongPosition.number));
					tempDot.set(wrongPosition.number, wrongPosition);
					wrongPosition = tempDot.get(i);
				}
			}
			for(ArrayList<Integer> list : tempDotConnectedTo){
				Dot d = tempDot.get(list.get(0));
				for(int i : list)
					d.connectTo(tempDot.get(i));
			}
			
			return true;
		}
		
		
		
	}
	
	
	
	
	public static void saveField(String fileName, boolean overwrite){
		if (fileName.length() == 0)
			return;
		File f = new File(dirPath + fileName + ".txt");
		if (overwrite && f.exists() && !f.isDirectory()) {
			GUI.Warning.init("Overwrite", "Do you want to over   write \""+ fileName + "\"?", GUI.Warning.YesCancel);
			return;
		}
		
		Version1_0_0 saveObject = new Version1_0_0();
		
		saveObject.save(f);
	}
	public static void loadField(String fileName){
		if (fileName.length() == 0)
			return;
		File f = new File(dirPath + fileName + ".txt");
		System.out.println(f);
		if(!f.exists() || f.isDirectory()){
			SmallNotification.init("File \"" + fileName + "\" doesn\'t exist" );
			return;
		}
		
		long starttime = System.nanoTime();
		System.out.println("fff");
		try {
			Version0_0_0 v0_0_0 = new Version0_0_0();
			if(v0_0_0.LoadField(f)){
				Field.dot = v0_0_0.dot;
				Field.lever = v0_0_0.lever;
				Field.lamp = v0_0_0.lamp;
				
				Dot someObject = Field.dot[5];
				someObject.drawnx = 5;
				for (java.lang.reflect.Field field : someObject.getClass().getDeclaredFields()) {
				    field.setAccessible(true);
				    Object value;
					try {
						value = field.get(someObject);
						if (value != null) {
					        System.out.println(field.getType() + "	" +  field.getName() + "=" + value);
					    }
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					} 
				    
				}
				for (java.lang.reflect.Field field : Tile.class.getDeclaredFields()) {
				    field.setAccessible(true);
				    Object value;
					try {
						value = field.get(someObject);
						if (value != null) {
					        System.out.println(field.getType() + "	" +  field.getName() + "=" + value);
					    }
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					} 
				    
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("fff");
		Version1_0_0 v1_0_0 = new Version1_0_0();
		if(v1_0_0.load(f)){
			Field.dot = v1_0_0.tempDot.toArray(new Dot[v1_0_0.tempDot.size()]);
			Field.lever = v1_0_0.tempLever.toArray(new Lever[v1_0_0.tempLever.size()]);
			Field.lamp = v1_0_0.tempLamp.toArray(new Lamp[v1_0_0.tempLamp.size()]);
		}
		
		System.out.println("took " +  (System.nanoTime()-starttime)/1000/1000.0d + "seconds");
		
		
		
		
		
		Controls.changed = true;
		Controls.changed();
	}
	public static void loadFieldToClipboard(String fileName){
		if (fileName.length() == 0)
			return;
		File f = new File(dirPath + fileName + ".txt");
		if(!f.exists() || f.isDirectory()){
			SmallNotification.init("File \"" + fileName + "\" doesn\'t exist" );
			return;
		}
		
		long starttime = System.nanoTime();
		
		Version0_0_0 v0_0_0 = new Version0_0_0();
		if(v0_0_0.LoadField(f)){
			Field.Clipboard.tempDot = new ArrayList<Dot>(Arrays.asList(v0_0_0.dot));
			Field.Clipboard.tempLever = new ArrayList<Lever>(Arrays.asList(v0_0_0.lever));
			Field.Clipboard.tempLamp = new ArrayList<Lamp>(Arrays.asList(v0_0_0.lamp));
			return;
		}
		Version1_0_0 v1_0_0 = new Version1_0_0();
		if(v1_0_0.load(f)){
			Field.Clipboard.tempDot = v1_0_0.tempDot;
			Field.Clipboard.tempLever = v1_0_0.tempLever;
			Field.Clipboard.tempLamp = v1_0_0.tempLamp;
		}
		
		System.out.println("took " +  (System.nanoTime()-starttime)/1000/1000.0d + "seconds");
	}
	
	
	static class ByteBuffer {
		int index;
		byte[] data = new byte[256];
		void addByte(byte b){
			if(index+1 == data.length)
				expand();
			data[index++] = b;
		}
		void addLong(long l){
			if(index+8 >= data.length)
				expand();
			for(int i = 7; i >= 0; i--){
				data[index+i] = (byte)l;
				l >>= 8;
			}
			index+=8;
		}
		void addInt(int l){
			if(index+4 >= data.length)
				expand();
			for(int i = 3; i >= 0; i--){
				data[index+i] = (byte)l;
				l >>= 8;
			}
			index+=4;
		}
		
		void expand(){
			byte[] doubled = new byte[data.length<<1];
			System.arraycopy(data, 0, doubled, 0, data.length);
			data = doubled;
		}
		byte[] getBytes(){
			byte[] ret = new byte[index];
			System.arraycopy(data, 0, ret, 0, index);
			return ret;
		}
		void print(){
			byte[] ret = new byte[index];
			System.arraycopy(data, 0, ret, 0, index);
			for(byte b : ret)
				System.out.println((Integer.toBinaryString((b&255)+256)+"").replaceFirst("1", ""));
		}
	}
	static class ByteBufferReader{
		public ByteBufferReader(byte[] data) {
			this.data = data;
		}
		byte[] data;
		int index = 0;
		
		long getLong(){
			long ret = 0;
			for(int i = 0; i < 8; i++){
				ret <<= 8;
				ret |= ((long)data[index++] & 0xFF);
			}
			return ret;
		}
		int getInt(){
			int ret = 0;
			for(int i = 0; i < 4; i++){
				ret <<= 8;
				ret |= (data[index++] & 0xFF);
			}
			return ret;
		}
		byte getByte(){
			return data[index++];
		}
		boolean hasNextInt(){
			return index+4 < data.length;
		}
	}
	static class InvalidVersionException extends Exception{
		private static final long serialVersionUID = 5904789821507573052L;
		
		public InvalidVersionException(final String message) {
			super(message);
		}
	}
	
	
}
