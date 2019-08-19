package main.java;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import main.java.Debug.TimeWaypoint;
import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;

public class GUI {	
	static int BarHeight = 15;	
	
	static class Hotbar{
		static byte selectedSlot;
		static byte startSelectedSlot;
		static byte IDinslot[] = {1,2,3,4,5,0,0,0,0};
		static int slotSize = 70;
		
		static int x = (Main.WIDTH-GUI.Hotbar.IDinslot.length*GUI.Hotbar.slotSize)/2,
				   y = Main.HEIGHT-GUI.Hotbar.slotSize-8;
		static boolean DragOut;
		
		static int box = (int) (slotSize*0.1), dbox = (int) (slotSize*0.8);
		
		static boolean insideBox(int ax, int ay){
			return ax >= x && ay >= y && ax < x + slotSize*IDinslot.length && ay < y + slotSize;
		}
	}
	static class Updatespeed{
		static int position = 0;
		static int barlength = 100;
		static int BallSize = 10;
		static int dx = 180,dy = 75;
		static int x = (int)(75*1.5),y = 0;
		
		static boolean maxSpeed = false;
		static boolean on = false;
		static long Delay = LFormula();
		static boolean Ontop;
		
		static int fac(int x){
			if(x==0) return 1;
			return fac(x-1)*x;
		}
		static double Formula(){
			return (120.0/fac(6-(position-position%25+25)/25)-120.0/fac(6-(position-position%25)/25))/25.0*(position%25)+120.0/fac(6-(position-position%25)/25);
		}
		static long LFormula(){
			return (long) (1_000_000_000/Formula());
		}
		static void setDelay(){
			Delay = LFormula();
			Main.LogicDelay = System.nanoTime()+Delay;
		}
		
		static String Text = "Update Delay";
		static Font f = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		
		static boolean isMoving;
		
		static boolean insideBox(int ax, int ay){
			return ax >= x && ay >= y && ax < x + dx && ay < y + dy;
		}
	}
	static class Menu{
		static String Text =  "Menu";
		static boolean Ontop;
		
		static int x=0, y=0;
		static int dx=112, dy=75;
		static byte Selected;
		static byte StartSel;
		static boolean Opened;

		static long ClearAnimation;
		
		
		static Font f = new Font(Font.MONOSPACED, Font.BOLD, 20);
		static Font sf = new Font(Font.MONOSPACED, Font.PLAIN, 10);
		static Item[] items = {
			new Item("Settings","F5"),
			new Item("Clear", "CTRL+N"),
			new Item("Save", "CTRL+S"),
			new Item("Open", "CTRL+O"),
			new Item("Import", "CTRL+I"),
			new Item("Exit", "CTRL+W")
			};
		
		
		static class Item{
			Item(String n, String s){
				Name = n;
				ShortCut = s;
			}
			String Name;
			String ShortCut;
		}
		
		static boolean insideBox(int ax, int ay){
			return ax >= x && ay >= y && ax < x + dx && ay < y + dy;
		}
	}

	static class Graph{
		Graph(String style, int x, int y, int dx, int dy, int min, int max, int lists, int len){
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.min = min;
			this.max = max;
			this.style = style;
			items = new double[lists][len];
			
			generatedImage = new BufferedImage(dx,dy,BufferedImage.TYPE_INT_RGB);
			Graphics temp = generatedImage.getGraphics();
			temp.setColor(new Color(0,0,0));
			temp.fillRect(0, 0, dx, dy);
			temp.dispose();
		}
		Graph(String style, int x, int y, int dx, int dy, int min, int max, int lists, int len, Color[] pallet){
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
			this.min = min;
			this.max = max;
			this.style = style;
			items = new double[lists][len];
			this.pallet = pallet;
			
			generatedImage = new BufferedImage(dx,dy,BufferedImage.TYPE_INT_RGB);
			Graphics temp = generatedImage.getGraphics();
			temp.setColor(new Color(0,0,0));
			temp.fillRect(0, 0, dx, dy);
			temp.dispose();
		}
		
		void Translate(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		void Add(int list, double d){
			for(int l = 1; l < items[list].length; l++)
				items[list][l-1] = items[list][l];
			items[list][items[list].length-1] = d;
			
			
		}
		void UpdatedrawGraph(){
			if(style.equals("Percentages")){
				if(items[0] == null)
					return;
				int len = items[0].length;
				double width = dx/(len);
				double startx = dx-width;
				double h = dy;

				BufferedImage temp = new BufferedImage(dx, dy, BufferedImage.TYPE_INT_RGB);
				Graphics tempg = temp.getGraphics();
				tempg.drawImage(generatedImage, (int)-width, 0, null);
				for(int i = 0; i < items.length; i++){
					tempg.setColor(pallet[i]);
					tempg.fillRect((int)startx,0,(int)width,(int)h);
					h=Math.max(0, h-(items[i][len-1])*dy);
				}
				tempg.dispose();
				generatedImage = temp;
			}else{
				int len = items[0].length;
				double width = dx/(len);
				double height = dy/(max-min);
				drawItems = new int[items.length][2][len];
				
				if(max == min)
					for(double[] i : items)
						for(double j : i)
							max = Math.max(max, j);
				for(int l = 0; l < items.length; l++)
					for(int i = 0; i < len; i++){
						drawItems[l][0][i] = (int)(x+i*width);
						drawItems[l][1][i] = (int)(y+height*(min-Math.min(items[l][i],max))+dy);
					}
				
			}
			
		}
		
		void drawGraph(Graphics g){
			if(style.equals("Percentages")){

				
				//System.out.println((int)(-dy*(min-Math.min(items[0][0],max))/(max-min)));
				if(dispscale){
					g.setColor(Color.white);
					g.drawString(max+"", x, y);
				}

				//g.setColor(new Color(0,0,0,128));
				//g.fillRect(x, y, dx, dy);
				
				//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
				g.drawImage(generatedImage, x, y,null);
				//g.setComposite(AlphaComposite.SrcAtop);
				g.setColor(Color.black);
				g.drawRect(x, y, dx, dy);
			}
			
			if(style.equals("Connected")){
				g.setColor(new Color(0,0,0,128));
				g.fillRect(x, y, dx, dy);
				g.setColor(Color.black);
				g.drawRect(x, y, dx, dy);
				int len = items[0].length;
				int l=0;
				for(int[][] items : this.drawItems){
					g.setColor(pallet[l++]);
					g.drawPolyline(items[0], items[1], len);
				}
				if(dispscale){
					g.setColor(Color.white);
					g.drawString(max+"", x, y);
				}
			}
		}
		Image generatedImage;
		
		boolean dispscale = true;
		boolean visible = true;
		double[][] items = new double[0][0];
		int drawItems[][][] = new int[0][2][0];
		int x, y, dx, dy;
		double min, max;
		
		String style;
		
		Color[] pallet = new Color[]{Color.red, Color.blue, Color.green, Color.magenta, Color.cyan, Color.pink, Color.YELLOW, Color.WHITE, Color.black};

		void setWaypoints(TimeWaypoint timeWaypoint) {
			long[] waypoints = timeWaypoint.waypoints;
			int len = 0;
			for(long l : waypoints){
				if(l == 0)	break;
				len++;
			}
			long total = waypoints[len-1] - waypoints[0];
			
			double[] temp = new double[len-1];
			//System.out.println(len);
			//System.out.println(items[0].length);
			for(int i = 1; i < len; i++)
				temp[i-1] = (waypoints[i] - waypoints[i-1]) / (double)total;
			
			for(int l = 0; l < len-1; l++)
				Add(l, temp[l]);
				
		}
		void setWaypoints(TimeWaypoint timeWaypoint, double TOTAL) {
			long[] waypoints = timeWaypoint.waypoints;
			int len = 0;
			for(long l : waypoints){
				if(l == 0)	break;
				len++;
			}
			
			double[] temp = new double[len-1];
			//System.out.println(len);
			//System.out.println(items[0].length);
			for(int i = 1; i < len; i++)
				temp[i-1] = (waypoints[i] - waypoints[i-1]) / TOTAL;
			
			for(int l = 0; l < len-1; l++)
				Add(l, temp[l]);
				
		}
	}
	static class Button{
		
	}
	static class Textbox{
		Textbox(int x, int y, int dx, int dy){
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
		}		
		int x, y, dx, dy;
		boolean Move;
		boolean visible;		
		boolean Type;
		boolean SelText;
		String text = "";
		byte Cursor;
		byte SelTill;
		boolean insert = true;
		
		long Animation;
		
		void TypeInTextInput(KeyEvent e){
			int k = e.getKeyCode();
			System.out.println(e.getKeyChar() + "	" + (e.getKeyChar() == '?'));
			
			if(k == KeyEvent.VK_ESCAPE || (e.isControlDown() && k == KeyEvent.VK_W)){
				visible = false;
				Type = false;
				return;
			}
			if(k == KeyEvent.VK_INSERT) {
				insert = !insert;
				return;
			}
			if(Type){
				if(e.isControlDown() && !e.isShiftDown()){
					if(k == KeyEvent.VK_A){
						Cursor = 0;
						SelTill = (byte) text.length();
					}
					if((k == KeyEvent.VK_C || k == KeyEvent.VK_X) && Cursor != SelTill){
						StringSelection selection = new StringSelection(text.substring(Math.min(Cursor, SelTill), Math.max(Cursor, SelTill)));
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(selection, selection);
						
						if(k == KeyEvent.VK_X){
							text = text.substring(0,Math.min(Cursor, SelTill)) + text.substring(Math.max(Cursor, SelTill),text.length());
							SelTill = Cursor = (byte) Math.min(Cursor, SelTill);
							
						}						
					}
					if (k == KeyEvent.VK_V) {
						String Clipboard = "";
						try {
							Clipboard = ((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
						} catch (HeadlessException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (UnsupportedFlavorException e1) {
							e1.printStackTrace();
							SmallNotification.init("Clipboard does not contain text");
						}
						if (Cursor == SelTill) {
							text = text.substring(0, Cursor) + Clipboard + text.substring(Cursor, text.length());
							SelTill = Cursor += Clipboard.length();
						} else {
							text = text.substring(0, Math.min(Cursor, SelTill)) + Clipboard + text.substring(Math.max(Cursor, SelTill), text.length());
							SelTill = Cursor = (byte) (Math.min(Cursor, SelTill) + Clipboard.length());
						}
						if (text.length() > 12) {
							text = text.substring(0, 12);
							if (Cursor > 12) {
								Cursor = 12;
								SelTill = 12;
							}
						}
					}
				}
				else {
					if (Cursor == SelTill || e.isShiftDown()) {
						if (k == KeyEvent.VK_LEFT && SelTill > 0) SelTill--;
						if (k == KeyEvent.VK_RIGHT && SelTill < text.length()) SelTill++;
					} else {
						if (k == KeyEvent.VK_RIGHT) Cursor = SelTill = (byte) Math.max(SelTill, Cursor);
						if (k == KeyEvent.VK_LEFT) Cursor = SelTill = (byte) Math.min(SelTill, Cursor);
					}
					
					if((e.getKeyChar()+"").matches(standard) && text.length() < 12){
						String newLetter = e.getKeyChar()+"";
						int insertAmount = insert||Math.max(Cursor, SelTill)>=text.length()?0:1;
						text = text.substring(0, Math.min(Cursor, SelTill)) + newLetter + text.substring(insertAmount+Math.max(Cursor, SelTill));
						Cursor = SelTill = (byte) (Math.min(Cursor, SelTill) + 1);
					}
						
					
					if(Cursor == SelTill){
						if(k == KeyEvent.VK_HOME)  Cursor=0;
						if(k == KeyEvent.VK_END)   Cursor=(byte)text.length();
						
						if(k==KeyEvent.VK_DELETE && Cursor < text.length())
							text = text.substring(0,Cursor) + text.substring(Cursor+1,text.length());
						
						if(k==KeyEvent.VK_BACK_SPACE && Cursor > 0){
							text = text.substring(0,Cursor-1) + text.substring(SelTill--,text.length());
							Cursor = SelTill;
						}
						
						
					}else{					
						if(k==KeyEvent.VK_DELETE || k == KeyEvent.VK_BACK_SPACE){	
							text = text.substring(0,Math.min(Cursor, SelTill)) + text.substring(Math.max(Cursor, SelTill));
							SelTill = (byte) (Math.min(Cursor, SelTill));
						}					
					}
					
					if(!e.isShiftDown())
						Cursor = SelTill;
					
					
					
					
				}
			}
		}
	}
	final static String standard = "[a-z|A-Z|0-9|\\.| |\\-|\\_|\\+|\\,|=]";
	
	static class TextInput {
		static void Init(String text) {
			x = (Main.WIDTH - dx) / 2;
			y = (Main.HEIGHT - dy) / 2;
			Text = text;
			visible = true;
			box.Type = true;
			box.Cursor = (byte) box.text.length();
			box.SelTill = (byte) box.text.length();

			Ontop = Controls.MouseInRegion(x, y, dx, dy);
		}

		static void Init(String text, int x1, int y1) {
			x = x1;
			y = y1;
			Text = text;
			visible = true;
			box.Type = true;
			box.Cursor = (byte) box.text.length();
			box.SelTill = (byte) box.text.length();

			Ontop = Controls.MouseInRegion(x, y, dx, dy);
		}

		static int sx, sy;
		static boolean Move;
		static boolean visible;
		static boolean Ontop;
		static int dx = 300, dy = 150, x = (Main.WIDTH - dx) / 2,
				y = (Main.HEIGHT - dy) / 2;
		static Font cf = new Font(Font.MONOSPACED, Font.BOLD, 30);
		static String Text = "";

		static Textbox box = new Textbox(20, 70, dx - 40, 40);
	}

	static class FileSelector {
		static int x, y;
		static int dx, dy;
		static boolean visible;
		static boolean mouseOver;
		static Font cf = new Font(Font.MONOSPACED, Font.BOLD, 30);
		
		static File selected;
		
		
	}
	static class Warning{
		static void init(String text, String warning, Button[] button){
			Text = text;
			Warning = warning;
			buttons = button;
			
			visible = true;
			Ontop = false;
			
			x = (Main.WIDTH-dx)/2;
			y = (Main.HEIGHT-dy)/2;
			
			Ontop = Controls.MouseInRegion(x, y, dx, dy);
		}	
		static boolean visible;
		static boolean Ontop;
		static String Text = "";
		static String Warning = "";
		static int x, y, dx = 300, dy = 150;
		static byte Selected;
		static byte StartSel;
		
		final static Font f = new Font(Font.MONOSPACED, Font.PLAIN, 22);
		
		final static Font bf = new Font(Font.MONOSPACED, Font.PLAIN, 22);
		
		static Button[] buttons;
		
		final static Button[] YesCancel = {
			new Button("Yes", 1, 30),
			new Button("Cancel", 2, 170)
		};		
		
		
		static boolean OnButton(){
			for(int i = 0; i < buttons.length; i++)
				if (Controls.MouseInRegion(x+buttons[i].x, y+buttons[i].y, buttons[i].dx, buttons[i].dy))
					return true;
			return false;
		}
		
		static class Button{
			Button(String Text, int num, int xoff, int dx, int dy){
				this.num = num;
				this.Text = Text;
				x += xoff;
				this.dx = dx;
				this.dy = dy;
			}
			Button(String Text, int num, int xoff){
				this.num = num;
				this.Text = Text;
				x += xoff;
			}
			boolean Ontop;
			int x = 0, y = 90, dx = 100, dy = 40;
			String Text;
			
			int num;
		}
		
	}	
	static class SmallNotification{

		static void init(String s){
			fade = 0;
			visible = true;
			text = s;
			dx = text.length()*11+30;
			x = (Main.WIDTH-dx)/2;
			Duration = 3_500_000_000L+System.nanoTime();
		}
		
		static Font f = new Font(Font.MONOSPACED, Font.PLAIN, 18);
		static String text = "Turned dispDots off";
		
		static int dx = text.length()*11+30, dy = 30;
		static int x = (Main.WIDTH-dx)/2, y = 40;
		
		static boolean visible;
		static long Duration;
		static int fade;
	}
	
	static class Debug{
		static Graph Updates = new Graph("Connected",Main.WIDTH-200,Main.HEIGHT-200,200,200,0,0,5,200);
		static Graph MainTimeDistr = new Graph("Percentages",Main.WIDTH-50,Main.HEIGHT-300,200,100,0,100,3,100,new Color[]{Color.blue, Color.red, Color.green, Color.magenta});
		static Graph logicTimeDistr = new Graph("Percentages",Main.WIDTH-50,Main.HEIGHT-300,200,100,0,100,2,100,new Color[]{Color.blue, Color.red});
		static Graph graphicsTimeDistr = new Graph("Percentages",Main.WIDTH-50,Main.HEIGHT-300,200,200,0,100,9,200);
		static Font f = new Font("Consolas", Font.PLAIN, 20);
		static Font sf = new Font(Font.MONOSPACED, Font.PLAIN, 15);
	}
	
	static void MouseReleased(int m){
		if(!(TextInput.visible && Controls.StillMouseInRegion(TextInput.x+TextInput.box.x, TextInput.y+TextInput.box.y, TextInput.box.dx, TextInput.box.dy)) && !TextInput.box.SelText && Controls.dragged)
			TextInput.box.Type = false;
		if(m == 1){
			if(TextInput.visible){
				if(Controls.StillMouseInRegion(TextInput.x+TextInput.box.x, TextInput.y+TextInput.box.y, TextInput.box.dx, TextInput.box.dy)){
					if(Controls.StillMouseInRegion(TextInput.x+TextInput.box.x, TextInput.y+TextInput.box.y, 8, TextInput.box.dy)){
						TextInput.box.Cursor = TextInput.box.SelTill = 0;
						TextInput.box.text = "";
					}else{
						if(!Controls.dragged){
							TextInput.box.SelTill = (byte)((Controls.ax-TextInput.x-TextInput.box.x-6)/18);
							if(TextInput.box.SelTill > TextInput.box.text.length()) TextInput.box.SelTill = (byte)TextInput.box.text.length();
							if(TextInput.box.SelTill < 0) TextInput.box.SelTill = 0;
						}
					}
				}
			}
			
			
			else if(Menu.Opened){
				if(Controls.StillMouseInRegion(Menu.x, Menu.y+Menu.dy, Menu.dx, Menu.dy*Menu.items.length/2)
						&& (Menu.StartSel == Menu.Selected)){
					if(Menu.Selected == 1){
						
					}
					else if(Menu.Selected == 2)	Warning.init("Clear", "Do you really want to clear screen?", Warning.YesCancel.clone());
					if(Menu.Selected == 3)TextInput.Init("Save");
					if(Menu.Selected == 4)TextInput.Init("Open",Main.WIDTH-TextInput.dx,0);
					if(Menu.Selected == 5)TextInput.Init("Import");
					if(Menu.Selected == 6)Warning.init("Exit", "Confirm on exit?", Warning.YesCancel);
					
					Menu.Opened = false;
				}		
			}
			
			
			else if(Warning.visible){
				Warning.Selected = 0;
				for(byte i = 0; i < Warning.buttons.length; i++)
					if(Controls.MouseInRegion(Warning.buttons[i].x+Warning.x, Warning.buttons[i].y+Warning.y, Warning.buttons[i].dx, Warning.buttons[i].dy))
						Warning.Selected = ++i;
				if(Warning.StartSel != 0 && Warning.StartSel == Warning.Selected){
					if(Warning.Selected == 1){
						if(Warning.Text == "Clear"){
							Menu.ClearAnimation = 200_000_000L+System.nanoTime();
							Field.lever = new Lever[0];
							Field.lamp = new Lamp[0];
							Field.dot = new Dot[0];
						}
						else if(Warning.Text == "Exit") System.exit(1);							
						else if(Warning.Text == "Overwrite") Save.saveField(GUI.TextInput.box.text, true);
						
					}					
					Warning.visible = false;
				}
			}
			if(Controls.StillMouseInRegion(Menu.x, Menu.y, Menu.dx, Menu.dy))
				Menu.Opened = !Menu.Opened && Controls.MouseInRegion(Menu.x, Menu.y, Menu.dx, Menu.dy);
			
			if(Controls.StillMouseInRegion(Warning.x+Warning.dx-GUI.BarHeight, Warning.y, GUI.BarHeight, GUI.BarHeight))
				Warning.visible = false;
			if(Controls.StillMouseInRegion(TextInput.x+TextInput.dx-GUI.BarHeight, TextInput.y, GUI.BarHeight, GUI.BarHeight))
				TextInput.visible = false;
			
		}
		else if(m == 3){
			if(Warning.visible)
				if(Controls.StillMouseInRegion(Warning.x, Warning.y, Warning.dx, GUI.BarHeight))
					Warning.visible = false;
			
			if(TextInput.visible)
				if(Controls.StillMouseInRegion(TextInput.x, TextInput.y, TextInput.dx, GUI.BarHeight))
					TextInput.visible = false;
		}
	}
	static boolean KeyPressed(KeyEvent e){
		int k = e.getKeyCode();
		
		
		if(Warning.visible){
			if(k == KeyEvent.VK_ENTER){
				if(Warning.Text == "Clear"){
					Menu.ClearAnimation = 200_000_000L+System.nanoTime();
					Field.lever = new Lever[0];
					Field.lamp = new Lamp[0];
					Field.dot = new Dot[0];
					SmallNotification.init("Cleared Screen");
				}
				else if(Warning.Text == "Exit") System.exit(1);							
				else if(Warning.Text == "Overwrite") Save.saveField(GUI.TextInput.box.text, true);
				Warning.visible = false;
			}else if(k == KeyEvent.VK_ESCAPE || k == KeyEvent.VK_DELETE)
				Warning.visible = false;
			
			return true;
		}
		
		
		if(TextInput.visible){			
			if(TextInput.box.Type){
				TextInput.box.TypeInTextInput(e);
				if(k==KeyEvent.VK_ENTER){					
					if(TextInput.Text.equals("Save")) Save.saveField(TextInput.box.text, false);
					if(TextInput.Text.equals("Open")) Save.loadField(TextInput.box.text);
					if(TextInput.Text.equals("Import")) Save.loadFieldToClipboard(TextInput.box.text);
					TextInput.box.Type = false;
					
					TextInput.visible = false;
				}
			}
			if(k==KeyEvent.VK_ESCAPE || (k == KeyEvent.VK_DELETE && !TextInput.box.Type))
				TextInput.visible = false;
			
			return true;
		}
		
		if(k == KeyEvent.VK_ESCAPE) Menu.Opened = !Menu.Opened;
		if(Menu.Opened){
			if(k == KeyEvent.VK_DOWN) Menu.Selected = (byte) (Menu.Selected+1 % Menu.items.length);
			if(k == KeyEvent.VK_UP) Menu.Selected = (byte) (Menu.Selected-1 % Menu.items.length);
			Menu.StartSel = Menu.Selected;
			
			if(k==KeyEvent.VK_ENTER){				
				if(Menu.Selected == 1){
					
				}							
				if(Menu.Selected == 2) Warning.init("Clear", "Do you really want to clear screen?", Warning.YesCancel.clone());
				if(Menu.Selected == 3) TextInput.Init("Save");
				if(Menu.Selected == 4) TextInput.Init("Open");
				if(Menu.Selected == 5) TextInput.Init("Import");
				if(Menu.Selected == 6) Warning.init("Exit", "Confirm on exit?", Warning.YesCancel);
				
				Menu.Opened = false;
			}
			
			return true;
		}
		
		
		if(k>=49 && k<=57)
			Hotbar.selectedSlot = (byte) (Byte.parseByte(((char)k)+"")-1);
		
		
		return false;
	}
	
	
	
	
	static boolean MouseInGUI(){
		return MouseInGUI(Controls.ax, Controls.ay);
	}
	static boolean MouseInGUI(int x, int y){
		return (((Hotbar.insideBox(x, y)) || 
				Controls.MouseInRegion(x, y, Updatespeed.x, Updatespeed.y, Updatespeed.dx, Updatespeed.dy)||
				Controls.MouseInRegion(x, y, Menu.x, Menu.y, Menu.dx, Menu.dy)||
				Menu.Opened && Controls.MouseInRegion(x, y, Menu.x, Menu.y+Menu.dy, Menu.dy*2, Menu.dy*Menu.items.length/2)||
				(Warning.visible && Controls.MouseInRegion(Warning.x, Warning.y, Warning.dx, Warning.dy))||
				(TextInput.visible && Controls.MouseInRegion(x, y, TextInput.x, TextInput.y, TextInput.dx, TextInput.dy))));
	}
	static boolean HandCursor(){
		return (Controls.MouseInRegion(Hotbar.x, Hotbar.y, Hotbar.slotSize*Hotbar.IDinslot.length, Hotbar.slotSize) || 
				Controls.MouseInRegion(Updatespeed.x, Updatespeed.y, Updatespeed.dx, BarHeight)||
				Main.CloseCoord((int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position, Updatespeed.y+Updatespeed.dy/2, Updatespeed.BallSize/2) || Updatespeed.isMoving ||
				Controls.MouseInRegion(Menu.x, Menu.y, Menu.dx, Menu.dy)||
				(Warning.visible && (Controls.MouseInRegion(Warning.x, Warning.y, Warning.dx, BarHeight)||Warning.OnButton()))||
				(Menu.Opened && Controls.MouseInRegion(Menu.x, Menu.y+Menu.dy, Menu.dy*2, Menu.dy*Menu.items.length/2))||
				(TextInput.visible && Controls.MouseInRegion(TextInput.x, TextInput.y, TextInput.dx, BarHeight))||
				(TextInput.visible && Controls.MouseInRegion(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,8,TextInput.box.dy)));
	}
	static boolean TypeCursor(){
		return TextInput.visible && Controls.MouseInRegion(TextInput.x+TextInput.box.x+8,TextInput.y+TextInput.box.y,TextInput.box.dx-8,TextInput.box.dy);
	}
}

