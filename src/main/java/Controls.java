package main.java;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;

import main.java.GUI.Hotbar;
import main.java.GUI.Menu;
import main.java.GUI.SmallNotification;
import main.java.GUI.TextInput;
import main.java.GUI.Updatespeed;
import main.java.GUI.Warning;
import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;

public class Controls {
	
	static int lx, ly;		//Absolute last
	static int x, y;		//Relative field
	static int sx, sy;		//Relative field start
	static int ax, ay;		//Absolute
	static int asx, asy;	//Absolute start
	static int cx, cy;		//Absolute fixed to grid
	static int fx, fy;		//Relative fixed to grid
	static int fsx, fsy;	//Relative fixed to grid start
	static boolean MouseDown = false;
	static boolean dragged = false;
	static boolean Connecting = false;
	static boolean shift = false;
	static boolean ctrl = false;
	static boolean tab = false;
	static boolean MoveTile = false;
	static boolean MoveNotPossible = false;
	static boolean MoveOnScreen = false;
	static boolean GroupSelect = false;
	static boolean changed = false;	
	static boolean UpdateField = true;
	static boolean UpdateChanged = true;	
	static boolean Undo = false;
	static boolean stick = false;

	static int m;
	static int Zoom = 7;
	static int Zx, Zy;
	
	
	static Point tdot[] = new Point[0];
	static byte tdotSide[] = new byte[0];
	static Point tlamp[] = new Point[0];
	static Point tlever[] = new Point[0];
	
	
	static void setField(){
		if(Main.ThreadedUpdate){
			Main.logic.terminateWait();
		}
		
		
		
		
	
		tdot = new Point[Field.dot.length];
		tdotSide = new byte[Field.dot.length];
		tlamp = new Point[Field.lamp.length];
		tlever = new Point[Field.lever.length];
		
		for(int i = 0; i < Field.dot.length; i++)
			if(Field.dot[i].Active){
				tdot[i] = new Point(Field.dot[i].x, Field.dot[i].y);
				tdotSide[i] = Field.dot[i].Side;
			}
		for(int i = 0; i < Field.lamp.length; i++)
			if(Field.lamp[i].Active)
				tlamp[i] = new Point(Field.lamp[i].x, Field.lamp[i].y);
		for(int i = 0; i < Field.lever.length; i++)
			if(Field.lever[i].Active)
				tlever[i] = new Point(Field.lever[i].x, Field.lever[i].y);
	}
	static void changed(){
		if(changed||check()){
			if(Display.dispisOn)
			try {
				int[] i = {0};
				System.out.println("check"+check());
				i[2]++;
			} catch (Exception e) {
				e.printStackTrace();
			}
			changed = false;	
			
			
			for(int i = 0; i < Field.dot.length; i++)
				Field.dot[i].number = i;
			for(int i = 0; i < Field.lever.length; i++)
				Field.lever[i].Number = i;
			for(int i = 0; i < Field.lamp.length; i++)
				Field.lamp[i].Number = i;
			setLogicField();
			
			
			if(!Undo)
				Field.undoHistory.add();
			Undo = false;
			
			UpdateField = true;
			
//			undoHistory.print();
		}
		if(Main.ThreadedUpdate){
			Main.logic.start();
		}
		
	}
	static boolean check(){
		if(tdot.length != Field.dot.length || tlever.length != Field.lever.length || tlamp.length != Field.lamp.length)
			return true;
		for(int i = 0; i < Field.dot.length; i++)
			if(Field.dot[i].Active == (tdot[i]==null) || (Field.dot[i].Active && (tdot[i].x != Field.dot[i].x || tdot[i].y != Field.dot[i].y || tdotSide[i] != Field.dot[i].Side)))
				return true;
		for(int i = 0; i < Field.lever.length; i++)
			if(Field.lever[i].Active == (tlever[i]==null) || (Field.lever[i].Active && (tlever[i].x != Field.lever[i].x || tlever[i].y != Field.lever[i].y)))
				return true;
		for(int i = 0; i < Field.lamp.length; i++)
			if(Field.lamp[i].Active == (tlamp[i]==null) || (Field.lamp[i].Active && (tlamp[i].x != Field.lamp[i].x || tlamp[i].y != Field.lamp[i].y)))
				return true;
		return false;
	}
	static void setLogicField(){
		for(Lever pointer : Field.lever){				
			int x = pointer.x, 
			    y = pointer.y;
			pointer.touching[0] = Dot.Close(x+0, y-1);
			pointer.touching[1] = Dot.Close(x+1, y+0);
			pointer.touching[2] = Dot.Close(x-1, y+0);
			pointer.touching[3] = Dot.Close(x+1, y+1);
			pointer.touching[4] = Dot.Close(x-1, y+1);
			pointer.touching[5] = Dot.Close(x+0, y+2);
			
			pointer.touching[6] = Dot.Close(x+0, y+1);	
		}
		for(Lamp pointer : Field.lamp){				
			int x = pointer.x, 
			    y = pointer.y;
			pointer.touching[0] = Dot.Close(x+0, y-1);
			pointer.touching[1] = Dot.Close(x+0, y+1);
			pointer.touching[2] = Dot.Close(x+1, y+0);
			pointer.touching[3] = Dot.Close(x-1, y+0);	
			
			for(int i = 0; i < 4; i++){
				Dot pointer2 = pointer.touching[i];
				if(pointer2 != null && pointer2.isSmall)
					if(pointer2.x + Main.xSide(pointer2.Side) != x || pointer2.y + Main.ySide(pointer2.Side) != y )
						pointer.touching[i] = null;
			}
		}
		LogicUpdate.createLogic();
	}

	
	public static class KA extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			int k = e.getKeyCode();
			setField();
			
			if(!(e.isShiftDown() || e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown())){	
				if(k == KeyEvent.VK_F1) SmallNotification.init("disp.Dots: "   + (Display.dispDots   = !Display.dispDots));
				if(k == KeyEvent.VK_F2) SmallNotification.init("disp.isOn: "   + (Display.dispisOn   = !Display.dispisOn));
				if(k == KeyEvent.VK_F3) SmallNotification.init("disp.Debug: "  + (Display.dispDebug  = !Display.dispDebug));
				if(k == KeyEvent.VK_F4) SmallNotification.init("disp.Number: " + (Display.dispNumber = !Display.dispNumber));
				
				
				if(k == KeyEvent.VK_F10) Save.loadField("Test");
				if(k == KeyEvent.VK_F11) Main.ToggleFullScreen = true;
			}
			
			if(GUI.KeyPressed(e))return;
			BoundLevers(e, true);
			
			// Move on Screen
			if(k >= 37 && k <= 40){
				int i = Main.GridSize;
				if(e.isControlDown()){
					System.out.println((Main.fixCoords(Controls.x)+Main.x+Main.GridSize));
					if(k%2==1)
						i = Main.WIDTH-Main.GridSize;
					else
						i = Main.HEIGHT-Main.GridSize;
				}
				else
					if(k%2==1)
						x += (((byte)(k-1)&2)-1)*i;
					else
						y += (((byte)(k-1)&2)-1)*i;
				
				if((k-1&1)==0)
					if(k==39) Main.x-=Math.max(0, (Main.fixCoords(Controls.x)+Main.x+Main.GridSize)-Main.WIDTH);
					else Main.x-=Math.min(0, Main.fixCoords(Controls.x)+Main.x);
				else
					if(k==40) Main.y-=Math.max(0, (Main.fixCoords(Controls.y)+Main.y+Main.GridSize)-Main.HEIGHT);
					else Main.y-=Math.min(0, Main.fixCoords(Controls.y)+Main.y);
					
				
				cx = Main.fixCoords(x);
				cy = Main.fixCoords(y);
				fx = cx/Main.GridSize;
				fy = cy/Main.GridSize;
			}
			
			
			//On-screen utilities
			
			if(k == KeyEvent.VK_SPACE && !Updatespeed.on){
				LogicUpdate.safeUpdate();
				LogicUpdate.updateGraphics();
			}
			if(k==KeyEvent.VK_ESCAPE)	deselectAll();
			if(k==KeyEvent.VK_DELETE){
				if(amountSelected()== 0){
					for (Dot pointer : Field.dot)
						if(pointer.x == fx && pointer.y == fy)
							pointer.remove();
					for (Lever pointer : Field.lever)
						if(pointer.x == fx && (pointer.y == fy || pointer.y+1 == fy))
							pointer.remove();
					for (Lamp pointer : Field.lamp){
						if(pointer.x == fx && pointer.y == fy)
							pointer.remove();
					}
				}
				else{
					for(Dot pointer : Field.dot)
						if(pointer.isSelected)
							pointer.remove();
					for(Lever pointer : Field.lever)
						if(pointer.isSelected)
							pointer.remove();
					for(Lamp pointer : Field.lamp){
						if(pointer.isSelected)
							pointer.remove();
					}
				}
			}
			
			// Hotkeys
			if(e.isControlDown()){
				if(k == KeyEvent.VK_A){
					for(Dot pointer : Field.dot)
						if(pointer.Active)
							pointer.isSelected = true;
					for(Lever pointer : Field.lever)
						if(pointer.Active) 
							pointer.isSelected = true;
					for(Lamp pointer : Field.lamp)
						if(pointer.Active)
							pointer.isSelected = true;
				}
				if(k == KeyEvent.VK_W)	Warning.init("Exit", "Do you really want to exit program?", Warning.YesCancel);
				if(k == KeyEvent.VK_N)	Warning.init("Clear", "Confirm on exit?", Warning.YesCancel);
				
				if(k == KeyEvent.VK_S)	TextInput.Init("Save");
				if(k == KeyEvent.VK_O)	TextInput.Init("Open");
				if(k == KeyEvent.VK_I)	TextInput.Init("Import");
				
				if(k == KeyEvent.VK_C)	Field.Clipboard.copy();
				if(k == KeyEvent.VK_V){
					deselectAll();
					Field.Clipboard.paste();
				}
				if(k == KeyEvent.VK_Z)
					Field.undoHistory.undo();
				if(k == KeyEvent.VK_Y)
					Field.undoHistory.redo();
				
				
				if(k == KeyEvent.VK_X){
					Field.Clipboard.copy();
					for (Dot pointer : Field.dot)
						if(pointer.isSelected)
							pointer.remove();
					for (Lever pointer : Field.lever)
						if(pointer.isSelected)
							pointer.remove();
					for (Lamp pointer : Field.lamp)
						if(pointer.isSelected)
							pointer.remove();						
					
				}
			}
			
			if(k == KeyEvent.VK_SHIFT)shift = true;
			if(k == KeyEvent.VK_CONTROL)ctrl = true;
			if(k == KeyEvent.VK_TAB)tab = true;
			if(k == KeyEvent.VK_X)stick = true;
			
			changed();			
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
		}
		public void keyReleased(KeyEvent e){
			setField();
			int k = e.getKeyCode();
			BoundLevers(e, false);
			if(k == KeyEvent.VK_SHIFT)shift = false;
			if(k == KeyEvent.VK_CONTROL)ctrl = false;
			if(k == KeyEvent.VK_TAB)tab = false;
			if(k == KeyEvent.VK_X)stick = false;
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
			changed();
		}
	}
	
	public static class MA extends MouseAdapter{
		public void mouseWheelMoved(MouseWheelEvent e){
			
			int mr = e.getWheelRotation();
			if(ctrl){
				int lG = Main.GridSize;
				Zoom -= mr;
				if(Zoom<=0)Zoom = 1;
				if(Zoom>=12)Zoom = 11; 
				Main.GridSize = (int) Math.pow(5.0/3,Zoom);
				Main.x = -(int) ((x*1.0/lG)*Main.GridSize-(x+1.0*Main.x));
				Main.y = -(int) ((y*1.0/lG)*Main.GridSize-(y+1.0*Main.y));
				Display.UpdateFieldImage = true;
			}else if(tab){
				Updatespeed.on = true;
				Updatespeed.position+=e.getWheelRotation()*5;
				if(Updatespeed.position < 0)Updatespeed.position = 0;
				if(Updatespeed.position > 125)Updatespeed.position = 125;
			}else
				Hotbar.selectedSlot = (byte)((((Hotbar.selectedSlot+e.getWheelRotation())%Hotbar.IDinslot.length)+Hotbar.IDinslot.length)%Hotbar.IDinslot.length);
			
			MouseMovement(MovementType.Move,e);
			
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
			
			
			
			
			//Display.UpdateField();
		}
		public void mousePressed(MouseEvent e){
			setField();
			m = e.getButton();
			MouseMovement(MovementType.Press,e);
			
			if(GUI.MouseInGUI()){
				if(m==1){
					if(Main.CloseCoord((int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position, Updatespeed.y+Updatespeed.dy/2, Updatespeed.BallSize/2))
						Updatespeed.isMoving = true;					
					if(MouseInRegion(GUI.TextInput.x, GUI.TextInput.y, GUI.TextInput.dx, GUI.BarHeight)){
						TextInput.sx = ax-TextInput.x;
						TextInput.sy = ay-TextInput.y;
						TextInput.Move = true;
					}
					TextInput.box.SelText = MouseInRegion(TextInput.x+TextInput.box.x+8, TextInput.y+TextInput.box.y, TextInput.box.dx, TextInput.box.dy);
					if(TextInput.box.SelText){
						if(!shift){
						TextInput.box.Cursor = (byte)((ax-TextInput.x-TextInput.box.x-6)/18);
						if(TextInput.box.Cursor > TextInput.box.text.length()) TextInput.box.Cursor = (byte)TextInput.box.text.length();
						if(TextInput.box.Cursor < 0) TextInput.box.Cursor = 0;
						}
						TextInput.box.SelTill = (byte)((ax-TextInput.x-TextInput.box.x-6)/18);
						if(TextInput.box.SelTill > TextInput.box.text.length()) TextInput.box.SelTill = (byte)TextInput.box.text.length();
						if(TextInput.box.SelTill < 0) TextInput.box.SelTill = 0;
					}
					
					
					for(int i = 0; i < Hotbar.IDinslot.length; i++)
						if(Controls.StillMouseInRegion(Hotbar.x+i*Hotbar.slotSize, Hotbar.y, Hotbar.slotSize, Hotbar.slotSize))
							Hotbar.selectedSlot = (byte)i;
				}
			}
			else{
				if(m == 3){
					if(Dot.isClose(fx, fy)){
						if(!Dot.Close.isSelected)
							if(!shift)
								deselectAll();
						Dot.Close.isSelected = true;
						Connecting = true;
					}else
						MoveOnScreen = true;
				}
				if(m == 1){
					if(isAnyClose(fx, fy)){
						if(!isTileSelected(fx, fy)){
							if(!shift)
								deselectAll();
							if(Dot.isClose(fx, fy))
								Dot.Close.isSelected = true;
							if(Lever.isClose(fx, fy))
								Lever.Close(fx, fy).isSelected = true;
							if(Lamp.isClose(fx, fy))
								Lamp.Close(fx, fy).isSelected = true;
						}
						if(ctrl){
							Field.Clipboard.copy();
							for(Tile pointer : Field.dot) {
								if(shift)
								pointer.tempSel = pointer.isSelected;
								pointer.isSelected = pointer.Pasted = false;
							}
							for(Tile pointer : Field.lever) {
								if(shift)
								pointer.tempSel = pointer.isSelected;
								pointer.isSelected = pointer.Pasted = false;
							}
							for(Tile pointer : Field.lamp) {
								if(shift)
								pointer.tempSel = pointer.isSelected;
								pointer.isSelected = pointer.Pasted = false;
							}
							Field.Clipboard.paste();
							
						}
					}else{
						if(!shift){
							deleteDuplicates();
							deselectAll();
						}
						GroupSelect = true;
					}
				}
					
			}
			if(!((Menu.Opened && Controls.MouseInRegion(Menu.x, Menu.y+Menu.dy, Menu.dx, Menu.dy*Menu.items.length/2)) || MouseInRegion(Menu.x, Menu.y, Menu.dx, Menu.dy)))
				Menu.Opened = false;
			if(Menu.Opened && MouseInRegion(Menu.x, Menu.y+Menu.dy, Menu.dx, Menu.dy*Menu.items.length/2))
				Menu.StartSel = (byte) ((ay-(Menu.y+Menu.dy))/(Menu.dy/2)+1);
			else Menu.StartSel = 0;
			
			
			
			
			TextInput.box.Type = MouseInRegion(TextInput.x+TextInput.box.x, TextInput.y+TextInput.box.y, TextInput.box.dx, TextInput.box.dy);
			lx = ax;
			ly = ay;
			MouseDown = true;
			changed();
			//UpdateField = true;
			
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
		}
		public void mouseReleased(MouseEvent e){
			MouseDown = false;
			setField();
			int m = e.getButton();	
			if(GUI.MouseInGUI())
				GUI.MouseReleased(m);			
			else {
				Menu.Opened = false;
				if(!dragged){
					if(m == 3){
						deselectAll();
						
						if(isAnyClose(fx, fy)){
							
							if(Lever.isClose(fx, fy)){
								Lever.Close.isOn = !(Lever.Close).isOn;
								changed = true;
							}
						}
						else{
							int s = Hotbar.IDinslot[Hotbar.selectedSlot];
							if(s == 1) Dot.add(fx,fy);
							else if(s == 2){ if(!Lamp.isClose(fx, fy+1) && !Lever.isClose(fx, fy+1)) Lever.add(fx,fy);}
							else if(s == 3) Lamp.add(fx,fy);
							else if(s == 4) Dot.add(fx,fy,true,false,(byte)0);
							else if(s == 5)	Dot.add(fx,fy,false,true,Side());
						}
					}
				}
				else{
					if(m == 3){
						if(Connecting){
							if(shift){
								for(Dot pointer : Field.dot)
									if(pointer.isSelected) 
										if(Dot.isClose(pointer.x+(fx-Main.fixGridCoords(sx)), pointer.y+(fy-Main.fixGridCoords(sy))))
											pointer.connectTo(Dot.Close);
							}
							else
								if(Dot.isClose(fx, fy)){
									for(Dot pointer : Field.dot)
										if(pointer.isSelected) 
											pointer.connectTo(Dot.Close);
								}
							//DeselectAll();
						}
					}
					else if(m == 1){
						if(MoveTile){
							if(MoveNotPossible){
								for(Dot pointer : Field.dot){
									pointer.ncoords(pointer.x, pointer.y);
									pointer.nSide = pointer.Side;
								}
								for(Lever pointer : Field.lever)
									if(pointer.isSelected)pointer.ncoords(pointer.x, pointer.y);
								for(Lamp pointer : Field.lamp){
									if(pointer.isSelected)
										pointer.ncoords(pointer.x, pointer.y);
								}
							}
							else{
								
								for(Dot pointer : Field.dot)
									if(pointer.isSelected){
										pointer.coords(pointer.nx,pointer.ny);
										pointer.Side = pointer.nSide;
									}
								for(Lever pointer : Field.lever)
									if(pointer.isSelected)
										pointer.coords(pointer.nx,pointer.ny);
								for(Lamp pointer : Field.lamp){
									if(pointer.isSelected)
										pointer.coords(pointer.nx,pointer.ny);
								}
								
								deleteDuplicates();
							}
//							if(fsx != fx || fsy != fy)
//								changed = true;
							for(Dot pointer : Field.dot){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
							for(Lever pointer : Field.lever){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
							for(Lamp pointer : Field.lamp){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
						}
						else if(GroupSelect){
							for(Dot pointer : Field.dot){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
							for(Lever pointer : Field.lever){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
							for(Lamp pointer : Field.lamp){
								pointer.isSelected = pointer.isSelected || pointer.tempSel;
								pointer.tempSel = false;
							}
						}
					}
				}
			}
			
			if(Connecting)deselectAll();
			Connecting = false;
			GroupSelect = false;	
			dragged = false;			
			MoveTile = false;
			MoveOnScreen = false;
			
			MouseMovement(MovementType.Release,e);
			
			
			Updatespeed.isMoving = false;
			changed();
			Menu.Ontop = MouseInRegion(Menu.x,Menu.y,Menu.dx,Menu.dy);
			TextInput.Move = false;
			Updatespeed.Ontop = MouseInRegion(Updatespeed.x,Updatespeed.y,Updatespeed.dx,Updatespeed.dy);
			//UpdateField = true;
			
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
		}
	}
	
	public static class MMA extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent e){
			//if(Main.fixCoords(lx)!=Main.fixCoords(ax-Main.x) || Main.fixCoords(ly)!=Main.fixCoords(ay-Main.y)){
			//	UpdateField = true;
				//System.out.println(Main.fixCoords(lx)+","+Main.fixCoords(e.getX()-Main.x)+"-"+Main.fixCoords(ly)+","+Main.fixCoords(e.getY()-Main.y));
			//}
			if(!MoveOnScreen){
				MouseMovement(MovementType.Move,e);
				
				if(Menu.Opened && MouseInRegion(Menu.x, Menu.y+Menu.dy, Menu.dx, Menu.dy*Menu.items.length/2))
					Menu.Selected = (byte) ((ay-(Menu.y+Menu.dy))/(Menu.dy/2)+1);
				else Menu.Selected = 0;
				Menu.Ontop = MouseInRegion(Menu.x,Menu.y,Menu.dx,Menu.dy);
				Updatespeed.Ontop = MouseInRegion(Updatespeed.x,Updatespeed.y,Updatespeed.dx,Updatespeed.dy);
				TextInput.Ontop = MouseInRegion(TextInput.x,TextInput.y,TextInput.dx,TextInput.dy);
				Warning.Ontop = MouseInRegion(Warning.x,Warning.y,Warning.dx,Warning.dy);
				Main.Hand=GUI.HandCursor();
				Main.Type=GUI.TypeCursor();
				
				if(Warning.visible){
					Warning.StartSel = 0;
					for(byte i = 0; i < Warning.buttons.length; i++)
						if(MouseInRegion(Warning.buttons[i].x+Warning.x, Warning.buttons[i].y+Warning.y, Warning.buttons[i].dx, Warning.buttons[i].dy))
							Warning.StartSel = ++i;
				}
			}
			
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
		}		
		public void mouseDragged(MouseEvent e){
			//SetField();
			//UpdateField = true;
			if(MoveOnScreen && !stick){
				Main.x -= lx - (e.getX()-(Main.FullScreen?0:8));
				Main.y -= ly - (e.getY()-(Main.FullScreen?0:30));
			}
			if(m == 1 && !MoveTile && (fsx != fx || fsy != sy) && isAnyClose(fsx,fsy)){
				if(!isTileSelected(fsx,fsy)){
					if(!shift)
						deselectAll();
					if(Dot.isClose(fsx, fsy))
						Dot.Close.isSelected = true;
					if(Lever.isClose(fsx, fsy))
						Lever.Close(sx,sy).isSelected = true;
					if(Lamp.isClose(fsx, fsy))
						Lamp.Close(sx,sy).isSelected = true;
				}
					
				MoveTile = true;
			}
			MouseMovement(MovementType.Drag,e);
			dragged = true;
			
			if(Updatespeed.isMoving){
				Updatespeed.on = true;
				Updatespeed.maxSpeed = false;
				Updatespeed.position = ax-(int)(Updatespeed.x+Updatespeed.dx*0.15);
				if(Updatespeed.position <= 0) {
					Updatespeed.position = 0;
					Updatespeed.on = false;
				}
				if(Updatespeed.position >= Updatespeed.dx*0.7 ){
					Updatespeed.position = (int)(Updatespeed.dx*0.7);
					Updatespeed.maxSpeed = true;
				}
				UpdateChanged = Updatespeed.maxSpeed;
				Updatespeed.setDelay();
			}
			else if(TextInput.Move){
				TextInput.x = ax-TextInput.sx;
				TextInput.y = ay-TextInput.sy;
				
				TextInput.x = Math.min(Main.WIDTH-TextInput.dx-5,Math.max(5, TextInput.x));
				TextInput.y = Math.min(Main.HEIGHT-TextInput.dy-5,Math.max(30, TextInput.y));
				;
				if(TextInput.y+TextInput.dy > Main.HEIGHT)TextInput.y = Main.HEIGHT-TextInput.dy;
				
				if(TextInput.y < Menu.dy && TextInput.x < Menu.dx+Updatespeed.dx)TextInput.x = Menu.dx+Updatespeed.dx;
				if(TextInput.x < Menu.dx+Updatespeed.dx && TextInput.y < Menu.dy)TextInput.y = Menu.dy;
				
			}
			else
			if(MoveTile){
				MoveNotPossible = false;
				
				int dgx = fx - fsx; 
				int dgy = fy - fsy;
				
				for(int i = 0; i < Field.dot.length; i++)
					if(Field.dot[i].isSelected){
						Field.dot[i].ncoords(Field.dot[i].x+dgx,Field.dot[i].y+dgy);
						if(isAnyClose(Field.dot[i].nx,Field.dot[i].ny) 
						   && !isSelected(Field.dot[i].nx, Field.dot[i].ny))
							if(Dot.isClose(Field.dot[i].nx, Field.dot[i].ny)){
								if(Field.dot[i].isInvert != Dot.Close.isInvert ||
								   Field.dot[i].isSmall != Dot.Close.isSmall)
									MoveNotPossible = true;
							} else MoveNotPossible = true;
					}
				for(int i = 0; i < Field.lever.length; i++)
					if(Field.lever[i].isSelected){
						Field.lever[i].ncoords(Field.lever[i].x+dgx,Field.lever[i].y+dgy);
						if((isAnyClose(Field.lever[i].nx,Field.lever[i].ny) && !isSelected(Field.lever[i].nx, Field.lever[i].ny))	|| 
						   (Lever.isClose(Field.lever[i].nx,Field.lever[i].ny+1) && !Lever.Close.isSelected) ||
						   (Lamp.isClose(Field.lever[i].nx,Field.lever[i].ny+1) && !Lamp.Close.isSelected))
							MoveNotPossible = true;
					}
				for(Lamp pointer : Field.lamp){
					if(pointer.isSelected){
						pointer.ncoords(pointer.x+dgx,pointer.y+dgy);
						if(isAnyClose(pointer.nx,pointer.ny) && !isSelected(pointer.nx, pointer.ny))
							MoveNotPossible = true;
					}
				}
				if(amountSelected() == 1 || (shift && MoveTile && amountSelected()>1 && (Dot.isClose(fsx, fsy) && Dot.Close.isSmall))){
					for(int i = 0; i < Field.dot.length; i++)
						if(Field.dot[i].isSmall && Field.dot[i].isSelected)
							Field.dot[i].nSide = Side();
				}else
					for(int i = 0; i < Field.dot.length; i++)
						if(Field.dot[i].isSmall && Field.dot[i].isSelected)
								Field.dot[i].nSide = Field.dot[i].Side;
				
			} else if (GroupSelect) {
				if (!(shift || MoveTile))
					deselectAll();
				int tx = Math.min(fsx, fx);
				int ty = Math.min(fsy, fy);
				int dx = Math.max(fsx, fx);
				int dy = Math.max(fsy, fy);

				for (Dot pointer : Field.dot)
					pointer.tempSel = pointer.isInBox(tx, ty, dx, dy);
				for (Lever pointer : Field.lever)
					pointer.tempSel = pointer.isInBox(tx, ty, dx, dy);
				for (Lamp pointer : Field.lamp)
					pointer.tempSel = pointer.isInBox(tx, ty, dx, dy);
			}
			if (TextInput.box.SelText) {
				TextInput.box.SelTill = (byte) ((ax - TextInput.x
						- TextInput.box.x - 6) / 18);
				if (TextInput.box.SelTill > TextInput.box.text.length())
					TextInput.box.SelTill = (byte) TextInput.box.text.length();
				if (TextInput.box.SelTill < 0)
					TextInput.box.SelTill = 0;
			}
			Dot.DrawCoords();
			Lever.DrawCoords();
			Lamp.DrawCoords();
		}
	}	
	
	
	
	
	
	
	
	static boolean MouseInRegion(int x, int y, int sx, int sy, int dx, int dy){
		return (x >= sx &&
				x < sx + dx &&
				y >= sy &&
				y < sy + dy);
	}	
	static boolean MouseInRegion(int sx, int sy, int dx, int dy){
		return (ax >= sx &&
				ax < sx + dx &&
				ay >= sy &&
				ay < sy + dy);
	}
	static boolean sMouseInRegion(int sx, int sy, int dx, int dy){
		return (asx >= sx &&
				asx < sx + dx &&
				asy >= sy &&
				asy < sy + dy);
	}
	static boolean StillMouseInRegion(int x, int y, int dx, int dy){
		return (ax >=x    &&
				ax < x+dx &&
				ay >=y    &&
				ay < y+dy)&&
			   (asx >=x    &&
				asx < x+dx &&
				asy >=y    &&
				asy < y+dy);
	}	
	
	static void MouseMovement(MovementType t, MouseEvent e){
		switch (t){
			case Press:
			case Release:
				asx = e.getX()-(Main.FullScreen?0:8);
				asy = e.getY()-(Main.FullScreen?0:30);
				sx = asx-Main.x;
				sy = asy-Main.y;
				fsx = Main.fixCoords(asx-Main.x)/Main.GridSize;
				fsy = Main.fixCoords(asy-Main.y)/Main.GridSize;
			case Drag:
				lx = ax;
				ly = ay;
			case Zoom:
			case Move:
				ax = e.getX()-(Main.FullScreen?0:8);
				ay = e.getY()-(Main.FullScreen?0:30);
				x = ax-Main.x;
				y = ay-Main.y;
				lx = ax;
				ly = ay;
				cx = Main.fixCoords(x);
				cy = Main.fixCoords(y);
				fx = cx/Main.GridSize;
				fy = cy/Main.GridSize;
			default:
				break;
		}
		if(t == MovementType.Zoom){
			
		}
	}
	static enum MovementType{
		Drag, Move, Press, Release, Zoom;
	}
	
	static void TurnOnLeversBoundTo(String s, boolean On){
		for(int i = 0; i < Field.lever.length; i++)
			for(int l = 0; l < Field.lever[i].BoundTo.length; l++)
				if(Field.lever[i].BoundTo[l].c == new KeyName(s).c && Field.lever[i].BoundTo[l].second == new KeyName(s).second)
					Field.lever[i].isOn = On;
	}	
	
	static String KeyPressed(KeyEvent e){
		int k = e.getKeyCode();
		if(((k>=48 && k<=57) || (k>64 && k<91)) || k == 32)
			return (((char)k)+"");
		else if(k == KeyEvent.VK_SHIFT) return "SH";
		else if(k == KeyEvent.VK_ENTER) return "EN";
		else if(k == KeyEvent.VK_TAB) return "TA";
		else if(k == KeyEvent.VK_COMMA) return "CO";
		else if(k == KeyEvent.VK_DECIMAL) return "PO";
		else if(k == KeyEvent.VK_F5) return "F5";
		else if(k == KeyEvent.VK_UP)   return "UP";
		else if(k == KeyEvent.VK_DOWN) return "DO";
		else if(k == KeyEvent.VK_LEFT) return "LE";
		else if(k == KeyEvent.VK_RIGHT)return "RI";
		else return "";
	}	
	static void BoundLevers(KeyEvent e, boolean On){
		int k = e.getKeyCode();
		TurnOnLeversBoundTo(KeyPressed(e), On);
		k= k+1;
	}	
	static class KeyName{
		KeyName(String s){
			if(s != null){
				if(s.length() > 0)
					c = s.toCharArray()[0];
				second = s.length()>1;
			}	
		}
		boolean second;
		char c;
	}
	static String KeyNametoString(KeyName k){
		String c = new Character(k.c).toString();
		if(k.second){
			
		}
		return c;
	}
	
	static void deselectAll(){
		for(Dot pointer : Field.dot)
			pointer.isSelected = pointer.Pasted = false;
		for(Lever pointer : Field.lever)
			pointer.isSelected = pointer.Pasted = false;
		for(Lamp pointer : Field.lamp)
			pointer.isSelected = pointer.Pasted = false;
	}
	static void deleteDuplicates(){
		for(Dot pointer1 : Field.dot){
			if(pointer1.isSelected)
			for(Dot pointer2 : Field.dot)
				if(pointer1.Active && pointer2.Active &&
				   (pointer1.x == pointer2.x &&
					pointer1.y == pointer2.y &&
					pointer1.number != pointer2.number &&
					pointer1.isInvert == pointer2.isInvert &&
					pointer1.isSmall == pointer2.isSmall &&
				   (pointer1.isSmall?(pointer1.Side == pointer2.Side):true))){
					for(Dot pointer3 : pointer2.ConnectedTo)
						pointer1.connectTo(pointer3);
					pointer2.remove();
				}
		}
		for(Lever pointer1 : Field.lever)
			for(Lever pointer2 : Field.lever)
				if(pointer1.Active && pointer2.Active && 
					pointer1.x == pointer2.x &&
					pointer1.y == pointer2.y &&
					pointer1 != pointer2)
					pointer2.remove();
		
		for(Lamp pointer1 : Field.lamp)
			for(Lamp pointer2 : Field.lamp)
				if(pointer1.Active && pointer2.Active && 
					pointer1.x == pointer2.x &&
					pointer1.y == pointer2.y &&
					pointer1 != pointer2)
					pointer2.remove();			
		
	}
	
	static int amountSelected(){
		int l = 0;
		for(Dot pointer : Field.dot)
			if(pointer.isSelected)l++;
		for(Lever pointer : Field.lever)
			if(pointer.isSelected)l++;
		for(Lamp pointer : Field.lamp)
			if(pointer.isSelected)l++;
		return l;
	}
	static boolean isSelected(int x, int y){
		if(Dot.isClose(x, y))
			return Dot.Close.isSelected;
		if(Lever.isClose(x, y))
			return Lever.Close(x, y).isSelected;
		if(Lamp.isClose(x, y))
			return Lamp.Close.isSelected;
		return false;
	}
	static boolean isAnyLeverSelected(){
		for(Lever pointer : Field.lever)
			if(pointer.isSelected) return true;
		return false;
	}
	static boolean isTileSelected(int x, int y){
		for(Dot pointer : Field.dot)
			if(pointer.DotisClose(x, y)&&pointer.isSelected)
				return true;
		for(Lever pointer : Field.lever)
			if(pointer.LeverisClose(x, y)&&pointer.isSelected)
				return true;
		for(Lamp pointer : Field.lamp)
			if((pointer.LampisClose(x, y)&&pointer.isSelected)) //|| (pointer.iLampisClose(x, y)&&pointer.iSelected))///
				return true;
		return false;
	}
	static boolean isAnyClose(int x, int y){
		return Dot.isClose(x, y) || Lever.isClose(x, y) || Lamp.isClose(x, y);
	}
	static Tile tileClose(int x, int y){
		Tile t;
		if((t = Dot.Close(x, y)) != null)
			return t;
		if((t = Lever.Close(x, y)) != null)
			return t;
		if((t = Lamp.Close(x, y)) != null)
			return t;
		return null;
	}
	
	static byte Side(){
		double tx = x-cx-Display.GridSize2,
			   ty = y-cy-Display.GridSize2;
		if(ty == 0 || Math.abs(tx/ty)>=1) 
			return (byte) (tx>0?3:1);
		return (byte) (ty>0?2:0);
	}
	
	
}
