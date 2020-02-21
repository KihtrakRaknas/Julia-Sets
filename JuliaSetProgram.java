import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
public class JuliaSetProgram extends JPanel implements AdjustmentListener{

	JFrame frame;
	JPanel scrollerPanel,checkPanel,bigSouthPanel;
	int aRange = 9999;
	int bRange = 9999;
	JScrollBar aBar;
	JScrollBar bBar;
	JScrollBar zoomBar;
	JScrollBar iterationsBar;
	GridLayout layout, boxLayout, leftLayout;
	float A = 0f;
	float B = 0f;
	float zoom = 1;
	float maxIteration = 300;
	float oldA = 0f;
	float oldB = 0f;
	float oldZoom = 1;
	float oldMaxIteration = 300;
	int renderScale = 1;
	boolean taskCalled = false;
	public JuliaSetProgram(){
		frame = new JFrame("Juila Set Program");
		frame.add(this);
		aBar = new JScrollBar(JScrollBar.HORIZONTAL, 0,0,-aRange,aRange);
		bBar = new JScrollBar(JScrollBar.HORIZONTAL, 0,0,-bRange,bRange);
		zoomBar = new JScrollBar(JScrollBar.HORIZONTAL, 500,0,0,1000);
		iterationsBar = new JScrollBar(JScrollBar.HORIZONTAL, 300,0,0,600);

		aBar.addAdjustmentListener(this);
		bBar.addAdjustmentListener(this);
		zoomBar.addAdjustmentListener(this);
		iterationsBar.addAdjustmentListener(this);

		aBar.setUnitIncrement(1);
		bBar.setUnitIncrement(1);
		zoomBar.setUnitIncrement(1);
		iterationsBar.setUnitIncrement(1);

		layout = new GridLayout(4,1);

		scrollerPanel = new JPanel();
		scrollerPanel.setLayout(layout);

		scrollerPanel.add(aBar);
		scrollerPanel.add(bBar);
		scrollerPanel.add(zoomBar);
		scrollerPanel.add(iterationsBar);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(layout);
		labelPanel.add(new JLabel("A Value"));
		labelPanel.add(new JLabel("B Value"));
		labelPanel.add(new JLabel("Zoom"));
		labelPanel.add(new JLabel("Iterations"));
		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BorderLayout());

		containerPanel.add(scrollerPanel, BorderLayout.CENTER);
		containerPanel.add(labelPanel, BorderLayout.WEST);
		frame.add(containerPanel, BorderLayout.SOUTH);


		frame.setSize(1000,800);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void adjustmentValueChanged(AdjustmentEvent e){
		if(e.getSource() == aBar){
			A = (float)aBar.getValue()/(aRange/2);
		}
		if(e.getSource() == bBar){
			B = (float)bBar.getValue()/(bRange/2);
		}
		if(e.getSource() == zoomBar){
			zoom = (float)zoomBar.getValue()/500;
			zoom = (float)Math.pow(zoom,100);
		}
		if(e.getSource() == iterationsBar){
			maxIteration = (float)iterationsBar.getValue();
		}
		if(oldA==A&&oldB==B&&oldZoom==zoom&&oldMaxIteration==maxIteration){
			if(renderScale>1){
				renderScale--;
				repaint();
				if(renderScale>1&&taskCalled==false){
					taskCalled = true;
					new java.util.Timer().schedule(
							new java.util.TimerTask() {
								@Override
								public void run() {
									taskCalled = false;
									adjustmentValueChanged(e);
								}
							},
							25
					);
				}
			}
		}else{
			renderScale = 10;
			repaint();
			if(taskCalled==false){
				taskCalled = true;
					new java.util.Timer().schedule(
							new java.util.TimerTask() {
								@Override
								public void run() {
									taskCalled = false;
									adjustmentValueChanged(e);
								}
							},
							25
					);
				}
			}
		oldA = A;
		oldB = B;
		oldZoom = zoom;
		oldMaxIteration = maxIteration;
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		BufferedImage image = new BufferedImage(frame.getWidth(),(frame.getHeight() - 60), BufferedImage.TYPE_INT_RGB);
		for(int horizonalPixel = 0; horizonalPixel<frame.getWidth()-renderScale+1;horizonalPixel+=renderScale)
			for(int verticalPixel = 0; verticalPixel<(frame.getHeight()-renderScale+1 - 60);verticalPixel+=renderScale){
				float iteration = maxIteration;
				double zx = 1.5 * (horizonalPixel-frame.getWidth()/2)/(.5*zoom*frame.getWidth());
				double zy = (verticalPixel-(frame.getHeight() - 60)/2)/(.5*zoom*(frame.getHeight() - 60));
				while(zx*zx+zy*zy<6 && iteration>0){
					double test = zy*zy-zx*zx +A;
					zy = 2*zx*zy + B;
					zx = test;
					iteration--;
				}

				int c;
				if(iteration>0)
					c = Color.HSBtoRGB((maxIteration/iteration)%1,1,1);
				else c = Color.HSBtoRGB(maxIteration/iteration,1,0);
				//image.setRGB(horizonalPixel,verticalPixel,c);
				for(int ix = 0; ix< renderScale; ix++){
					for(int iy = 0; iy< renderScale; iy++){
						//if(horizonalPixel+ix<frame.getWidth() && verticalPixel+iy<frame.getHeight())
						try{
							image.setRGB(horizonalPixel+ix,verticalPixel+iy,c);
						}catch(Exception e){
							System.out.println(""+horizonalPixel+" "+ix+"e"+verticalPixel+" "+iy);
						}
					}
				}
			}
		g.drawImage(image,0,0,null);
		/*g.setColor(new Color(redValue,greenValue,blueValue));
		g.fillRect(0,0,frame.getWidth(),(frame.getHeight() - 60));*/
	}
	public static void main(String[] args)
	{
		JuliaSetProgram app = new JuliaSetProgram();
	}

	}