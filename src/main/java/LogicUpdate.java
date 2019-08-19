package main.java;

import java.util.ArrayList;

import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;




public class LogicUpdate{
	
	static ArrayList<Logic> inputs = new ArrayList<Logic>();
	static ArrayList<Logic> outputs = new ArrayList<Logic>();
	
	static class Logic{
		Logic[] affects = new Logic[0];
		int on;
		boolean isOn;
		boolean invert;
		
		boolean output;
		boolean update;
		
	}
	

	
	static void createLogic(){
		inputs = new ArrayList<Logic>();
		outputs = new ArrayList<Logic>();
		
		for(Dot dot : Field.dot)
			dot.setLogic();
		for(Dot dot : Field.dot){
			if(!dot.Active || dot.Pasted)
				continue;
			
			
			if(dot.isSmall){
				inputs.add(dot.logic);
//				dot.logic.num = inputs.size()-1;
				
			}
			else{
				outputs.add(dot.logic);
//				dot.logic.num = outputs.size()-1;
				if(dot.isInvert)
					dot.extendLogic();
				else
					dot.mergeLogic();
			}
			
			
		}
		
		
		
		
		for(Lever lever : Field.lever)
			lever.setLogic();
		for(Lever lever : Field.lever){
			if(!lever.Active || lever.Pasted)
				continue;
			
			inputs.add(lever.logic);
//			lever.logic.num = inputs.size()-1;
		}
	}
	
	static void safeUpdate(){
		for(Logic l : inputs)
			l.on = 0;
		for(Logic l : outputs)
			l.on = 0;
		
		
		for(Logic l : inputs)
			if(l.isOn)
				for(Logic toUpdate : l.affects)
					toUpdate.on++;
		for(Logic l : outputs)
			l.isOn = l.on>0 != l.invert;
		
		
		
		for(Logic l : outputs)
			if(l.isOn)
				for(Logic toUpdate : l.affects)
					toUpdate.on++;
		for(Logic l : outputs)
			l.isOn = l.on>0 != l.invert;
		for(Logic l : inputs)
			l.isOn = l.on>0 != l.invert;
		
	}
	
	static void setupQuick(){
		for(Logic l : inputs)
			l.on = 0;
		for(Logic l : outputs)
			l.on = 0;
		
		
		for(Logic l : inputs)
			if(l.isOn)
				for(Logic toUpdate : l.affects)
					toUpdate.on++;
		for(Logic l : outputs)
			l.isOn = l.on>0 != l.invert;
		
		
		
		for(Logic l : outputs)
			if(l.isOn)
				for(Logic toUpdate : l.affects)
					toUpdate.on++;
		
		
		for(Logic l : outputs){
			boolean isOn = l.on>0 != l.invert;
			l.update = l.isOn != isOn;
			l.isOn = isOn;
		}
		for(Logic l : inputs){
			boolean isOn = l.on>0 != l.invert;
			l.update = l.isOn != isOn;
			l.isOn = isOn;
		}
	}
	
	static void quickUpdate(){
		
		for(Logic l : inputs)
			if(l.update){
				if(l.isOn)
					for(Logic toUpdate : l.affects)
						toUpdate.on++;
				else
					for(Logic toUpdate : l.affects)
						toUpdate.on--;
				l.update = false;
			}
		for(Logic l : outputs){
			boolean isOn = l.on>0 != l.invert;
			l.update = l.isOn != isOn;
			l.isOn = isOn;
		}
		
		
		
		for(Logic l : outputs)
			if(l.update){
				if(l.isOn)
					for(Logic toUpdate : l.affects)
						toUpdate.on++;
				else
					for(Logic toUpdate : l.affects)
						toUpdate.on--;
				l.update = false;
			}
		
		
		for(Logic l : inputs){
			boolean isOn = l.on>0 != l.invert;
			l.update = l.isOn != isOn;
			l.isOn = isOn;
		}
		
		
		
	}
	
	static void stopQuick(){
		for(Logic l : outputs){
			boolean isOn = l.on>0 != l.invert;
			l.update = l.isOn != isOn;
			l.isOn = isOn;
		}
		updateGraphics();
	}
	
	static void updateGraphics(){
		for(Dot dot : Field.dot){
			if(dot.isOn != dot.logic.isOn){
				dot.isOn = dot.logic.isOn;
				dot.UpdafteImg();
			}
		}
		for(Lamp pointer : Field.lamp){
			pointer.isOn = false;
			for(Dot pointer2 : pointer.touching)
				if(pointer2 != null && pointer.Active && pointer2.isOn)
					pointer.isOn = true;
		}
	}
	
}
