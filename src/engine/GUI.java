package engine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GUI extends JFrame {

  public Engine engine;
  JButton nextCycleBtn;
  
  JPanel topRight;
  JPanel bottomRight;
  JPanel topLeft;
  JPanel bottomLeft;
  
  public static void main(String[] args){
	  GUI f = new GUI(new Engine());
	    f.setVisible(true);
	  }
  public GUI(Engine engine){
	  this.engine = engine;
    setTitle("Tomasulo");
    setSize(1400,800);
    setLocation(new Point(300,200));
    
    GridLayout glMain = new GridLayout(0,2);
    JPanel mainContent = new JPanel(glMain);
    
    BorderLayout bl = new BorderLayout();
    setLayout(bl);
    
    nextCycleBtn = new JButton("Next Cycle");
    nextCycleBtn.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			engine.runCycle();
			bottomRight.removeAll();
			topRight.removeAll();
			bottomLeft.removeAll();
			topLeft.removeAll();
			setUpBottomRight();
			setUpTopRight();
			setUpBottomLeft();
			setUpTopLeft();
			refresh();
		}
	});
    // START LEFT SIDE
    JPanel leftSide = new JPanel();
    mainContent.add(leftSide);
    leftSide.setLayout(new GridLayout(2,0));
    
    topLeft = new JPanel();
    bottomLeft = new JPanel(new GridLayout(2,0));
    leftSide.add(topLeft);
    leftSide.add(bottomLeft);
    
    setUpBottomLeft();
    setUpTopLeft();
    // END LEFT SIDE
    
    // START RIGHT SIDE
    
    JPanel rightSide = new JPanel();
    mainContent.add(rightSide);
    rightSide.setLayout(new GridLayout(2,0));
    
    topRight = new JPanel(new GridLayout(0,2));
    bottomRight = new JPanel(new GridLayout(2,0));
    rightSide.add(topRight);
    rightSide.add(bottomRight);
    
    setUpBottomRight();
    setUpTopRight();
    
    // END RIGHT SIDE
    
    add(mainContent, BorderLayout.CENTER);
    add(nextCycleBtn, BorderLayout.SOUTH);
    
    setResizable(false);

    initEvent();    
  }

  public void refresh() {
	  this.invalidate();
	  this.validate();
	  this.repaint();
  }
  private void setUpBottomRight() {
	  setUpMuls();
	  setUpAdds();
  }
  private void setUpTopRight() {
	  setUpRegfile();
	  setUpMem();
  }
  private void setUpBottomLeft() {
	  setUpLoads();
	  setUpStores();
  }
  private void setUpTopLeft() {
	  setUpInsts();
  }
  // RIGHT SETUP CONNECTON TO ENGINE
  private void setUpMuls() {
	  String[][] data = engine.getMuls();
	  String columns[] = {"Busy", "OP", "Vj", "Vk", "Qj", "Qk", "Rem-Time"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  JPanel MulContainer = new JPanel(new GridLayout(2,0));
	  JLabel label = new JLabel("Multiply RS");
	  label.setFont(new Font("Serif", Font.BOLD, 24));
	  MulContainer.add(label);
	  MulContainer.add(sp);
	  bottomRight.add(MulContainer);
  }

  private void setUpAdds() {
	  String[][] data = engine.getAdds();
	  String columns[] = {"Busy", "OP", "Vj", "Vk", "Qj", "Qk", "Rem-Time"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  JPanel addContainer = new JPanel(new GridLayout(2,0));
	  JLabel label = new JLabel("Addition RS");
	  label.setFont(new Font("Serif", Font.BOLD, 24));
	  addContainer.add(label);
	  addContainer.add(sp);
	  bottomRight.add(addContainer);
  }
  
  private void setUpRegfile() {
	  String[][] data = engine.getRegFile();
	  String columns[] = {"Value", "QI"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  topRight.add(sp);
  }
  
  private void setUpMem() {
	  String[][] data = engine.getMem();
	  String columns[] = {"Memory"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  topRight.add(sp);
  }
  // RIGHT SETUP CONNECTON TO ENGINE
  private void setUpLoads() {
	  String[][] data = engine.getLoads();
	  String columns[] = {"Busy", "OP", "A", "Rem-Time"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  JPanel loadContainer = new JPanel(new GridLayout(2,0));
	  JLabel label = new JLabel("Load RS");
	  label.setFont(new Font("Serif", Font.BOLD, 24));
	  loadContainer.add(label);
	  loadContainer.add(sp);
	  bottomLeft.add(loadContainer);
  }
  
  private void setUpInsts() {
	  String[][] data = engine.getInsts();
	  String columns[] = {"Instruction"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  topLeft.add(sp);
  }
  
  private void setUpStores() {
	  String[][] data = engine.getStores();
	  String columns[] = {"Busy", "OP", "Vj", "Qj", "A", "Rem-Time"};
	  
	  JTable j = new JTable(data, columns);
	  JScrollPane sp = new JScrollPane(j);
	  JPanel storeContainer = new JPanel(new GridLayout(2,0));
	  JLabel label = new JLabel("Store RS");
	  label.setFont(new Font("Serif", Font.BOLD, 24));
	  storeContainer.add(label);
	  storeContainer.add(sp);
	  bottomLeft.add(storeContainer);
  }
  
  private void initEvent(){
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e){
       System.exit(1);
      }
    });
  }
}