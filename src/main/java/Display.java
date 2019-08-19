package main.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import main.java.GUI.Debug;
import main.java.GUI.Hotbar;
import main.java.GUI.Menu;
import main.java.GUI.SmallNotification;
import main.java.GUI.TextInput;
import main.java.GUI.Updatespeed;
import main.java.GUI.Warning;
import main.java.GUI.Warning.Button;
import main.java.Utilities.Lamp;
import main.java.Utilities.Lever;
public class Display {	
	static int GridSize = Main.GridSize;	
	static int GridSize10 = GridSize/10;
	static int GridSize8t = GridSize-2*GridSize10;
	static int GridSize2t = 2*GridSize10;
	static int GridSize5 = GridSize/5;
	static int GridSize2 = GridSize/2;
	
	
	static int outer = (int)(GridSize*0.05), outerd = (int) (GridSize*.9);
	static int inner = (int)(GridSize*0.15), innerd = (int) (GridSize*.7);
	static int ChunkSize;
	
	static boolean dispisOn;
	static boolean dispNumber;
	static boolean dispDots = true;
	static boolean dispDebug = true;
	
	static int lines;
	
	static boolean UpdateFieldImage = true;
	
	static JComponent bgfieldLayer;
	static JComponent fieldLayer;
	static JComponent fieldLinesLayer;
	static JComponent fieldOverlayLayer;
	static JComponent fieldGUILayer;

	static BufferedImage bgfieldImg;
	static BufferedImage fieldImg;
	static BufferedImage fieldLinesImg;
	static BufferedImage fieldOverlayImg;
	static BufferedImage fieldGUIImg;
	
	static Graphics2D bgfield;
	static Graphics2D field;
	static Graphics2D fieldLines;
	static Graphics2D fieldOverlay;
	static Graphics2D fieldGUI;
	
	
	static BufferedImage SCREEN;
	static Graphics2D g;
	
	
	static void UpdateDotImage(){

		
//		int temp = GridSize;
//		GridSize = Math.min(GridSize, 100);
//		outer = (int)(GridSize*0.05); 
//		outerd = GridSize-(int)(GridSize*0.1);			
//		inner = (int)(GridSize*0.15); 
//		innerd = GridSize-(int)(GridSize*0.3);
		
		for(int i = 0; i < 32; i++)
			DotImg[i] = new BufferedImage(GridSize, GridSize, BufferedImage.TYPE_INT_RGB);
		
		final Color[] Index = {
				new Color(80,80,80), //Idle
				new Color(100,100,100), //Hov
				new Color(140,140,140), //Sel
				new Color(120,120,120), //Mov
		};
		final Color[] InvisOn = {
				new Color(0,0,0,0), //Dot off
				new Color(0,128,128), //Inv off
				new Color(0,255,255,80), //Dot on
				new Color(0,255,255), //Inv on
		};
		
		
		for(int bg = 0; bg < 32; bg+=16){
			Color bgc = bg==0?new Color(128,128,128):new Color(125,125,125);
			for(int i = 0; i < 16; i+=4)
				for(int on = 0; on < 4; on+=2){
					drawDot(0, 0, Color.black, InvisOn[on+0], Index[i/4], bgc, DotImg[bg+i+on+0].getGraphics());
					drawDot(0, 0, Color.black, Index[i/4], InvisOn[on+1], bgc, DotImg[bg+i+on+1].getGraphics());
				}
		}
		
		for(int i = 0; i < 32; i++)
			DotImg[i] = toCompatibleImage(DotImg[i]);
		
	}
	static void UpdateSmlImage(){
		for(int i = 0; i < 64; i++)
			SmlImg[i] = new BufferedImage(GridSize, GridSize, BufferedImage.TYPE_INT_RGB);
		
		final Color[] Index = {
				new Color(80,80,80), //Idle
				new Color(100,100,100), //Hov
				new Color(140,140,140), //Sel
				new Color(120,120,120), //Mov
		};
		final Color[] isOn = {
				new Color(80,80,80), //Dot off
				new Color(64,128,128), //Dot on
		};
		
		for(int bg = 0; bg < 64; bg+=32){
			Color bgc = bg==0?new Color(128,128,128):new Color(125,125,125);
			for(int s = 0; s < 32; s+=8)
				for(int i = 0; i < 8; i+=2)
					for(int on = 0; on < 2; on++){
						Graphics temp = SmlImg[bg+s+i+on].getGraphics();
						temp.setColor(bgc);
						temp.fillRect(0, 0, GridSize, GridSize);
						drawSmallDotinPosition(0, 0, GridSize, GridSize, Color.black, Index[i/2], isOn[on], s/8, temp);
						temp.dispose();
					}
		}
		
		for(int i = 0; i < 64; i++)
			SmlImg[i] = toCompatibleImage(SmlImg[i]);
	}
	static BufferedImage toCompatibleImage(BufferedImage image){
	    // obtain the current system graphical settings
	    GraphicsConfiguration gfx_config = GraphicsEnvironment.
	        getLocalGraphicsEnvironment().getDefaultScreenDevice().
	        getDefaultConfiguration();

	    /*
	     * if image is already compatible and optimized for current system 
	     * settings, simply return it
	     */
	    
	    if (image.getColorModel().equals(gfx_config.getColorModel()))
	        return image;
	    System.out.println("Optimizze");
	    // image is not optimized, so create a new image that is
	    BufferedImage new_image = gfx_config.createCompatibleImage(
	            image.getWidth(), image.getHeight(), image.getTransparency());

	    // get the graphics context of the new image to draw the old image on
	    Graphics2D g2d = (Graphics2D) new_image.getGraphics();

	    // actually draw the image and dispose of context no longer needed
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();

	    // return the new optimized image
	    return new_image; 
	}
	
	static void UpdatebgField(){
		ChunkSize = GridSize*8;
		Tile.xchunks = 1+(int) Math.ceil(.5*Main.WIDTH/ChunkSize);
		Tile.ychunks = 1+(int) Math.ceil(.5*Main.HEIGHT/ChunkSize);
		
		bgfieldImg = new BufferedImage(Tile.xchunks*ChunkSize*2,Tile.ychunks*ChunkSize*2,BufferedImage.TYPE_INT_RGB);
		Display.bgfieldImg = toCompatibleImage(bgfieldImg);
		if(bgfield != null)
			bgfield.dispose();
		bgfield = (Graphics2D) bgfieldImg.getGraphics();
		
		
		bgfield.setColor(new Color(128,128,128));
		bgfield.fillRect(0, 0, Tile.xchunks*ChunkSize*2, Tile.ychunks*ChunkSize*2);
		bgfield.setColor(new Color(125,125,125));
		boolean b = false;
		for(int gx = 0; gx <= Tile.xchunks*2; gx++)		
			for(int gy = 0; gy <= Tile.ychunks*2; gy++)
				if(b =!b)
					bgfield.fillRect(gx*ChunkSize, gy*ChunkSize, ChunkSize, ChunkSize);
//		
//		for(int gx = 0; gx <= Tile.xchunks*2; gx++)		
//			for(int gy = 0; gy <= Tile.ychunks*2; gy++)
//				bgfield.drawRect(gx*ChunkSize, gy*ChunkSize, ChunkSize, ChunkSize);
	}	
	static void UpdateField(){
		
		if(GridSize != Main.GridSize || UpdateFieldImage){
			GridSize = Main.GridSize;
			GridSize10 = GridSize/10;
			GridSize8t = GridSize-2*GridSize10;
			GridSize2t = 2*GridSize10;
			GridSize5 = GridSize/5;
			GridSize2 = GridSize/2;
			
			outer = (int)(GridSize*0.05); 
			outerd = (int) (GridSize*.9);
			inner = (int)(GridSize*0.15); 
			innerd = (int) (GridSize*.7);
			
			ChunkSize = GridSize*8;
			
			UpdateDotImage();
			UpdateSmlImage();
			UpdatebgField();
			
			UpdateFieldImage = false;
		}
		
		Tile.chunkx = (int) (Main.x-Math.floor(.5d*Main.x/ChunkSize+1)*ChunkSize*2);
		Tile.chunky = (int) (Main.y-Math.floor(.5d*Main.y/ChunkSize+1)*ChunkSize*2);
		
		Main.Framedistr.add();
		
		field.drawImage(bgfieldImg,Tile.chunkx,Tile.chunky,null);
		
		
		Main.Framedistr.add();
		
		
		if(dispDots){
			if(Main.ThreadedUpdate){
				Main.logic.terminateWait();
			}
			drawDots();
		}
		Main.Framedistr.add();
		if(dispDots){
			drawConnectedPoints();
			if(Main.ThreadedUpdate){
				Main.logic.start();
			}
		}
		Main.Framedistr.add();
		
		field.setColor(Color.black);
		field.drawLine(Main.x-1, -(Main.GridSize<<11)+Main.y-1 , Main.x-1, (Main.GridSize<<11)+Main.y-1);
		field.drawLine(-(Main.GridSize<<11)+Main.x-1, Main.y-1 , (Main.GridSize<<11)+Main.x-1, Main.y-1);
		field.setColor(Color.black);
		field.drawRect(-(Main.GridSize<<11)+Main.x-1, -(Main.GridSize<<11)+Main.y-1, Main.GridSize<<12, Main.GridSize<<12);
		
		field.drawRect(Main.x-1, +Main.y-1, Main.GridSize<<6, Main.GridSize<<6);
		field.drawRect(Main.x-1, +Main.y-1, Main.GridSize<<9, Main.GridSize<<9);
		
		drawLamps();
		drawLevers();
		
		Controls.UpdateField = false;
	}	
	static void drawScreen(){
		
		GridSize = (int)Main.GridSize;
		GridSize10 = GridSize/10;
		GridSize8t = GridSize-2*GridSize10;
		GridSize2t = 2*GridSize10;
		GridSize5 = GridSize/5;
		GridSize2 = GridSize/2;
		
		if(Main.LastFramedistr.waypoints[8] != 0){
			Debug.graphicsTimeDistr.setWaypoints(Main.LastFramedistr, 1000000000/30.0);
			Debug.graphicsTimeDistr.UpdatedrawGraph();
		}
		
		Debug.Updates.Add(0,Math.log10(Main.time+1));
		Debug.Updates.Add(1,Math.log10(Main.time2+1));
		Debug.Updates.Add(2,Math.log10(Main.LogicUpdatecount+1)+1);
		Debug.Updates.Add(3,Math.round(10000000000d/Main.FrameTime)/600d*5);
		Debug.Updates.Add(4,Math.log10(Main.skipped+1));
		Debug.Updates.UpdatedrawGraph();
		
		//if(Controls.UpdateChanged){
		//	drawDots();
		//	drawConnectedPoints();
		//	drawLamps();
		//}
		
		
		
		Main.Framedistr.add("General GUI");
		
		drawCursor();
		drawOverlay();
		
		
		drawDelaybar();
		drawMenu();
		drawHotbar();
		
		drawTextInput();	
		drawWarning();
		drawSmlNotif();
		
		Main.Framedistr.add("");
		
		if(dispDebug)
			drawDebug();
		
	}
	
	static void drawCursor(){
		g.setColor(Color.black);
		g.setFont(GUI.Updatespeed.f);
		if(Controls.ctrl)
			g.drawString(Controls.fx + "," + Controls.fy, Controls.cx+Main.x, Controls.cy+Main.y);
		
		if(!(Controls.sMouseInRegion(Hotbar.x, Hotbar.y, Hotbar.slotSize*Hotbar.IDinslot.length, Hotbar.slotSize) && 
				Hotbar.IDinslot[((Controls.asx-Hotbar.x)/Hotbar.slotSize)]==2) &&
				!GUI.MouseInGUI() && 
				!(Controls.dragged && GUI.MouseInGUI(Controls.asx, Controls.asy))){
			if(!Lever.isClose(Controls.fx, Controls.fy) && !Lamp.isClose(Controls.fx, Controls.fy)){
				g.drawRect(Main.fixCoords(Controls.x)+Main.x, Main.fixCoords(Controls.y)+Main.y, GridSize, GridSize);
			}
			if((Hotbar.IDinslot[Hotbar.selectedSlot] == 5 && !Controls.isAnyClose(Controls.fx, Controls.fy)) || 
					(Controls.sMouseInRegion(Hotbar.x, Hotbar.y, Hotbar.slotSize*Hotbar.IDinslot.length, Hotbar.slotSize) && Hotbar.IDinslot[((Controls.asx-Hotbar.x)/Hotbar.slotSize)]==5) ||
					(Controls.shift && Controls.MoveTile && Controls.amountSelected()>1 && (Dot.isClose(Controls.fsx, Controls.fsy) && Dot.Close.isSmall))){
				//System.out.println((Dot.isClose(Controls.sgx, Controls.sgy) && Dot.dot[Dot.Close].isSmall));
				drawCursorinPosition();
			}
		}
		
		
	}
	static void drawCursorinPosition(){
		g.setColor(new Color(255,255,255,32));
		byte s = Controls.Side();
		if(s == 0){
			for(int i = 0; i < GridSize2; i++)
				g.drawLine(Controls.cx+Main.x+i, Controls.cy+Main.y+i, Controls.cx+Main.x+GridSize-i, Controls.cy+Main.y+i);
			g.setColor(new Color(0,0,0));
			g.drawLine(Controls.cx+Main.x, Controls.cy+Main.y, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
			g.drawLine(Controls.cx+Main.x+GridSize, Controls.cy+Main.y, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
		}
		else if(s == 1){
			for(int i = 0; i < GridSize2; i++)
				g.drawLine(Controls.cx+Main.x+i, Controls.cy+Main.y+i, Controls.cx+Main.x+i, Controls.cy+Main.y+GridSize-i);
			g.setColor(new Color(0,0,0));
			g.drawLine(Controls.cx+Main.x, Controls.cy+Main.y, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
			g.drawLine(Controls.cx+Main.x, Controls.cy+Main.y+GridSize, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
		}
		else if(s == 2)	{
			for(int i = 0; i < GridSize2; i++)
				g.drawLine(Controls.cx+Main.x+i, Controls.cy+Main.y+GridSize-i, Controls.cx+Main.x+GridSize-i, Controls.cy+Main.y+GridSize-i);
			g.setColor(new Color(0,0,0));
			g.drawLine(Controls.cx+Main.x, Controls.cy+Main.y+GridSize, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
			g.drawLine(Controls.cx+Main.x+GridSize, Controls.cy+Main.y+GridSize, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
		}
		else if(s == 3){	
			for(int i = 0; i < GridSize2; i++)
				g.drawLine(Controls.cx+Main.x+GridSize-i, Controls.cy+Main.y+GridSize-i, Controls.cx+Main.x+GridSize-i, Controls.cy+Main.y+i);
			g.setColor(new Color(0,0,0));
			g.drawLine(Controls.cx+Main.x+GridSize, Controls.cy+Main.y+GridSize, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
			g.drawLine(Controls.cx+Main.x+GridSize, Controls.cy+Main.y, Controls.cx+Main.x+GridSize2, Controls.cy+Main.y+GridSize2);
		}
	}
	
	static void drawSmlNotif(){
		if(SmallNotification.visible){
			if(SmallNotification.Duration-System.nanoTime() < 3_500_000_000L && SmallNotification.Duration-System.nanoTime()>3_250_000_000L) 
				SmallNotification.fade = 200-(int) ((SmallNotification.Duration-System.nanoTime()-3_250_000_000L)/1000*200/250_000);
			
			if(SmallNotification.Duration-System.nanoTime() < 1_000_000_000L && SmallNotification.Duration-System.nanoTime()>0) 
				SmallNotification.fade = (int) ((SmallNotification.Duration-System.nanoTime())/1000*200/1_000_000);
			
			if(SmallNotification.Duration-System.nanoTime()<0)
				SmallNotification.visible = false;
	 
			g.setColor(new Color(32,32,32,SmallNotification.fade));
			g.fillRect(SmallNotification.x, SmallNotification.y, SmallNotification.dx, SmallNotification.dy);
			
			g.setColor(new Color(0,0,0,SmallNotification.fade));
			g.drawRect(SmallNotification.x, SmallNotification.y, SmallNotification.dx, SmallNotification.dy);
			g.setColor(new Color(255,255,255,SmallNotification.fade));
			g.setFont(SmallNotification.f);
			g.drawString(SmallNotification.text, SmallNotification.x+(-GUI.SmallNotification.text.length()*11+GUI.SmallNotification.dx)/2, SmallNotification.y+20);
		}
	}
	static void drawWarning(){
		if(!Warning.visible)
			return;
		g.setColor(new Color(32,32,32,225));
		g.fillRect(Warning.x, Warning.y, Warning.dx, Warning.dy);		
		
		g.setColor(Color.white);
		g.setFont(Warning.f);
		for(int l = 0; l*19 < Warning.Warning.length(); l++)
			g.drawString(Warning.Warning.substring(l*19,Math.min((l+1)*19, Warning.Warning.length())),Warning.x+34, Warning.y+45+l*20);
		
		g.setFont(GUI.Warning.bf);
		for(Button pointer : Warning.buttons){
			g.setColor(new Color(80,80,80));
			if(Warning.StartSel == pointer.num) 
				g.setColor(new Color(100,100,100));
			g.fillRect(pointer.x+Warning.x, pointer.y+Warning.y, pointer.dx, pointer.dy);
			g.setColor(Color.black);
			g.drawRect(pointer.x+Warning.x, pointer.y+Warning.y, pointer.dx, pointer.dy);		
			g.setColor(Color.white);		
			g.drawString(pointer.Text, pointer.x+Warning.x+(pointer.dx-pointer.Text.length()*12)/2, pointer.y+Warning.y+27);
			
		}
		g.setColor(new Color(0,0,0,128));
		g.fillRect(Warning.x, Warning.y, Warning.dx, GUI.BarHeight);
		if(Warning.Ontop){
			g.setColor(new Color(0,0,0,128));
			g.fillRect(Warning.x, Warning.y, Warning.dx-GUI.BarHeight, GUI.BarHeight);
			
			g.setColor(new Color(64,64,64));
			g.fillRect(Warning.x+Warning.dx-GUI.BarHeight, Warning.y, GUI.BarHeight, GUI.BarHeight);
			g.setColor(new Color(196,196,196));
			drawCross(Warning.x+Warning.dx-GUI.BarHeight, Warning.y+2, GUI.BarHeight-5, 3);
			
			
			g.setColor(Color.black);
			g.drawRect(Warning.x, Warning.y, Warning.dx-1, GUI.BarHeight-1);
		}
	}
	static void drawTextInput(){
		if(!TextInput.visible)
			return;
		g.setColor(new Color(32,32,32,225));		
		drawAround(TextInput.x,TextInput.y, TextInput.dx, TextInput.dy,
				  TextInput.box.x,TextInput.box.y,TextInput.box.dx,TextInput.box.dy);
		g.setColor(new Color(80,80,80));
		g.fillRect(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,TextInput.box.dx,TextInput.box.dy);
		g.setColor(Color.black);
		g.drawRect(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,TextInput.box.dx,TextInput.box.dy);
		g.setColor(new Color(0,0,0,128));
		g.fillRect(TextInput.x, TextInput.y, TextInput.dx, GUI.BarHeight);
		
		g.setColor(new Color(32,32,32));
		if(Controls.MouseInRegion(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,8,TextInput.box.dy))
			g.setColor(new Color(48,48,48));
		g.fillRect(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,8,TextInput.box.dy);
		g.setColor(Color.black);
		g.drawRect(TextInput.x+TextInput.box.x,TextInput.y+TextInput.box.y,8,TextInput.box.dy);
		g.setColor(Color.WHITE);
		g.setFont(TextInput.cf);
		g.drawString(TextInput.Text, TextInput.x+20, TextInput.y+50);
		g.drawString(TextInput.box.text, TextInput.x+TextInput.box.x+15, TextInput.y+TextInput.box.y+TextInput.box.dy-8);
		
		if(TextInput.box.Type)
		if(TextInput.box.Cursor == TextInput.box.SelTill)
			g.fillRect(TextInput.x+TextInput.box.x+TextInput.box.Cursor*18+16, TextInput.y+TextInput.box.y+5, 3, TextInput.box.dy-10);
		else
			if(TextInput.box.Cursor>TextInput.box.SelTill){
				g.fillRect(TextInput.x+TextInput.box.x+TextInput.box.SelTill*18+16, TextInput.y+TextInput.box.y+5, (TextInput.box.Cursor-TextInput.box.SelTill)*18, TextInput.box.dy-10);
				g.setColor(new Color(80,80,80));
				g.drawString(TextInput.box.text.substring(TextInput.box.SelTill,TextInput.box.Cursor), TextInput.x+TextInput.box.x+TextInput.box.SelTill*18+16, TextInput.y+TextInput.box.y+TextInput.box.dy-8);
			}
			else{
				g.fillRect(TextInput.x+TextInput.box.x+TextInput.box.Cursor*18+16, TextInput.y+TextInput.box.y+5, (TextInput.box.SelTill-TextInput.box.Cursor)*18, TextInput.box.dy-10);
				g.setColor(new Color(80,80,80));
				g.drawString(TextInput.box.text.substring(TextInput.box.Cursor,TextInput.box.SelTill), TextInput.x+TextInput.box.x+TextInput.box.Cursor*18+16, TextInput.y+TextInput.box.y+TextInput.box.dy-8);
			}
		
			
		if(TextInput.Ontop){
			g.setColor(new Color(0,0,0,128));
			g.fillRect(TextInput.x, TextInput.y, TextInput.dx-GUI.BarHeight, GUI.BarHeight);
			
			g.setColor(new Color(64,64,64));
			g.fillRect(TextInput.x+TextInput.dx-GUI.BarHeight, TextInput.y, GUI.BarHeight, GUI.BarHeight);
			g.setColor(new Color(196,196,196));
			drawCross(TextInput.x+TextInput.dx-GUI.BarHeight, TextInput.y+2, GUI.BarHeight-5, 3);
			
			
			g.setColor(new Color(0,0,0));
			g.drawRect(TextInput.x, TextInput.y, TextInput.dx-1, GUI.BarHeight-1);
			
			
			
		}	
	}
	static void drawMenu(){
		g.setColor(new Color(16,16,16,220));
		if(Menu.Ontop) g.setColor(new Color(32,32,32,196));
		g.fillRect(Menu.x, Menu.y, Menu.dx, Menu.dy);		
		g.setColor(new Color(255,255,255,192));
		g.setFont(Menu.f);
		g.drawString(Menu.Text,Menu.x+34, Menu.y+Menu.dy/2);	
		g.setColor(new Color(0,0,0,196));
		g.drawLine(Menu.x, Menu.y, Menu.x, Menu.y+Menu.dy-1);
		g.setFont(Menu.f);
		
		if(Menu.Opened)
		for(int i = 0; i < Menu.items.length; i++){
			if(Menu.Selected == i+1){
				g.setColor(new Color(32,32,32,196));
				g.fillRect(Menu.x, Menu.y+Menu.dy+Menu.dy*i/2, Menu.dy*2, Menu.dy/2+(i&1));
				g.setColor(Color.black);
				g.drawRect(Menu.x, Menu.y+Menu.dy+Menu.dy*i/2, Menu.dy*2, Menu.dy/2+(i&1));		
				g.setColor(new Color(255,255,255,196));
				g.setFont(Menu.sf);
				g.drawString(Menu.items[i].ShortCut, Menu.x+(Menu.dy*2-Menu.items[i].ShortCut.length()*6)-3, Menu.y+Menu.dy+Menu.dy*(i+1)/2-3);
				g.setFont(Menu.f);
			}
			else{
				g.setColor(new Color(24,24,24,220));
				g.fillRect(Menu.x, Menu.y+Menu.dy+Menu.dy*i/2, Menu.dx, Menu.dy/2+(i&1));
				g.setColor(Color.BLACK);
				g.drawLine(Menu.x,Menu.y+Menu.dy*(i+3)/2, Menu.dx-1, Menu.dy*(i+3)/2+Menu.y);
				g.setColor(new Color(192,192,192,128));
			}
			g.drawString(Menu.items[i].Name, Menu.x+10, Menu.y+Menu.dy+Menu.dy*i/2+20);
			
		}
	}
	static void drawDelaybar(){
		g.setColor(new Color(16,16,16,192));		
		g.fillRect(Updatespeed.x, Updatespeed.y, Updatespeed.dx, Updatespeed.dy);
		
		g.setColor(new Color(128,128,128));
		g.fillRect((int)(Updatespeed.x+Updatespeed.dx*0.15)-7, Updatespeed.y+Updatespeed.dy/2-6, 4, 12);
		g.setColor(Color.black);
		g.drawRect((int)(Updatespeed.x+Updatespeed.dx*0.15)-7, Updatespeed.y+Updatespeed.dy/2-6, 4, 12);
		
		g.setColor(new Color(128,128,128));
		g.fillRect((int)(Updatespeed.x+Updatespeed.dx*0.15)-3, Updatespeed.y+Updatespeed.dy/2-2, (int)(Updatespeed.dx*0.7), 4);
		g.setColor(Color.black);
		//g.drawRect((int)(Updatespeed.x+Updatespeed.dx*0.15), Updatespeed.y+Updatespeed.dy/2-2, (int)(Updatespeed.dx*0.7), 4);
		
		g.setColor(Color.darkGray);
		if(Main.CloseCoord((int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position, Updatespeed.y+Updatespeed.dy/2, Updatespeed.BallSize/2) || Updatespeed.isMoving)
			g.setColor(new Color(128,128,128));
		
		g.fillOval((int)(Updatespeed.x+Updatespeed.dx*0.15)-Updatespeed.BallSize/2+Updatespeed.position,
				(Updatespeed.y+Updatespeed.dy/2)-Updatespeed.BallSize/2,
				Updatespeed.BallSize,
				Updatespeed.BallSize);
		
		g.setColor(Color.black);
		g.drawOval((int)(Updatespeed.x+Updatespeed.dx*0.15)-Updatespeed.BallSize/2+Updatespeed.position,
				(Updatespeed.y+Updatespeed.dy/2)-Updatespeed.BallSize/2,
				Updatespeed.BallSize,
				Updatespeed.BallSize);
		
		if(Main.CloseCoord((int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position, Updatespeed.y+Updatespeed.dy/2, Updatespeed.BallSize/2) || Updatespeed.isMoving){
			g.setColor(new Color(16,16,16,192));
			int bx = (int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position+30,
			by = (Updatespeed.y+Updatespeed.dy/2)+10;
			
			if(Updatespeed.isMoving){
				bx = Controls.sx+Main.x+30;
				by = Controls.sy+Main.y+10;
			}			
			
			String s = (Math.round(Updatespeed.Formula()*100)/100.0+"Hz");
			if(s.endsWith(".0Hz"))
				s = s.substring(0, s.length()-4)+"Hz";
			if(s.equals("0.17Hz"))
				s = "0Hz";
			
			g.fillRect(bx, by, s.length()*10+10, 20);
			g.setColor(Color.BLACK);	
			g.drawRect(bx, by, s.length()*10+10, 20);
			g.drawLine((int)(Updatespeed.x+Updatespeed.dx*0.15)+Updatespeed.position,
					(Updatespeed.y+Updatespeed.dy/2), bx, by);
			
			g.setColor(new Color(192,192,192,192));
			g.setFont(Updatespeed.f);
			
			g.drawString(s,bx+9, by+15);	
		}
			
		if(Updatespeed.Ontop){
			g.setColor(new Color(0,0,0,128));
			g.fillRect(Updatespeed.x, Updatespeed.y, Updatespeed.dx, GUI.BarHeight);
			g.setColor(new Color(0,0,0));
			g.drawRect(Updatespeed.x, Updatespeed.y, Updatespeed.dx-1, GUI.BarHeight-1);
			g.setColor(new Color(192,192,192,192));
			g.setFont(Updatespeed.f);
			g.drawString(Updatespeed.Text,Updatespeed.x+20, Updatespeed.y+10);
		}	
	}
	static void drawHotbar(){
		for(int i = 0; i < Hotbar.IDinslot.length; i++){
			g.setColor(new Color(16,16,16,192));
			if(Hotbar.selectedSlot == i)
				g.setColor(new Color(96,96,96));
			drawAround(Hotbar.x+Hotbar.slotSize*i, Hotbar.y, Hotbar.slotSize, Hotbar.slotSize,
					   Hotbar.box, Hotbar.box, Hotbar.dbox, Hotbar.dbox);
			
			g.setColor(new Color(0,0,0,64));
			if(Hotbar.selectedSlot == i)
				g.setColor(new Color(128,128,128,64));
			g.fillRect(Hotbar.x+Hotbar.box+Hotbar.slotSize*i, Hotbar.y+Hotbar.box, Hotbar.dbox, Hotbar.dbox);
			
			g.setColor(Color.black);			
			g.drawRect(Hotbar.x,Hotbar.y,Hotbar.slotSize*Hotbar.IDinslot.length,Hotbar.slotSize);
			g.drawRect(Hotbar.x+Hotbar.box+Hotbar.slotSize*i, Hotbar.y+Hotbar.box, Hotbar.dbox, Hotbar.dbox);
			
			int x = Hotbar.x + (int)(Hotbar.slotSize*0.2) + Hotbar.slotSize * i, y = Hotbar.y + (int)(Hotbar.slotSize*0.2),
				box = (int)(Hotbar.slotSize*0.6);
			if(Hotbar.IDinslot[i] == 1){
				drawDotinPosition(x,y,box,box,
						new Color(20,20,20),
						new Color(80,80,80),
						new Color(80,80,80),g);
			}
			else if(Hotbar.IDinslot[i] == 2){
				drawLever(x+(int)(Hotbar.slotSize*0.15),y,box-(int)(Hotbar.slotSize*0.3),box,
						new Color(20,20,20),
						new Color(80,80,80),
						false,g);
			}
			else if(Hotbar.IDinslot[i] == 3){
				drawDotinPosition(x,y,box,box,
						new Color(20,20,20),
						new Color(80,80,80),
						new Color(128,128,0),g);
			}	
			else if(Hotbar.IDinslot[i] == 4){
				drawDotinPosition(x,y,box,box,
						new Color(20,20,20),
						new Color(80,80,80),
						new Color(0,255,255),g);
			}	
			else if(Hotbar.IDinslot[i] == 5){
				drawSmallDot(x,y,box,box,
						new Color(20,20,20),
						new Color(80,80,80),
						new Color(0,255,255),2,g);
			}	
		}
		
		if(Controls.ax > Hotbar.x && Controls.ay > Hotbar.y && Controls.ax < Hotbar.x+Hotbar.IDinslot.length*Hotbar.slotSize){
			byte s = (byte) ((Controls.x+Main.x-Hotbar.x)/Hotbar.slotSize);
			g.setColor(new Color(255,255,255,16));		
			drawAround(Hotbar.x+Hotbar.slotSize*s, Hotbar.y, Hotbar.slotSize, Hotbar.slotSize,
					   Hotbar.box, Hotbar.box, Hotbar.dbox, Hotbar.dbox);
		}
	}
	
	static void drawOverlay(){
		for(Lever pointer : Field.lever){
			Color C1 = new Color(0,0,0), 
			C2 = new Color(175,175,175);
			if(pointer.isSelected && Controls.MoveTile){
				if(Controls.MoveNotPossible)
					C2 = new Color(255,100,100);
				drawLever(pointer.drawnx, pointer.drawny, GridSize, GridSize*2, C1, C2, pointer.isOn,g);
			}
		}
		
		for(Dot pointer : Field.dot){
			Color C1 = new Color(0,0,0), C2, C3;
			
			g.setColor(new Color(0,255,0,8));
			if(pointer.Pasted)
				g.fillRect(pointer.drawnx, pointer.drawny, GridSize, GridSize);
			
			C2 = new Color(175,175,175);
			if(pointer.isSelected && Controls.MoveTile){
				if(Controls.MoveNotPossible)
					C2 = new Color(255,100,100);
				C3 = C2;
				if(pointer.isInvert){
					C3 = new Color(0,128,128);
					//if(pointer.isOn) C3 = new Color(0,255,255);
				}
				if(!pointer.isSmall) {
					drawDotinPosition(pointer.drawnx, pointer.drawny, GridSize, GridSize, C1, C2, C3,g);
					
				}
				else
					drawSmallDotinPosition(pointer.drawnx, pointer.drawny, GridSize, GridSize, C1, C2, C3, pointer.nSide,g);
			}
		}		
		for(Lamp pointer : Field.lamp){
			Color C1 = new Color(0,0,0), C2 = new Color(175,175,175), C3 = new Color(128,128,0);
			if(Controls.MoveNotPossible) C2 = new Color(255,100,100);
			if(pointer.isOn) C3 = Color.YELLOW;
			g.setColor(Color.black);
			//g.drawString(Integer.toBinaryString(Byte.toUnsignedInt(pointer.texture)) + "", pointer.drawx, pointer.drawy);
			
			//if(pointer.iSelected && Controls.Move){					
			//	drawDotinPosition(pointer.drawnix, pointer.drawniy, GridSize, GridSize, C1, C2, C2,g);
			//	g.setColor(C1);
			//	g.drawOval(pointer.drawnix+GridSize/2-GridSize/10, pointer.drawniy+GridSize/2-GridSize/10, GridSize/5, GridSize/5);
			//}
			
			if(pointer.isSelected && Controls.MoveTile) drawLamp(pointer.drawnx, pointer.drawny, GridSize, GridSize, C1, C2, C3, pointer.texture, g);
		}
		g.setColor(Color.BLACK);
		if(!Updatespeed.on)
			if(dispisOn && dispNumber)		
				for(Dot pointer : Field.dot)
					g.drawString(pointer.number+":"+(pointer.logic.on), pointer.drawx, pointer.drawy);
			else if(dispisOn)		
				for(Dot pointer : Field.dot)
					g.drawString((pointer.logic.on)+"", pointer.drawx, pointer.drawy);
			else if(dispNumber)		
				for(Dot pointer : Field.dot)
					g.drawString(pointer.number+"", pointer.drawx, pointer.drawy);
		
		//if(dispDebug)	
		////for(Dot pointer : Dot.dot)
		//	g.drawString(Arrays.toString(pointer.ConnectedTo), pointer.drawx, pointer.drawy);
		
		if(Menu.ClearAnimation-System.nanoTime() > 0){
			g.setColor(new Color(255,255,255,(int)((Menu.ClearAnimation-System.nanoTime())/1_000_000L)));
			g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		}
		
		
		if(Controls.GroupSelect){
			g.setColor(new Color(255,255,255,64));
			
			g.fillRect(Math.min(Controls.asx,Controls.ax),Math.min(Controls.asy,Controls.ay),
					   Math.abs(Controls.asx-Controls.ax),Math.abs(Controls.asy-Controls.ay));
			g.setColor(Color.DARK_GRAY);
			g.drawRect(Math.min(Controls.asx,Controls.ax),Math.min(Controls.asy,Controls.ay),
					   Math.abs(Controls.asx-Controls.ax),Math.abs(Controls.asy-Controls.ay));
		}
		
		
		if(Controls.sMouseInRegion(Hotbar.x, Hotbar.y, Hotbar.slotSize*Hotbar.IDinslot.length, Hotbar.slotSize) && !GUI.MouseInGUI()){
				int x = Controls.fx*Main.GridSize+Main.x;
				int y = Controls.fy*Main.GridSize+Main.y;
				Color C1 = new Color(0,0,0,128), C2 = new Color(150,150,150,128), C3 = new Color(C2.getRGB());				
				switch (Hotbar.IDinslot[((Controls.asx-Hotbar.x)/Hotbar.slotSize)]){
					case 4: 
						C3 = new Color(0,128,128);
						C2 = new Color(255,100,100);
					case 1:
						drawDotinPosition(x,y, GridSize, GridSize, C1, C2, C3,g);
					break;
					case 3: 	
						C3 = new Color(128,128,0,128);
						drawDotinPosition(x,y, GridSize, GridSize, C1, C2, C3,g);
					break;
					case 2:
						C3 = new Color(128,128,0);					
						drawLever(x,y, GridSize, GridSize*2, C1,C2, false, g);						
					break;
					
					case 5:
						drawSmallDotinPosition(x,y, GridSize, GridSize, C1, C2, C3, Controls.Side(),g);
					break;
				}
			
		}
		if(Controls.Connecting){
			g.setStroke(new BasicStroke((int)Math.ceil(GridSize/16.0)));
			for(Dot pointer : Field.dot){
				g.setColor(new Color(0,64,128));
				if(pointer.isOn && !pointer.isSmall)g.setColor(new Color(0,128,255));
				if(pointer.isSelected) 
					if(Controls.shift){
						g.drawLine(pointer.drawx + GridSize2,
								   pointer.drawy + GridSize2,
								   pointer.drawx-(Main.fixCoords(Controls.sx)-Controls.x),
								   pointer.drawy-(Main.fixCoords(Controls.sy)-Controls.y));
						
						g.setColor(new Color(32,32,32,100));
						g.fillOval(pointer.drawx-(Main.fixCoords(Controls.sx)-Controls.x)-2,
								   pointer.drawy-(Main.fixCoords(Controls.sy)-Controls.y)-2, 4, 4);
					}
					else g.drawLine(pointer.drawx + GridSize2, pointer.drawy + GridSize2, Controls.ax, Controls.ay);
			}
			g.setStroke(new BasicStroke(1));
		}
	}
	static void drawDebug(){
		g.setFont(GUI.Debug.f);
		
		
		if(Debug.graphicsTimeDistr.visible)
			Debug.graphicsTimeDistr.drawGraph(g);
		if(Debug.logicTimeDistr.visible)
			Debug.logicTimeDistr.drawGraph(g);
		if(Debug.MainTimeDistr.visible)
			Debug.MainTimeDistr.drawGraph(g);
		
		if(Debug.Updates.visible)
			Debug.Updates.drawGraph(g);
		
		
		
		g.drawString("FPS: " + Math.round(Main.ExactFPS*10d)/10d + " / " + Math.round(10000000000d/Main.FrameTime)/10d, 20, 100);
		g.drawString("RPS: " + Math.round(Main.ExactRPS*10d)/10d + " / " + Math.round(10000000000d/Main.RefreshTime)/10d, 20, 120);
		g.drawString("Upd: " + Math.round(Main.ExactUPS*10d)/10d + " / " + Main.LogicUpdatecount, 20, 140);
		g.drawString("draw: " + (Dot.drawsmllen + Dot.drawdotlen) +"/"+Field.dot.length, 20, 160);
		g.drawString(lines +"", 20, 180);
	}
	
	
	
	static void drawDots(){
		int i = 0;
		if(GUI.Hotbar.selectedSlot!=7)
		for(Dot pointer : Dot.drawdot){
			if(i++ == Dot.drawdotlen) break;
			field.drawImage(Display.DotImg[pointer.img], pointer.drawx, pointer.drawy, GridSize, GridSize, null);
		}
		i=0;
		if(GUI.Hotbar.selectedSlot!=8)
		for(Dot pointer : Dot.drawsml){
			if(i++ == Dot.drawsmllen) break;
			field.drawImage(Display.SmlImg[pointer.img], pointer.drawx, pointer.drawy, GridSize, GridSize, null);
		}
	}
	static void drawConnectedPoints(){
		lines = 0;
		if(GUI.Hotbar.selectedSlot == 6)
			return; //TODO debugging
		
		field.setStroke(new BasicStroke((int)Math.ceil(GridSize/16.0)));
		
		final Color off = new Color(0,64,128),
				    on = new Color(0,128,255);
		final int[] xoff = {
				(int)(GridSize/6.0*SmallDotx(0)),
				(int)(GridSize/6.0*SmallDotx(1)),
				(int)(GridSize/6.0*SmallDotx(2)),
				(int)(GridSize/6.0*SmallDotx(3))
			};
		final int[] yoff = {
				(int)(GridSize/6.0*SmallDoty(0)),
				(int)(GridSize/6.0*SmallDoty(1)),
				(int)(GridSize/6.0*SmallDoty(2)),
				(int)(GridSize/6.0*SmallDoty(3))
			};
		
		for(Dot pointer : Field.dot)
			if(!pointer.isSmall){
				field.setColor(pointer.isOn?on:off);
				int x = pointer.drawx + GridSize2;
				int y = pointer.drawy + GridSize2;
				for(Dot pointer2 : pointer.ConnectedTo){
					int x2 = pointer2.drawx;
					int y2 = pointer2.drawy;
					if(Math.min(x, x2) > Main.WIDTH || Math.min(y, y2) > Main.HEIGHT || Math.max(x, x2) < -GridSize || Math.max(y, y2) < -GridSize)
						continue;
					
					if(pointer2.isSmall)
						field.drawLine(x, y, x2+xoff[pointer2.Side], y2+yoff[pointer2.Side]);
					else
						if(pointer2.number > pointer.number || !pointer.isInvert || !pointer2.isInvert)
							field.drawLine(x, y, x2+GridSize2, y2+GridSize2);
						
					lines++;
				}
			}
		
		
		
		field.setStroke(new BasicStroke(1));
		
	}
	
	
	static void drawLevers(){
		for(Lever pointer : Field.lever){
			if(pointer.Active){
				int t = 255;
				if(pointer.isSelected && Controls.MoveTile)
					t = 64;
				Color C1 = new Color(0,0,0,t), C2 = new Color(100,100,100,t);
				if(pointer.isSelected)
					C2 = new Color(175,175,175,t);
				else if(pointer.x == Controls.fx && (pointer.y == Controls.fy || pointer.y+1 == Controls.fy))
					C2 = new Color(120,120,120,t);
				
				drawLever(pointer.drawx, pointer.drawy, GridSize, GridSize*2, C1,C2,pointer.isOn, field);
				
//				field.drawString(Arrays.toString(pointer.BoundTo), pointer.drawx, pointer.drawy+GridSize*2);
			}
		}
	}
	static void drawLamps(){
		for(Lamp pointer : Field.lamp){
			if(pointer.Active){
				Color C1, C2, C3;
				int t = 255;
				
				//int it = 255;
				
				if(pointer.isSelected && Controls.MoveTile)
					t = 128;
				//if(pointer.iSelected && Controls.Move)
				//	it = 128;
				C1 = new Color(0,0,0,t);
				C2 = new Color(100,100,100,t);
				
				if(pointer.isSelected)
					C2 = new Color(175,175,175,t);
				else if(pointer.x == Controls.fx && pointer.y == Controls.fy)
					C2 = new Color(120,120,120,t);
				
					
				C3 = new Color(128,128,0,t);				
				if(pointer.isOn)
					C3 = new Color(255,255,0,t);
				
				
				drawLamp(pointer.drawx, pointer.drawy, GridSize, GridSize, C1, C2, C3, pointer.texture, field);		
			}
		}
	}
	static void drawLever(int x, int y, int dx, int dy, Color C1, Color C2, boolean on, Graphics g){
		g.setColor(C2);
		g.fillRect(x,y,dx, dy);
		g.setColor(C1);
		g.drawRect(x,y,dx, dy);
			g.setColor(Color.RED);
		if(on)
			g.setColor(Color.GREEN);
		g.fillRect((int)(x+(dx*0.3)), (int)(y+(dy*0.15)), (int)(dx*0.4), (int)(dy*0.7));
		g.setColor(Color.darkGray);
		int offsy = (int)(y+(dy*0.15));
		if(on)
			offsy = (int)(y+(dy*.65));
		g.fillRect((int)(x+(dx*0.3)), offsy, (int)(dx*0.4), (int)(dy*0.2));
		
		g.setColor(C1);
		g.drawRect((int)(x+(dx*0.3)), (int)(y+(dy*0.15)), (int)(dx*0.4), (int)(dy*0.7));
		
	}
	
	
	static void drawDotinPosition(int x, int y, int dx, int dy, Color C1, Color C2, Color C3, Graphics g){
		g.setColor(C2);
		g.fillOval(x+(int)((double)dx*0.05), y+(int)((double)dy*0.05), dx-(int)((double)dx*0.1), dy-(int)((double)dy*0.1));
		g.setColor(C1);
		g.drawOval(x+(int)((double)dx*0.05), y+(int)((double)dy*0.05), dx-(int)((double)dx*0.1), dy-(int)((double)dy*0.1));
		if(C3 != C1){
			g.setColor(C3);
			g.fillOval(x+(int)(dx*0.15), y+(int)(dy*0.15), dx-(int)(dx*0.3), dy-(int)(dy*0.3));
		}
	}
	static void drawDot(int x, int y, Color outline, Color out, Color in, Color bg, Graphics g){
		g.setColor(bg);
		g.fillRect(0, 0, GridSize, GridSize);
		g.setColor(out);
		g.fillOval(x+outer, y+outer, outerd, outerd);
		g.setColor(outline);
		g.drawOval(x+outer, y+outer, outerd, outerd);
		if(in != out){
			g.setColor(in);
			g.fillOval(x+inner, y+inner, innerd, innerd);
		}
	}
	static void drawSmallDotinPosition(int x, int y, int dx, int dy, Color C1, Color C2, Color C3, int s, Graphics g){
		dx /= 3;
		dy /= 3;
		if(s == 0)
			x += dx;
		else if(s == 1)
			y += dy;
		else if(s == 2){
			y += dy*2;
			x += dx;
		}
		else if(s == 3){
			x += dx*2;
			y += dy;
		}
		drawSmallDot(x,y,dx,dy,C1,C2,C3,s,g);
	}
	static void drawSmallDot(int x, int y, int dx, int dy, Color C1, Color C2, Color C3, int s, Graphics g){
		g.setColor(C2);
		g.fillOval(x+(int)Math.round((double)dx*0.05), y+(int)Math.round((double)dy*0.05), dx-(int)Math.round((double)dx*0.1), dy-(int)Math.round((double)dy*0.1));
		g.setColor(C1);
		g.drawOval(x+(int)Math.round((double)dx*0.05), y+(int)Math.round((double)dy*0.05), dx-(int)Math.round((double)dx*0.1), dy-(int)Math.round((double)dy*0.1));
		
		int 
		x2 = x+(int)Math.round((double)dx*0.05),
		y2 = y+(int)Math.round((double)dy*0.05),
		dx2 = dx-(int)Math.round((double)dx*0.1),
		dy2 = dy-(int)Math.round((double)dy*0.1);
		
		if(s == 0){
			y2 = y;
			dy2 = dy/2;
		}
		else if(s == 1){
			x2 = x;
			dx2 = dx/2;
		}
		else if(s == 2){
			y2 = y + dy/2;
			dy2 = dy/2;
		}
		else if(s == 3){
			x2 = x + dx/2;
			dx2 = dx/2;
		}
		g.setColor(C1);
		g.drawRect(x2, y2, dx2, dy2);
		g.setColor(C3);
		if(s == 0)
			dy2++;
		else if(s == 1)
			dx2++;		
		else if(s == 2){
			dy2++;
			y2--;
		}
		else if(s == 3){
			dx2++;
			x2--;
		}
		g.fillRect(x2+1, y2+1, dx2-1, dy2-1);
	}
	static void drawLamp(int x, int y, int dx, int dy, Color C1, Color C2, Color C3, byte Texture, Graphics g){
		g.setColor(C2);
		g.fillRect(x, y, dx, dy);
		g.setColor(C3);
		g.fillRect(x+dx/5, y+dy/5, 3*dx/5, 3*dy/5);
		
		
		
		if((Texture & 0b1000_0000) != 0)	g.fillRect(x+dx/5, y, 3*dx/5, 4*dy/5);
		if((Texture & 0b0100_0000) != 0)	g.fillRect(x, y+dy/5, 4*dx/5, 3*dy/5);
		if((Texture & 0b0010_0000) != 0)	g.fillRect(x+dx/5, y+dy/5, 3*dx/5, 4*dy/5+1);
		if((Texture & 0b0001_0000) != 0)	g.fillRect(x+dx/5, y+dy/5, 4*dx/5+1, 3*dy/5);
		
		g.setColor(C1);		
		
		
		if((Texture & 0b1000) == 0)	g.drawLine(x, y, x+dx, y);
		if((Texture & 0b0100) == 0)	g.drawLine(x, y, x, y+dy);
		if((Texture & 0b0010) == 0)	g.drawLine(x, y+dy, x+dx, y+dy);
		if((Texture & 0b0001) == 0)	g.drawLine(x+dx, y, x+dx, y+dy);
		
		
	}
	
	
	static BufferedImage[] DotImg = new BufferedImage[32];	
	static BufferedImage[] SmlImg = new BufferedImage[64];
	
	static void drawCross(int x, int y, int height, int cwidth){
		for(int i = 0; i <= height; i++){
			g.drawLine(x+i, y+i, x+i+cwidth, y+i);
			g.drawLine(x+i, y+height-i, x+i+cwidth, y+height-i);
		}
	}
	static int SmallDotx(int s){
		if(s == 1)return 1;
		if(s == 3)return 5;
		return 3;
	}
	static int SmallDoty(int s){
		if(s == 0)return 1;
		if(s == 2)return 5;
		return 3;
	}
	static void drawAround(int x, int y, int dx, int dy, int sx, int sy, int sdx, int sdy){
		g.fillRect(x, y, sdx+sx, sy);
		g.fillRect(x+sx+sdx, y, dx-sx-sdx, dy);
		g.fillRect(x, y+sy, sx, dy-sy);
		g.fillRect(x+sx, y+sy+sdy, sdx, dy-sy-sdy);
	}
}
