package main.java;

import java.util.ArrayList;
import java.util.Arrays;

import main.java.LogicUpdate.Logic;
import main.java.Save.ByteBuffer;

public class Dot extends Tile {

	static Dot Close;

	static Dot[] drawdot = new Dot[0];
	static Dot[] drawsml = new Dot[0];
	static int drawdotlen;
	static int drawsmllen;

	static void DrawCoords() {
		if (drawdot.length < Field.dot.length) {
			drawdot = new Dot[Field.dot.length];
			drawsml = new Dot[Field.dot.length];
		}

		drawdotlen = 0;
		drawsmllen = 0;
		drawdotlen = 0;
		drawsmllen = 0;
		for (Dot pointer : Field.dot)
			if (pointer.Active) {
				pointer.UpdafteImg();
				if (pointer.img != -1) {
					if (!pointer.isSmall)
						drawdot[drawdotlen++] = pointer;
					else
						drawsml[drawsmllen++] = pointer;

				}
			}

	}

	static void add(Dot d) {
		for (int i = 0; i < Field.dot.length; i++)
			if (!Field.dot[i].Active) {
				Field.dot[i] = d.clone();
				Field.dot[i].number = i;
				Field.dot[i].Pasted = true;
				d.pairedwith = Field.dot[i];
				Field.dot[i].logic = new Logic();
				return;
			}
		Dot[] d2 = new Dot[Field.dot.length + 1];
		System.arraycopy(Field.dot, 0, d2, 0, Field.dot.length);
		d2[Field.dot.length] = new Dot();
		Field.dot = d2;
		int l = Field.dot.length - 1;
		Field.dot[l] = d.clone();
		Field.dot[l].number = l;
		Field.dot[l].Pasted = true;
		Field.dot[l].logic = new Logic();

		d.pairedwith = Field.dot[l];
	}

	static void add(int x, int y) {
		add(x, y, false, false, (byte) 0);
	}

	static void add(int x, int y, boolean Inverted, boolean Small, byte Side) {
		Controls.changed = true;
		int i = 0;
		if (Field.dot.length == 0 || !isClose(x, y)) {
			for (Dot pointer : Field.dot) {
				if (!pointer.Active) {
					pointer.coords(x, y);
					pointer.Active = true;
					pointer.isInvert = Inverted;
					pointer.isSmall = Small;
					pointer.Side = Side;
					pointer.nSide = Side;
					pointer.number = i;
					pointer.logic = new Logic();
					return;
				}
				i++;
			}
			Dot[] d = new Dot[Field.dot.length + 1];
			System.arraycopy(Field.dot, 0, d, 0, Field.dot.length);
			d[Field.dot.length] = new Dot();
			Field.dot = d;
			Dot pointer = Field.dot[Field.dot.length - 1];
			pointer.isInvert = Inverted;
			pointer.isSmall = Small;
			pointer.Active = true;
			pointer.coords(x, y);
			pointer.Side = Side;
			pointer.nSide = Side;
			pointer.number = i;
			pointer.logic = new Logic();
		}
	}

	static boolean isClose(int x, int y) {
		for (Dot pointer : Field.dot)
			if (x == pointer.x && y == pointer.y && pointer.Active) {
				Close = pointer;
				return true;
			}
		Close = null;
		return false;
	}

	static Dot Close(int x, int y) {
		for (Dot pointer : Field.dot)
			if (x == pointer.x && y == pointer.y && pointer.Active)
				return pointer;
		return null;
	}

	int number = -1;
	Dot pairedwith;

	boolean isInvert;
	boolean isSmall;

	byte nSide = 0;
	byte Side = 0;

	Dot[] ConnectedTo = new Dot[0];

	Dot() {

	}

	Dot(byte ID) {
		if (ID == 1) {
			return;
		}
		if (ID == 2) {
			isInvert = true;
			return;
		}
		if (ID >= 3 && ID <= 6) {
			isSmall = true;
			nSide = Side = (byte) (ID - 3);
			return;
		}
		new Exception().printStackTrace();

	}

	void UpdafteImg() {
		dcoords();
		if ((drawx > -Display.GridSize && drawy > -Display.GridSize && drawx < Main.WIDTH && drawy < Main.HEIGHT)
				|| (Controls.MoveTile && (drawnx > -Display.GridSize && drawny > -Display.GridSize
						&& drawnx < Main.WIDTH && drawny < Main.HEIGHT))) {
			if (!isSmall) {
				int bg = 16 * ((Math.floorDiv(x, 8) + Math.floorDiv(y, 8)) & 1 ^ 1);
				int index;
				if (isSelected || tempSel)
					index = Controls.MoveTile ? 8 : 12;
				else
					index = 4;
				int on = isOn ? 2 : 0;
				int inv = isInvert ? 1 : 0;
				img = bg | index | inv | on;
			} else {
				int bg = 32 * ((Math.floorDiv(x, 8) + Math.floorDiv(y, 8)) & 1 ^ 1);
				int index;
				if (isSelected || tempSel)
					index = Controls.MoveTile ? 4 : 6;
				else
					index = 2;
				int s = Side * 8;
				int on = isOn ? 1 : 0;
				img = bg | index | s | on;
			}
		} else {
			img = -1;
		}

	}

	void UpdateOnOff() {

	}

	public Dot clone() {
		Dot dot = new Dot();
		dot.isInvert = isInvert;
		dot.isSmall = isSmall;
		dot.x = x;
		dot.y = y;
		dot.Side = Side;
		dot.nSide = Side;
		dot.ConnectedTo = ConnectedTo.clone();
		dot.isOn = isOn;
		pairedwith = dot;
		dot.Active = true;
		dot.isSelected = true;
		if (logic == null)
			logic = new Logic();

		return dot;
	}

	void remove() {
		Dot pointer1 = Field.dot[number];
		for (Dot pointer2 : pointer1.ConnectedTo) {
			int i = 0;
			for (Dot temp : pointer2.ConnectedTo)
				if (temp.number == number) {
					pointer2.ConnectedTo[i] = pointer2.ConnectedTo[pointer2.ConnectedTo.length - 1];
					pointer2.ConnectedTo = Arrays.copyOf(pointer2.ConnectedTo, pointer2.ConnectedTo.length - 1);
				} else
					i++;

		}
		Field.dot[pointer1.number] = new Dot();
	}

	void connectTo(Dot dot) {
		Dot pointer1 = this;
		Dot pointer2 = dot;

		if (pointer1 == pointer2 || pointer1.number == pointer2.number)
			return;

		for (Dot temp : pointer1.ConnectedTo)
			if (temp.equals(pointer2))
				return;

		Dot[] temp = new Dot[pointer1.ConnectedTo.length + 1];
		System.arraycopy(pointer1.ConnectedTo, 0, temp, 0, pointer1.ConnectedTo.length);
		temp[temp.length - 1] = pointer2;
		pointer1.ConnectedTo = temp;
		Controls.changed = true;
		
		temp = new Dot[pointer2.ConnectedTo.length + 1];
		System.arraycopy(pointer2.ConnectedTo, 0, temp, 0, pointer2.ConnectedTo.length);
		temp[temp.length - 1] = pointer1;
		pointer2.ConnectedTo = temp;
		Controls.changed = true;
	}

	boolean DotisClose(int x, int y) {
		return x == this.x && y == this.y && Active;
	}

	void addMetadata2(ByteBuffer write) {
		if (number < 0)
			return;

		int toWrite = number;
		for (Dot pointer : ConnectedTo) {
			if (pointer.number < number) {
				write.addInt(toWrite);
				toWrite = pointer.number;
			}
		}
		toWrite |= Integer.MIN_VALUE;
		write.addInt(toWrite);
	}

	void setLogic() {
		if (logic == null) {
			logic = new Logic();
		}
		if (isSmall) {
			Dot next = Close(Main.xSide(Side) + x, Main.ySide(Side) + y);
			if (next == null)
				logic.affects = new Logic[0];
			else
				logic.affects = new Logic[] { next.logic };

		} else {
			int len = 0;
			for (Dot dot : ConnectedTo) {
				if (!dot.Active || dot.Pasted)
					continue;
				if (dot.isInvert)
					continue;
				len++;
			}
			logic.affects = new Logic[len];
			len = 0;
			for (Dot dot : ConnectedTo) {
				if (!dot.Active || dot.Pasted)
					continue;
				if (dot.isInvert)
					continue;
				logic.affects[len++] = dot.logic;
			}
		}
		logic.output = !isSmall;
		logic.invert = isInvert;
		logic.isOn = isOn;
	}

	void mergeLogic() {
		if (logic.affects.length == 0 || logic.affects[0] == null)
			return;
		if (logic.affects[0].affects == logic.affects)
			return;
		ArrayList<Logic> total = new ArrayList<Logic>(Arrays.asList(logic.affects));

		for (int i = 0; i < total.size(); i++) {
			Logic dot = total.get(i);
			if (dot == null) {
				System.err.println("NULL");
				total.remove(i);
				i--;
				continue;
			}

			if (dot.output)
				for (Logic l : dot.affects)
					if (!total.contains(l))
						total.add(l);
		}

		logic.affects = total.toArray(new Logic[total.size()]);
		for (Logic l : logic.affects) {
			if (l.output)
				l.affects = logic.affects;
		}
	}

	void extendLogic() {
		if (logic.affects.length == 0 || logic.affects[0] == null)
			return;

		ArrayList<Logic> total = new ArrayList<Logic>(Arrays.asList(logic.affects));

		for (int i = 0; i < total.size(); i++) {
			Logic dot = total.get(i);
			if (dot == null) {
				System.err.println("NULL");
				total.remove(i);
				i--;
				continue;
			}

			if (dot.output)
				for (Logic l : dot.affects)
					if (!total.contains(l))
						total.add(l);
		}

		logic.affects = total.toArray(new Logic[total.size()]);
	}

	public String toString() {
		return number + "|" + ConnectedTo.length;
	}
}
