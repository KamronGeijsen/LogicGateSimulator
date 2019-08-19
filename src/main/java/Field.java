package main.java;

import java.util.ArrayList;

import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;

public class Field {

	static Dot[] dot = new Dot[0];
	static Lever[] lever = new Lever[0];
	static Lamp[] lamp = new Lamp[0];
	
	static class TileIterator{
		
		Tile[][] list = {dot, lever, lamp};
		
		int index = 0;
		boolean hasNext(){
			return index < dot.length+lever.length+lamp.length;
		}
		Tile getNext(){
			return null;
		}
		
	}
	
	static class TempField {
		private boolean copyAll;
		private boolean overwriteAll;
	
		TempField(boolean copyAll, boolean overwriteAll) {
			this.copyAll = copyAll;
			this.overwriteAll = overwriteAll;
		}
	
		void copy() {
			if (Controls.amountSelected() == 0 && !copyAll)
				return;
			tempDot = new ArrayList<Dot>();
			for (Dot pointer : Field.dot)
				if ((pointer.isSelected || copyAll) && pointer.Active)
					tempDot.add(pointer.clone());
	
			int nulls;
			for (Dot pointer : tempDot) {
				nulls = 0;
				for (Dot pointer2 : pointer.ConnectedTo)
					if (!((pointer2.isSelected || copyAll) && pointer2.Active))
						nulls++;
	
				Dot[] TempConn = new Dot[pointer.ConnectedTo.length - nulls];
				int j = 0;
				for (Dot pointer2 : pointer.ConnectedTo)
					if ((pointer2.isSelected || copyAll) && pointer2.Active)
						TempConn[j++] = pointer2.pairedwith;
				pointer.ConnectedTo = TempConn;
			}
	
			tempLever = new ArrayList<Lever>();
			for (Lever pointer : Field.lever)
				if ((pointer.isSelected || copyAll) && pointer.Active)
					tempLever.add(pointer.copy());
	
			tempLamp = new ArrayList<Lamp>();
			for (Lamp pointer : Field.lamp)
				if ((pointer.isSelected || copyAll) && pointer.Active)
					tempLamp.add(pointer.copy());
	
		}
	
		void paste() {
			if (overwriteAll) {
				Field.dot = new Dot[0];
				Field.lever = new Lever[0];
				Field.lamp = new Lamp[0];
			}
	
			for (Dot pointer : tempDot)
				Dot.add(pointer);
	
			for (Dot pointer : tempDot) {
				Dot pointer2 = pointer.pairedwith;
				for (int i = 0; i < pointer2.ConnectedTo.length; i++)
					pointer2.ConnectedTo[i] = pointer2.ConnectedTo[i].pairedwith;
			}
	
			for (Lever pointer : tempLever)
				Lever.add(pointer);
			for (Lamp pointer : tempLamp)
				Lamp.add(pointer);
	
			Controls.changed = true;
		}
	
		ArrayList<Dot> tempDot = new ArrayList<Dot>();
		ArrayList<Lever> tempLever = new ArrayList<Lever>();
		ArrayList<Lamp> tempLamp = new ArrayList<Lamp>();
	
	}
	
	static class UndoHistory {
		public UndoHistory() {
			backups.add(new TempField(true, true));
		}
	
		private ArrayList<TempField> backups = new ArrayList<TempField>();
		private int undoIndex = 0;
	
		void add() {
			undoIndex++;
			while (undoIndex < backups.size()) {
				backups.remove(backups.size() - 1);
			}
			backups.add(new TempField(true, true));
			backups.get(backups.size() - 1).copy();
			
			//TODO
			if(backups.size() > 15){
				backups.set(backups.size()-10, null);
			}
		}
	
		void undo() {
			int i = undoIndex;
			undoIndex = Math.max(0, undoIndex - 1);
			if (i != undoIndex) {
				backups.get(undoIndex).paste();
				Controls.Undo = true;
			}
		}
	
		void redo() {
			int i = undoIndex;
			undoIndex = Math.min(backups.size() - 1, undoIndex + 1);
			if (i != undoIndex) {
				backups.get(undoIndex).paste();
				Controls.Undo = true;
			}
		}
	
		void print() {
			int len = 0;
			for (TempField c : undoHistory.backups) {
				for (Dot d : c.tempDot)
					len += 4 * d.ConnectedTo.length;
				len += 20;
			}
			System.out.println("Undo: " + undoHistory.undoIndex + " Len: "
					+ len + " bytes long");
		}
	}
	
	static UndoHistory undoHistory = new UndoHistory();
	static TempField Clipboard = new TempField(false, false);

}