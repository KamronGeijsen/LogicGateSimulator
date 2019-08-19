package main.java;

import main.java.Controls.KeyName;
import main.java.LogicUpdate.Logic;


public class Utilities{
	
	public static class Lever extends Tile{
		int Number;
		static void DrawCoords(){
			for(int i = 0; i < Field.lever.length; i++)
				if(Field.lever[i].Active)
					Field.lever[i].dcoords();
		}
		
		Lever copy(){
			Lever lev = new Lever();
			lev.BoundTo = BoundTo.clone();
			lev.coords(x, y);
			lev.isOn = isOn;
			lev.Active = true;
			lev.isSelected = true;
			return lev;
		}
		
		static void add(Lever l){
			if(Field.lever.length != 0){
				for(int i = 0; i < Field.lever.length; i++)
					if(!Field.lever[i].Active){
						Field.lever[i] = l;
						return;
					}
			}
			Lever[] d = new Lever[Field.lever.length + 1];
			System.arraycopy(Field.lever, 0, d, 0, Field.lever.length);
			d[Field.lever.length] = new Lever();
			Field.lever = d;
			
			Field.lever[Field.lever.length-1] = l;
			
		}
		static void add(int x, int y){
			if(Field.lever.length == 0){
				for(int i = 0; i < Field.lever.length; i++)
					if(!Field.lever[i].Active){
						Field.lever[i].coords(x, y);
						Field.lever[i].Active = true;						
						return;
					}
			}
			Lever[] d = new Lever[Field.lever.length + 1];
			System.arraycopy(Field.lever, 0, d, 0, Field.lever.length);
			d[Field.lever.length] = new Lever();
			Field.lever = d;
			Field.lever[Field.lever.length-1].Active = true;
			Field.lever[Field.lever.length-1].coords(x, y);
		}
		
		void remove() {
			Field.lever[Number] = new Lever();
		}
		
		public byte getID(){
			return 7;
		}
		
		static boolean isClose(int x, int y){
			for (Lever pointer : Field.lever)
				if(x == pointer.x && (y == pointer.y || y == pointer.y+1) && pointer.Active){
					Close = pointer;
					return true;
				}
			return false;
		}
		static Lever Close (int x, int y){
			for (Lever pointer : Field.lever)
				if(x == pointer.x && (y == pointer.y || y == pointer.y+1) && pointer.Active){
					Close = pointer;
					return pointer;
				}
			return null;
		}
		boolean LeverisClose(int x, int y){
			return x == this.x && (y == this.y || y == this.y+1) && Active;
		}
				
		void BindKey(KeyName k){
			for(int i = 0; i < BoundTo.length; i++)
				if(BoundTo[i].second == k.second && BoundTo[i].c == k.c)
					return;
			KeyName[] Temp = new KeyName[BoundTo.length + 1];
			System.arraycopy(BoundTo, 0, Temp, 0, BoundTo.length);
			Temp[BoundTo.length] = k;
			isSelected = false;
			BoundTo = Temp.clone();
		}
		
		String getMetadata(){
			String r = "0";
			if(isOn) r = "1";
			r = r + x + "-" + y;
			return r;
		}
		Dot[] touching = new Dot[7];
		boolean wasOn;
		
		void setLogic(){
			if (logic == null){
				System.err.println("no logic!" + Number);
				logic = new Logic();
			}
			logic.invert = isOn;
			int len = 0;
			for(Dot dot : touching)
				if(dot != null && dot.Active && !dot.Pasted)
					len++;
			logic.affects = new Logic[len];
			len = 0;
			for(Dot dot : touching)
				if(dot != null && dot.Active && !dot.Pasted)
					logic.affects[len++] = dot.logic;
		}
		
		KeyName[] BoundTo = {};
		boolean bind;
		
	}
	
	
	
	public static class Lamp extends Tile{
		int Number;
		static void DrawCoords(){
			for(int i = 0; i < Field.lamp.length; i++)
				if(Field.lamp[i].Active)
					Field.lamp[i].dcoords();
		}
		
		Lamp copy(){
			Lamp lam = new Lamp();
			lam.coords(x, y);
			lam.isOn = isOn;
			lam.Active = true;
			lam.isSelected = true;
			return lam;
		}
		
		static void add(Lamp l){
			if(Field.lamp.length != 0){
				for(int i = 0; i < Field.lamp.length; i++)
					if(!Field.lamp[i].Active){
						Field.lamp[i] = l;
						return;
					}
			}
			Lamp[] d = new Lamp[Field.lamp.length + 1];
			System.arraycopy(Field.lamp, 0, d, 0, Field.lamp.length);
			d[Field.lamp.length] = new Lamp();
			Field.lamp = d;
			
			Field.lamp[Field.lamp.length-1] = l;
			
		}
		static void add(int x, int y){
			if(Field.lamp.length == 0){
				for(int i = 0; i < Field.lamp.length; i++)
					if(!Field.lamp[i].Active){
						Field.lamp[i].coords(x, y);
						Field.lamp[i].Active = true;						
						return;
					}
			}
			Lamp[] d = new Lamp[Field.lamp.length + 1];
			System.arraycopy(Field.lamp, 0, d, 0, Field.lamp.length);
			d[Field.lamp.length] = new Lamp();
			Field.lamp = d;
			Field.lamp[Field.lamp.length-1].Active = true;
			Field.lamp[Field.lamp.length-1].coords(x, y);
		}
		
		
		void remove(){
			Field.lamp[Number] = new Lamp();
		}
		public byte getID(){
			return 8;
		}
		
		static boolean isClose(int x, int y){
			for (Lamp pointer : Field.lamp)
				if(x == pointer.x && y == pointer.y && pointer.Active){
					Close = pointer;
					return true;
				}
			return false;
		}
		static Lamp Close (int x, int y){
			for (Lamp pointer : Field.lamp)
				if(x == pointer.x && y == pointer.y && pointer.Active){
					Close = pointer;
					return pointer;
				}
			return null;
		}
		
		boolean LampisClose(int x, int y){
			return x == this.x && y == this.y && Active;
		}
		@Override
		void dcoords(){
			drawx = x*Main.GridSize+Main.x;
			drawy = y*Main.GridSize+Main.y;
			drawnx = nx*Main.GridSize+Main.x;
			drawny = ny*Main.GridSize+Main.y;
			
			
			texture = 0;
			if(isClose(x,y-1))
				texture += 8 + (Close.isOn?128:0);
			if(isClose(x-1,y))
				texture += 4 + (Close.isOn?64:0);
			if(isClose(x,y+1))
				texture += 2 + (Close.isOn?32:0);
			if(isClose(x+1,y))
				texture += 1 + (Close.isOn?16:0);
			
		}
		
		String getMetadata(){
			String r = "0";
			if(isOn) r = "1";
			r = r + x + "-" + y;
			return r;
		}
		Dot[] touching = new Dot[4];
		
		byte texture;
	}
	
	
	
	static class MicroChip extends Tile{
		
	}
	
	
}
